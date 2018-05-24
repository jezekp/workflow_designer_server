package cz.zcu.kiv.server;

import java.net.URI;
import java.net.URL;
import java.security.ProtectionDomain;

import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.core.UriBuilder;

import com.sun.javafx.tools.packager.Log;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

public class EmbeddedServer{

	private static final int SERVER_PORT = 8680;

	private Server server;
	public EmbeddedServer() {
	}

    public static void main(String[] args) throws Exception {
        new EmbeddedServer().startServer();
	}

    public void onServerStarted(){
    }

    public void startServer() throws Exception {

		URI baseUri = UriBuilder.fromUri("http://localhost").port(SERVER_PORT)
				.build();
		ResourceConfig config = new ResourceConfig(Workflow.class);
		config.register(Slf4jLog.class);
		config.register(MultiPartFeature.class);
		server = JettyHttpContainerFactory.createServer(baseUri, config,
				false);


		ContextHandler contextHandler = new ContextHandler("/rest");
		contextHandler.setHandler(server.getHandler());
		contextHandler.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, MultiPartFeature.class.getCanonicalName());
		ProtectionDomain protectionDomain = EmbeddedServer.class
				.getProtectionDomain();
		URL location = protectionDomain.getCodeSource().getLocation();

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });
		resourceHandler.setResourceBase(location.toExternalForm());
		HandlerCollection handlerCollection = new HandlerCollection();
		handlerCollection.setHandlers(new Handler[] { resourceHandler,
				contextHandler, new DefaultHandler() });
		server.setHandler(handlerCollection);
		server.addLifeCycleListener(new LifeCycle.Listener() {
            @Override
            public void lifeCycleStarting(LifeCycle lifeCycle) {
            }

            @Override
            public void lifeCycleStarted(LifeCycle lifeCycle) {
                onServerStarted();
            }

            @Override
            public void lifeCycleFailure(LifeCycle lifeCycle, Throwable throwable) {

            }

            @Override
            public void lifeCycleStopping(LifeCycle lifeCycle) {

            }

            @Override
            public void lifeCycleStopped(LifeCycle lifeCycle) {

            }
        });
		server.start();
		server.join();

	}

    public void stopServer() throws Exception {
	    server.stop();
    }
}