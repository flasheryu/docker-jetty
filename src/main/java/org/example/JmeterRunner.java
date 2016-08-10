package org.example;

import javax.servlet.AsyncContext;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.PullImageResultCallback;

public class JmeterRunner implements Runnable{
	AsyncContext ctx;
	
	JmeterRunner(AsyncContext ctx){ 
        this.ctx = ctx;
    }

	public void run() {
	    //3.0.0 is different from 3.0.1 by DockerClientConfig and DefaultDockerClientConfig types.
		DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder()
				.withDockerHost("tcp://42.62.101.83:2376")
				.withDockerTlsVerify(true)
				.withDockerCertPath("openssl")
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
		ctx.complete();
	 } 		
}
