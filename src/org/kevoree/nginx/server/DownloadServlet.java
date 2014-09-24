package org.kevoree.nginx.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The servlet used to download files from the server.
 */
public class DownloadServlet extends HttpServlet {
		
	private static final long serialVersionUID = 1L;
	private static final String PROPERTIES_FILE = "/WEB-INF/classes/filemanager.properties";
	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadServlet.class);
	private static final String FILE_SEPERATOR = System.getProperty("file.separator");
	private static boolean restrict = false;
	private static String rootDir = "c:\\myfiles\\root1";
	private static String rootDir2 = "c:\\myfiles\\root2";
	private static String validHost = "127.0.0.1"; 	// accept requests from this address 127.0.0.1
													// changed in the filemanager.properties
	/**
	 * @see javax.servlet.http.HttpServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		Properties properties = new Properties();
		try {
			properties.load(getServletContext().getResourceAsStream(
					PROPERTIES_FILE));
		} catch (IOException ioe) {
			throw new ServletException(ioe);
		}
		rootDir = properties.getProperty("root.directory", "c:\\myfiles\\root1");
		rootDir2 = properties.getProperty("root2.directory", "c:\\myfiles\\root2");
		validHost = properties.getProperty("valid.host", "localhost");
		restrict = new Boolean(properties.getProperty("restrict", "true")).booleanValue();
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doPost(req, resp)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
								throws ServletException, IOException {
		super.doGet(req, resp);		
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(req, resp)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
								throws ServletException, IOException {
		String fileName = req.getParameter("fn");
		String host = req.getHeader("host");
		if(host == null)
			host = "";
		/*
		 * Causes issues with IE. It doesn't set the referrer
		 * must find another way to restrict access.
		 */
		String referrer = req.getHeader("referrer");
		if(referrer == null)
			referrer = "";
		// host and referrer have to be valid host or no deal
		if(restrict && (!host.equals(validHost) 
				|| !referrer.contains(validHost)))
			return;
		
		if(fileName == null)
			return;
		
		ServletOutputStream out;		
		FileInputStream in = null;
		
		try {			
			LOGGER.info("Download File " + fileName);
			fileName = URLDecoder.decode(fileName, "UTF-8");
			
			out = resp.getOutputStream();
			
			resp.setHeader("Content-Disposition", "attachment; filename=\"" + fileName.substring(fileName.lastIndexOf(FILE_SEPERATOR)+1) + "\"");
			resp.setHeader("Content-Transfer-Encoding", "binary");
			in = new FileInputStream(fileName);
			int length = 0;
			byte[] bytes = new byte[4096];			
			while((length = in.read(bytes)) > 0) {
				out.write(bytes, 0, length);
			}
			
		} catch(IOException e2) {
			LOGGER.error("Download Servlet - File manager");
			LOGGER.error(e2.getMessage());
		} finally {
			if(in != null)
				in.close();
		}
	}
}
