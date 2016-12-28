package com.oak.main;

import java.io.IOException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.HashSessionIdManager;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class OakStartUp {

	// private static final int DEFAULT_PORT = 8080;
	private static final int DEFAULT_PORT = 6767;
	private static final String CONTEXT_PATH = "/";
	private static final String CONFIG_LOCATION = "com.oak";
	private static final String MAPPING_URL = "/*";
	private static final String DEFAULT_PROFILE = "dev";

	public static void main(String[] args) throws Exception {
		Server server = new Server(DEFAULT_PORT);
		// Specify the Session ID Manager
        HashSessionIdManager idmanager = new HashSessionIdManager();
        server.setSessionIdManager(idmanager);
		server.setHandler(getServletContextHandler(getContext()));
		
		server.start();
		server.join();
	}

	private static ServletContextHandler getServletContextHandler(
			WebApplicationContext context) throws IOException {

		//To enable jetty sessions
		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		contextHandler.setErrorHandler(null);
		contextHandler.setContextPath(CONTEXT_PATH);
		contextHandler.addServlet(new ServletHolder(new DispatcherServlet(
				context)), MAPPING_URL);
		contextHandler.addEventListener(new ContextLoaderListener(context));		
				
		//Mandatory for Spring Security
		contextHandler.addFilter(new FilterHolder(new DelegatingFilterProxy("springSecurityFilterChain")), "/*", EnumSet.allOf(DispatcherType.class));
						
		return contextHandler;
	}

	private static WebApplicationContext getContext() {
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.setConfigLocation(CONFIG_LOCATION);
		context.getEnvironment().setDefaultProfiles(DEFAULT_PROFILE);	
		
		return context;
	}

}
