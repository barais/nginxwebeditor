package org.kevoree.nginx.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous interface used for remote procedure calls
 * to retrieve properties for the client side application
 * @see com.google.gwt.user.client.rpc.AsyncCallback
 */
public interface PropertiesServiceAsync {
	
	/**
	 * Retrieves a map of properties for the client 
	 * @return a map of properties for the client
	 */
	void getProperties(AsyncCallback<HashMap<String, String>> callback);

}
