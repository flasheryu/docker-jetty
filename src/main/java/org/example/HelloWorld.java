package org.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;

public class HelloWorld extends AbstractHandler
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
        
        //3.0.0 is different from 3.0.1 by DockerClientConfig and DefaultDockerClientConfig types.
		DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder()
				.withDockerHost("tcp://42.62.101.83:2375")
                .withRegistryUrl("https://index.docker.io/v1/").build();
		DockerClient dockerClient = DockerClientBuilder.getInstance(config)
		  .build();
		
		Volume volume1 = new Volume("/tmp"); 
		
		String testImage = "flasheryu/jmeter";
        dockerClient.pullImageCmd(testImage).exec(new PullImageResultCallback()).awaitSuccess();

		CreateContainerResponse container = dockerClient.createContainerCmd(testImage)
				.withVolumes(volume1)
				.withBinds(new Bind("/log",volume1))
				   .exec();

		dockerClient.startContainerCmd(container.getId()).exec();

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
        
        ContextHandler contextJmeter = new ContextHandler("/hijmeter");
        contextJmeter.setHandler(new HelloWorld());

//        server.setHandler(new HelloWorld());
//        server.setHandler( context );

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[] { context, contextFR, contextDE, contextJmeter});

        server.setHandler(contexts);

        server.start();
        server.join();
    }
}