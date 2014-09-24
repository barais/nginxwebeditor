package org.kevoree.nginx.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.HashMap;

/**
 * Asynchronous interface used for remote procedure calls
 * to retrieve properties for the client side application
 * @see com.google.gwt.user.client.rpc.AsyncCallback
 */
@RemoteServiceRelativePath("prop")
public interface PropertiesService extends RemoteService {

	/**
	 * Retrieves a map of properties for the client 
	 * @return a map of properties for the client
	 */
	HashMap<String, String> getProperties();

}
