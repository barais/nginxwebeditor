package org.kevoree.nginx.client;

import java.io.Serializable;
import java.lang.Comparable;

/**
 * Stores file information.
 *
 */
public class FileWrapper implements Serializable, Comparable<Object> {

	private static final long serialVersionUID = 1L;
	private String name;
    private String path;
    private FileType kind;
    private String modifiedAt;

    /**
     * Creates a file wrapper
     * @param path the path of the file/directory
     * @param name the name of the file/directory
     * @param modifiedAt the last modified date
     */
    public FileWrapper(String path, String name, String modifiedAt) {
        this.name = name;
        this.path = path;
        this.modifiedAt = modifiedAt;
        this.kind = this.getFileType(extractFileExtention(name));
    }

    /**
     * Creates a file wrapper
     * @param path the path of the file/directory
     * @param name the name of the file/directory
     */
    public FileWrapper(String path, String name) {
        this.name = name;
        this.path = path;
        this.kind = this.getFileType(extractFileExtention(name));
    }

    /**
     * Creates a file wrapper
     * @param path the path of the file/directory
     */
    public FileWrapper(String path) {
        this.name = "";
        this.path = path;
        this.kind = FileType.DIR;
    }

    /**
     * Creates a file wrapper
     */
    public FileWrapper() {
    	this.name = "";
        this.path = "";
        this.modifiedAt = "";
        this.kind = FileType.OTHER;
    }

    /**
     * Gets the file name
     * @return file name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the file path
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the modified date
     * @return the modified a date
     */
    public String getModified() {
        return this.modifiedAt;
    }

    /**
     * Gets the kind of file
     * @return kind of file
     * @see edu.nocccd.filemanager.client.FileType
     */
    public FileType getKind() {
        return kind;
    }

    /**
     * Sets the file wrapper to a directory type
     */
    public void setIsDirectory() {
        this.kind = FileType.DIR;
    }
    
    /**
     * Compares the FileWrapper names to see which is alphabetically earlier than
     * another.
     * @param o the fileWrapper to be compared
     * @return a negative integer, zero, or a positive integer as this FileWrapper's name 
     * 			is less than, equal to, or greater than the specified FileWrapper's. 
     */
    public int compareTo(Object o) {
    	if(this.name == null || ((FileWrapper)o).getName() == null)
    		return 0;
    	return this.name.compareTo(((FileWrapper)o).getName());
    }
    
    private static String extractFileExtention(String file) {
        int dot = file.lastIndexOf('.');
        return file.substring(dot + 1).toLowerCase();
    }

    private FileType getFileType(String ext) {
    	if(ext.equals("avi"))
        	return FileType.AVI;
    	if(ext.equals("doc") || ext.equals("docx") || ext.equals("wbk") || ext.equals("dot"))
        	return FileType.DOC;
    	if(ext.equals("xls") || ext.equals("xlsx"))
        	return FileType.XLSX;
    	if(ext.equals("html") || ext.equals("htm"))
        	return FileType.HTML;
    	if(ext.equals("mov"))
        	return FileType.MOV;
    	if(ext.equals("mp3") || ext.equals("mpeg"))
        	return FileType.MP3;
        if(ext.equals("pdf"))
            return FileType.PDF;
        if(ext.equals("ppt") || ext.equals("pptx"))
            return FileType.PPTX;
        if(ext.equals("wmv"))
            return FileType.WMV;
        if(ext.equals("gif") || ext.equals("jpg") || ext.equals("png") || ext.equals("tif") || ext.equals("psd"))
            return FileType.IMG;
        if(ext.equals("txt"))
        	return FileType.TXT;
        if(ext.equals("wpd") || ext.equals("wpt"))
        	return FileType.WPD;
        if(ext.equals("zip"))
        	return FileType.ZIP;
        return FileType.OTHER;
    }    
}
