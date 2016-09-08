package org.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;

import util.ResourceConfig;

public class SyncHelloWorld extends AbstractHandler
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
        
        Properties systemProperties = ResourceConfig.getSystemProperty();
        String host = systemProperties.getProperty("dockerhost");
    	String certpath = systemProperties.getProperty("certpath");
    	String testImage = systemProperties.getProperty("imagename");
    	
		Logger.getRootLogger().info("Running remote jmeter docker on host: "+host);
		StringBuilder sb = new StringBuilder();

	    String res = sb.append("<h1>Hello World, Jetty, automated build from github! Cool!!</h1>")
	    		.append("<h1>New test for Jenkins and docker build!</h1>").append("<h1>WOW!!</h1>").append("<h1>Integration DONE!!!</h1>")
	    		.append("<h1>Unlink from github again, and the new flasheryu/jetty repo!!</h1>")
	    		.append("<h1>Remote jmeter docker done!!! See the latest log in /log directory!!!</h1>")
	    		.toString();
		response.getWriter().println("Running jmeter remotely!!");

		 //3.0.0 is different from 3.0.1 by DockerClientConfig and DefaultDockerClientConfig types.
		DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
				.withDockerHost(host)
				.withDockerTlsVerify(true)
				.withDockerCertPath(certpath)
	            .withRegistryUrl("https://index.docker.io/v1/")
	            .build();
		DockerClient dockerClient = DockerClientBuilder.getInstance(config)
		  .build();
		
		Volume volume1 = new Volume("/log"); 
		
	    dockerClient.pullImageCmd(testImage).exec(new PullImageResultCallback()).awaitSuccess();
	
		CreateContainerResponse container = dockerClient.createContainerCmd(testImage)
				.withVolumes(volume1)
				.withBinds(new Bind("/var/log",volume1))
				   .exec();
	
		dockerClient.startContainerCmd(container.getId()).exec();
		Logger.getRootLogger().info("Completed running remote jmeter docker on host: "+ host +"!");
		response.getWriter().println(res);
    }
}