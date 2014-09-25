package main;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.handlers.AuthenticationCallHandler;
import io.undertow.security.handlers.AuthenticationConstraintHandler;
import io.undertow.security.handlers.AuthenticationMechanismsHandler;
import io.undertow.security.handlers.SecurityInitialHandler;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.BasicAuthenticationMechanism;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.proxy.mod_cluster.ModClusterProxyTarget.ExistingSessionTarget;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.kevoree.nginx.server.DownloadServlet;
import org.kevoree.nginx.server.FileSystemServiceImpl;
import org.kevoree.nginx.server.PropertiesServiceImpl;

public class Main {

	public static final String MYAPP = "/";

	
	public static void main(final String[] args) {
		
		if (args.length < 2){
			System.out.println("please pass the user and the pass as an argument");
			System.exit(0);
		}
		final Map<String, char[]> users = new HashMap<String, char[]>(2);
        users.put(args[0], args[1].toCharArray());
        //users.put("userTwo", "passwordTwo".toCharArray());
        final IdentityManager identityManager = new MapIdentityManager(users);

		
		try {

			DeploymentInfo servletBuilder = deployment().setResourceManager(new FileResourceManager(new File("war"),100))
					.addWelcomePage("index.html")
					.setClassLoader(Main.class.getClassLoader())
					.setContextPath(MYAPP)
					.setDeploymentName("test.war")
					.addServlets(
							servlet("download", DownloadServlet.class)
									.addMapping("/dn"),
							servlet("properties", PropertiesServiceImpl.class)
									.addMapping("/gwtnginxeditor/prop"),
							servlet("fs", FileSystemServiceImpl.class)
									.addMapping("/gwtnginxeditor/fs"));

			DeploymentManager manager = defaultContainer().addDeployment(
					servletBuilder);
			manager.deploy();
			
			
			
			
			HttpHandler servletHandler = manager.start();
			servletHandler = addSecurity(servletHandler,identityManager);
			
			PathHandler path = Handlers.path(Handlers.redirect(MYAPP))
					.addPrefixPath(MYAPP, servletHandler);			
			Undertow server = Undertow.builder()
					.addHttpListener(8080, "localhost").setHandler(path)
					.build();
		        
			server.start();
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}
    private static HttpHandler addSecurity(final HttpHandler toWrap, final IdentityManager identityManager) {
        HttpHandler handler = toWrap;
        handler = new AuthenticationCallHandler(handler);
        handler = new AuthenticationConstraintHandler(handler);
        final List<AuthenticationMechanism> mechanisms = Collections.<AuthenticationMechanism>singletonList(new BasicAuthenticationMechanism("My Realm"));
        handler = new AuthenticationMechanismsHandler(handler, mechanisms);
        handler = new SecurityInitialHandler(AuthenticationMode.PRO_ACTIVE, identityManager, handler);
        return handler;
    }

}
