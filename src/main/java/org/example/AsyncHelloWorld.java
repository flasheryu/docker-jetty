package org.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class AsyncHelloWorld extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
        throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        
		Logger.getGlobal().info("Running remote jmeter docker on host: 42.62.101.83...");
		StringBuilder sb = new StringBuilder();

	    String res = sb.append("<h1>Hello World, Jetty, automated build from github! Cool!!</h1>")
	    		.append("<h1>New test for Jenkins and docker build!</h1>").append("<h1>WOW!!</h1>").append("<h1>Integration DONE!!!</h1>")
	    		.append("<h1>Unlink from github again, and the new flasheryu/jetty repo!!</h1>")
	    		.append("<h1>Remote jmeter docker done!!! See the latest log in /log directory!!!</h1>")
	    		.toString();
		response.getWriter().println("Running jmeter remotely!!");

		AsyncContext ctx = request.startAsync();
		ctx.setTimeout(0);
		new Thread(new JmeterRunner(ctx)).start();
		Logger.getGlobal().info("Completed running remote jmeter docker on host: 42.62.101.83!");
		response.getWriter().println(res);
    }

    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        
        ContextHandler context = new ContextHandler("/");
        context.setContextPath("/");
        context.setHandler(new HelloHandler("Root Hello"));
        
        ContextHandler contextFR = new ContextHandler("/fr");
        contextFR.setHandler(new HelloHandler("Bonjour"));
        
        ContextHandler contextDE = new ContextHandler("/de");
        contextDE.setHandler(new HelloHandler("Gutten tag!"));
        
        ContextHandler contextJmeterAsync = new ContextHandler("/hijmeterasync");
        contextJmeterAsync.setHandler(new AsyncHelloWorld());
        
        ContextHandler contextJmeterSync = new ContextHandler("/hijmetersync");
        contextJmeterSync.setHandler(new SyncHelloWorld());

        ServletContextHandler contextServlet = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        contextServlet.setContextPath("/resume");
        contextServlet.addServlet(SimpleSuspendResumeServlet.class, "/");

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context, contextFR, contextDE, contextJmeterAsync, contextJmeterSync, contextServlet});
        server.setHandler(contexts);

        server.start();
        server.join();
    }
    
}