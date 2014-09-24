package org.kevoree.nginx.server;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.kevoree.nginx.client.PropertiesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The implementation of the PropertiesService that allows remote clients to access 
 * properties setup on the server
 *
 */
public class PropertiesServiceImpl extends RemoteServiceServlet implements PropertiesService {
	
	private static final long serialVersionUID = 1L;
	private static final String PROPERTIES_FILE = "/WEB-INF/classes/filemanager.properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesServiceImpl.class);
    
    /**
	 * Retrieves a map of properties for the client 
	 * @return a map of properties for the client
	 */
    public HashMap<String, String> getProperties() {
    	Properties properties = new Properties();
    	HashMap<String, String> table = new HashMap<String, String>();
    	
		try {
			properties.load(getServletContext().getResourceAsStream(
					PROPERTIES_FILE));
		} catch (Exception ioe) {
			LOGGER.error(ioe.getMessage() + ioe.toString());
		}
		
		for(@SuppressWarnings("unchecked")
		Enumeration<String> e = (Enumeration<String>) properties.propertyNames(); e.hasMoreElements(); ) {
			String key = (String) e.nextElement();
			String value = properties.getProperty(key);
			table.put(key, value);	
		}
		
		return table;
    }
    
}

/*
 * PropertiesServiceImpl.java
 * 
 * Copyright (c) Aug 2, 2012 North Orange County Community College District. 
 * All rights reserved.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE, ARE EXPRESSLY DISCLAIMED. IN NO EVENT SHALL
 * NORTH ORANGE COUNTY COMMUNITY COLLEGE DISTRICT OR ITS EMPLOYEES BE LIABLE FOR 
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED, THE COSTS OF PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED IN ADVANCE OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * Redistribution and use of this software in source or binary forms, with or
 * without modification, are permitted, provided that the following conditions
 * are met.
 * 
 * 1. Any redistribution must include the above copyright notice and disclaimer
 *    and this list of conditions in any related documentation and, if feasible, in
 *    the redistributed software.
 * 
 * 2. Any redistribution must include the acknowledgment, "This product includes
 *    software developed by North Orange County Community College District, in any 
 *    related documentation and, if feasible, in the redistributed software."
 * 
 * 3. The names "NOCCCD" and "North Orange County Community College District" must 
 *    not be used to endorse or promote products derived from this software.
 */
