package milestone4;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

//Class responsible for setting up the server with jersey
@SuppressWarnings("restriction")
public class NodeServer {
	private int port;
	
	public NodeServer(int port) {
		this.port = port;
	}
	
	//ResourceConfig defines where the resources (classes with tagged methods) responsible for HTTP requests are.
	//By sending the Node object to this class and registering it, the Node retains its information (ID, successor, predecessor etc.)
	public HttpServer createHttpServer(Node n) throws IOException {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(n);
        return JdkHttpServerFactory.createHttpServer(getURI(), resourceConfig);
    }
	
	private URI getURI() {
        URI uri = UriBuilder.fromUri("http://localhost/").port(port).build();
        return uri;
    }
	
}
