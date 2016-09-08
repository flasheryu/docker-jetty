package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
//import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  

import org.eclipse.jetty.continuation.Continuation;  
import org.eclipse.jetty.continuation.ContinuationSupport;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Volume;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.google.gson.Gson;

import Model.LoadCaseInfo;  

public class SimpleSuspendResumeServlet extends HttpServlet {  

  /** 
   *  
   */  
  private static final long serialVersionUID = 6112996063962978130L;  

  private MyAsyncHandler myAsyncHandler;  

  private String param = null; 
  
  private InputStream dockerhost = SimpleSuspendResumeServlet.class.getResourceAsStream("/resource.properties");
  private BufferedReader bf = new BufferedReader(new InputStreamReader(dockerhost));
  private static Properties systemProperties = new Properties();;

  public void init() throws ServletException {  

	  try {
		systemProperties.load(bf);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
      myAsyncHandler = new MyAsyncHandler() {  
          public void register(final MyHandler myHandler) {  
              new Thread(new Runnable() {  
                  public void run() {  
                	String host = systemProperties.getProperty("dockerhost");
                    Logger.getGlobal().info("Running remote jmeter docker on host: "+host);
                    
					 //3.0.0 is different from 3.0.1 by DockerClientConfig and DefaultDockerClientConfig types.
            		DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
							.withDockerHost(host)
							.withDockerTlsVerify(true)
							.withDockerCertPath("openssl")
//					        .withRegistryUrl("https://index.docker.io/v1/")
					        .build();
					
					DockerClient dockerClient = DockerClientBuilder.getInstance(config)
					  .build();
					
					Volume volume1 = new Volume("/log"); 
					
					String testImage = "flasheryu/jmeter";
                    Logger.getGlobal().info("Starting pulling!");
					dockerClient.pullImageCmd(testImage).exec(new PullImageResultCallback()).awaitSuccess();
             	
                    Logger.getGlobal().info("Starting creating!");
					CreateContainerResponse container = dockerClient.createContainerCmd(testImage)
							.withVolumes(volume1)
							.withBinds(new Bind("/var/log",volume1))
							.withCmd("/runload.sh", param)
							.exec();
					
                    Logger.getGlobal().info("Create DONE! Starting executing!");
                    Logger.getGlobal().info("Container Id is "+container.getId());
					dockerClient.startContainerCmd(container.getId()).exec();
					Logger.getGlobal().info("Completed running remote jmeter docker on host: "+host+"!");
					myHandler.onMyEvent("complete!");  
                  }  
              }).start();  
          }  
      };  

  }  

  public void doGet(HttpServletRequest request, HttpServletResponse response)  
          throws ServletException, IOException {  

	  final Continuation continuation = ContinuationSupport.getContinuation(request);  
      param = "hello-baidu";
      Logger.getGlobal().info("Param is "+param);
	  
      if (continuation.isInitial()) {  
            
          //request.setAttribute("results","null");  
          sendMyFirstResponse(response);  
          // suspend the request  
          continuation.suspend(); // always suspend before registration  

          // register with async service. The code here will depend on the  
          // the service used (see Jetty HttpClient for example)  
          myAsyncHandler.register(new MyHandler() {  
              public void onMyEvent(Object result) {  
                  continuation.setAttribute("results", result);  
                    
                  continuation.resume();  
              }  
          });  
          return; // or continuation.undispatch();  
      }  

      if (continuation.isExpired()) {  
          sendMyTimeoutResponse(response);  
          return;  
      }  
       //Send the results  
      Object results = request.getAttribute("results");  
      if(results==null){  
          response.getWriter().write("why reach here??");  
          continuation.resume();  
          return;  
      }  
      sendMyResultResponse(response, results);  
  }  

  public void doPost(HttpServletRequest request, HttpServletResponse response)  
          throws ServletException, IOException {  
      // if we need to get asynchronous results  
      //Object results = request.getAttribute("results");  
//      final PrintWriter writer = response.getWriter();  
      final Continuation continuation = ContinuationSupport.getContinuation(request);  

      BufferedReader br =request.getReader();
      
      StringBuffer stringBuffer = new StringBuffer();  
      String str = "";  
      while ((str = br.readLine()) != null) {  
          stringBuffer.append(str);  
      }  
      String info = stringBuffer.toString();  
      Gson gson = new Gson();  
      LoadCaseInfo loadcase = gson.fromJson(info, LoadCaseInfo.class);  
      param = loadcase.getLoadname();
      Logger.getGlobal().info("Param is "+param);

      //if (results == null) {  
      if (continuation.isInitial()) {  
            
          //request.setAttribute("results","null");  
          sendMyFirstResponse(response);  
          // suspend the request  
          continuation.suspend(); // always suspend before registration  

          // register with async service. The code here will depend on the  
          // the service used (see Jetty HttpClient for example)  
          myAsyncHandler.register(new MyHandler() {  
              public void onMyEvent(Object result) {  
                  continuation.setAttribute("results", result);  
                    
                  continuation.resume();  
              }  
          });  
          return; // or continuation.undispatch();  
      }  

      if (continuation.isExpired()) {  
          sendMyTimeoutResponse(response);  
          return;  
      }  
       //Send the results  
      Object results = request.getAttribute("results");  
      if(results==null){  
          response.getWriter().write("why reach here??");  
          continuation.resume();  
          return;  
      }  
      sendMyResultResponse(response, results);  
  }  
  
  private interface MyAsyncHandler {  
      public void register(MyHandler myHandler);  
  }  

  private interface MyHandler {  
      public void onMyEvent(Object result);  
  }  
    
  private void sendMyFirstResponse(HttpServletResponse response) throws IOException {  
      //���������һ�У�����flushҲû�ã�Ϊʲô��  
      response.setContentType("text/html");  
//      response.getWriter().write("starting...");  
      response.getWriter().println("starting...");  
      response.getWriter().flush();  

  }  

  private void sendMyResultResponse(HttpServletResponse response,  
          Object results) throws IOException {  
      //response.setContentType("text/html");  
      response.getWriter().println("results:" + results);  
      response.getWriter().flush();  

  }  

  private void sendMyTimeoutResponse(HttpServletResponse response)  
          throws IOException {  
      response.getWriter().write("timeout");  

  }  

}  