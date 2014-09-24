package org.kevoree.nginx.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous interface used for remote procedure calls
 * @see com.google.gwt.user.client.rpc.AsyncCallback
 */
public interface FileSystemServiceAsync {
	
	/**
	 * Retrieves a list of file and directory information based on
	 * the file/directory specified
	 * @param file the file or directory to get the contents of
	 * @return a list of files and directories
	 */
	void getContents(FileWrapper file, AsyncCallback<FileWrapper[]> callback);
	
	/**
	 * Retrieves a list of file and directory information based on
	 * the path specified
	 * @param file the path to get the contents of
	 * @return a list of files and directories
	 */
    void getContents(String file, AsyncCallback<FileWrapper[]> callback);
    
    /**
	 * Deletes a file or directory 
	 * @param absoluteName the absolute path the file or directory
	 * @return true if and only if the file or directory is successfully deleted; false otherwise 
	 */
    void deleteFile(String absoluteName, AsyncCallback<Boolean> callback);
    
    /**
	 * Renames a file or directory 
	 * @param absoluteName the absolute path the file or directory
	 * @param newAbsoluteName the new name of the file or directory
	 * @return true if and only if the file or directory is successfully renamed; false otherwise 
	 */
    void renameFile(String absoluteName, String newAbsoluteName, AsyncCallback<Boolean> callback);
    
    /**
	 * Creates a directory 
	 * @param absoluteName the absolute path to the directory
	 * @return true if and only if the directory was successfully created; false otherwise 
	 */
    void mkDir(String absoluteName, AsyncCallback<Boolean> callback);

	void getFileContents(FileWrapper file, AsyncCallback<String> callback);

	void editandCommitFileContents(FileWrapper file, String content,
			AsyncCallback<Boolean> callback);
}
