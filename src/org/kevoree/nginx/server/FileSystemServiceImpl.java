package org.kevoree.nginx.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.StashCreateCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.StashApplyFailureException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.kevoree.nginx.client.FileSystemService;
import org.kevoree.nginx.client.FileWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The implementation of the FileSystemService that allows remote clients to
 * access local files and directories
 * 
 */
public class FileSystemServiceImpl extends RemoteServiceServlet implements
		FileSystemService {
	private static final long serialVersionUID = 1L;
	private static SimpleDateFormat dateFormater;
	private static final ThreadLocal<SimpleDateFormat> tSDF = new ThreadLocal<SimpleDateFormat>();
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FileSystemServiceImpl.class);

	static {
		dateFormater = new SimpleDateFormat("MMM d, yy");
	}

	/**
	 * Retrieves a list of file and directory information based on the
	 * file/directory specified
	 * 
	 * @param file
	 *            the file or directory to get the contents of
	 * @return a list of files and directories
	 */
	public FileWrapper[] getContents(FileWrapper file) {
		File fsFile = new File(file.getPath());
		if (fsFile.isDirectory()) {
			return this.buildFilesList(fsFile.listFiles());
		}
		return null;
	}

	/**
	 * Retrieves a list of file and directory information based on the path
	 * specified
	 * 
	 * @param file
	 *            the path to get the contents of
	 * @return a list of files and directories
	 */
	public FileWrapper[] getContents(String file) {
		return this.getContents(new FileWrapper(file));
	}

	/**
	 * Deletes a file or directory
	 * 
	 * @param absoluteName
	 *            the absolute path the file or directory
	 * @return true if and only if the file or directory is successfully
	 *         deleted; false otherwise
	 */
	public Boolean deleteFile(String absoluteName) {
		LOGGER.info("deleting : " + absoluteName);
		File f = new File(absoluteName);
		if (!f.exists())
			return false;
		if (f.isDirectory())
			return deleteRecursive(f);
		return f.delete();
	}

	/**
	 * Renames a file or directory
	 * 
	 * @param absoluteName
	 *            the absolute path the file or directory
	 * @param newAbsoluteName
	 *            the new name of the file or directory
	 * @return true if and only if the file or directory is successfully
	 *         renamed; false otherwise
	 */
	public Boolean renameFile(String absoluteName, String newAbsoluteName) {
		LOGGER.info("rename : " + absoluteName + " to " + newAbsoluteName);

		if (newAbsoluteName.endsWith("null"))
			return false;

		File f = new File(absoluteName);
		if (!f.exists())
			return false;
		return f.renameTo(new File(newAbsoluteName));
	}

	/**
	 * Creates a directory
	 * 
	 * @param absoluteName
	 *            the absolute path to the directory
	 * @return true if and only if the directory was successfully created; false
	 *         otherwise
	 */
	public Boolean mkDir(String absoluteName) {
		LOGGER.info("create : " + absoluteName);
		File f = new File(absoluteName);
		if (f.exists())
			return false;
		return f.mkdir();
	}

	/**
	 * By default File#delete fails for non-empty directories, it works like
	 * "rm". We need something a little more brutual - this does the equivalent
	 * of "rm -r"
	 * 
	 * @param path
	 *            Root File Path
	 * @return true if the file and all sub files/directories have been removed
	 */
	private boolean deleteRecursive(File path) {
		if (!path.exists())
			return false;
		boolean ret = true;
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}

	private FileWrapper[] buildFilesList(File[] files) {

		FileWrapper[] result = new FileWrapper[files.length];
		for (int i = 0; i < files.length; i++) {
			result[i] = new FileWrapper(files[i].getAbsolutePath(),
					files[i].getName(), dateFormat(files[i].lastModified()));
			if (files[i].isDirectory()) {
				result[i].setIsDirectory();
			}
		}
		Arrays.sort(result);
		return result;
	}

	private static String dateFormat(long dateLong) {
		SimpleDateFormat sdf = tSDF.get();
		if (sdf == null) {
			sdf = new SimpleDateFormat("MMM d, yy");
			tSDF.set(sdf);
		}
		return sdf.format(new Date(dateLong));
	}

	/**
	 * Guess whether given file is binary. Just checks for anything under 0x09.
	 */
	public boolean isBinaryFile(File f) throws FileNotFoundException,
			IOException {
		FileInputStream in = new FileInputStream(f);
		int size = in.available();
		if (size > 1024)
			size = 1024;
		byte[] data = new byte[size];
		in.read(data);
		in.close();

		int ascii = 0;
		int other = 0;

		for (int i = 0; i < data.length; i++) {
			byte b = data[i];
			if (b < 0x09)
				return true;

			if (b == 0x09 || b == 0x0A || b == 0x0C || b == 0x0D)
				ascii++;
			else if (b >= 0x20 && b <= 0x7E)
				ascii++;
			else
				other++;
		}

		if (other == 0)
			return false;

		return 100 * other / (ascii + other) > 95;
	}

	public String getFileContents(FileWrapper file) {
		File f = new File(file.getPath());
		try {
			if (!f.exists())
				return "no such file";
			else if (isBinaryFile(f))
				return "binary file";

			else {
				BufferedReader br = null;
				String sCurrentLine;
				br = new BufferedReader(new FileReader(f));
				StringBuffer bf = new StringBuffer();
				while ((sCurrentLine = br.readLine()) != null) {
					bf.append(sCurrentLine + "\n");
				}
				return bf.toString();
			}
		} catch (FileNotFoundException e) {
			return "cannot check if it is a binary file";
		} catch (IOException e) {
			return "cannot check if it is a binary file";
		}

	}

	public boolean editandCommitFileContents(FileWrapper file, String content) {
		File f = new File(file.getPath());
		try {
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(content.getBytes());
			fo.flush();
			fo.close();
			FileRepositoryBuilder builder = new FileRepositoryBuilder();
			Repository db = builder.setGitDir(new File("/etc/nginx/.git"))
					.readEnvironment() // scan environment GIT_* variables
					.findGitDir() // scan up the file system tree
					.build();
			Git git = new Git(db);
			if (this.restartNginx()) {
				AddCommand add = git.add();
				add.addFilepattern(file.getPath().replaceAll("/etc/nginx/", "")).call();
				CommitCommand commit = git.commit();
				commit.setMessage("new Version").call();
				git.close();
				return true;

			} else {
				System.err.println("call drop " + f.getAbsoluteFile().toString().replaceAll("/etc/nginx/", ""));
				StashCreateCommand stash = git.stashCreate();
				System.err.println(git.getRepository().getBranch());
				System.err.println(git.getRepository().getAllRefs().size());
				stash.call();
				 Collection<RevCommit> stashes = git.stashList().call();
			        for(RevCommit rev : stashes) {
			            System.out.println("Found stash: " + rev);
			        }
				 ObjectId call = git.stashDrop().setStashRef(0).call();
				git.close();
				 
				return false;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WrongRepositoryStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StashApplyFailureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private boolean restartNginx() {
		try {
			String line;
			Process p = Runtime.getRuntime().exec("sudo service nginx reload");
			BufferedReader bri = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader bre = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				System.err.println(line);
				if (line.contains("...done."))
					return true;
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				System.err.println(line);
				if (line.contains("...done."))
					return true;
			}
			bre.close();
			p.waitFor();
		} catch (Exception err) {
			err.printStackTrace();
		}
		return false;
	}

}
