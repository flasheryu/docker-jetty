package org.example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

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
        StringBuilder sb = new StringBuilder();
        String res = sb.append("<h1>Hello World, Jetty, automated build from github! Cool!!</h1>")
        		.append("<h1>New test for Jenkins and docker build!</h1>").append("<h1>WOW!!</h1>").append("<h1>Integration DONE!!!</h1>")
        		.append("<h1>Unlink from github again, and the new flasheryu/jetty repo!!</h1>")
        		.append("<h1>Remote jmeter docker done!!! See the latest log in /log directory!!!</h1>")
        		.toString();
        
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

		response.getWriter().println(res);
    }

    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new HelloWorld());
        

        server.start();
        server.join();
    }
}