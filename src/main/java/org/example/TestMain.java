package org.example;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;

import util.Log4jConfig;
import util.ResourceConfig;


public class TestMain {
    public static void main(String[] args) throws Exception
    {
    	Log4jConfig.initialize();
  	  	ResourceConfig.initialize();

        Server server = new Server(8080);
        
        ContextHandler context = new ContextHandler("/");
        context.setContextPath("/");
        context.setHandler(new HelloHandler("Root Hello"));
        
        ContextHandler contextFR = new ContextHandler("/fr");
        contextFR.setHandler(new HelloHandler("Bonjour"));
        
        ContextHandler contextDE = new ContextHandler("/de");
        contextDE.setHandler(new HelloHandler("Gutten tag!"));
        
        ContextHandler contextJmeterSync = new ContextHandler("/hijmetersync");
        contextJmeterSync.setHandler(new SyncHelloWorld());

        ServletContextHandler contextServlet = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
      contextServlet.setContextPath("/hijmeterasync");
        contextServlet.addServlet(SimpleSuspendResumeServlet.class, "/");

        ContextHandlerCollection contexts = new ContextHandlerCollection();
//        contexts.setHandlers(new Handler[] { context, contextFR, contextDE, contextJmeterAsync, contextJmeterSync, contextServlet});
        contexts.setHandlers(new Handler[] { context, contextFR, contextDE, contextJmeterSync, contextServlet});
        server.setHandler(contexts);

        server.start();
        server.join();
    }
}
