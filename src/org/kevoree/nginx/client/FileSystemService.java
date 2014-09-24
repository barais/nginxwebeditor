package org.kevoree.nginx.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The remote interface that the file manager communicates with to
 * retrieve local file/directory information.
 */
@RemoteServiceRelativePath("fs")
public interface FileSystemService extends RemoteService {

	/**
	 * Retrieves a list of file and directory information based on
	 * the file/directory specified
	 * @param file the file or directory to get the contents of
	 * @return a list of files and directories
	 */
	FileWrapper[] getContents(FileWrapper file);
	String getFileContents(FileWrapper file);
	boolean editandCommitFileContents(FileWrapper file,String content);

	
	/**
	 * Retrieves a list of file and directory information based on
	 * the path specified
	 * @param file the path to get the contents of
	 * @return a list of files and directories
	 */
	FileWrapper[] getContents(String file);
	
	/**
	 * Deletes a file or directory 
	 * @param absoluteName the absolute path the file or directory
	 * @return true if and only if the file or directory is successfully deleted; false otherwise 
	 */
    Boolean deleteFile(String absoluteName);
    
    /**
	 * Renames a file or directory 
	 * @param absoluteName the absolute path the file or directory
	 * @param newAbsoluteName the new name of the file or directory
	 * @return true if and only if the file or directory is successfully renamed; false otherwise 
	 */
    Boolean renameFile(String absoluteName, String newAbsoluteName);
    
    /**
	 * Creates a directory 
	 * @param absoluteName the absolute path to the directory
	 * @return true if and only if the directory was successfully created; false otherwise 
	 */
    Boolean mkDir(String absoluteName);
}
