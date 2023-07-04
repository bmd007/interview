package wonderland.interview.risk.decision.application;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Application main entry point.
 */
public final class RiskDecisionApplication {

    public static void main(String[] args) throws Exception {
        Server jettyServer = createServer();
        jettyServer.start();
        jettyServer.join();
    }

    /**
     * Creates a jetty server.
     * Resources are loaded using the specified package.
     * Dependency injection is configured using the custom binder.
     */
    public static Server createServer() {
        Server server = new Server(8080);

        ResourceConfig config = new ResourceConfig()
            .register(new RiskDecisionBinder())
            .packages(true, "wonderland.interview.risk.decision");

        ServletContextHandler context = new ServletContextHandler(server, "/");
        context.addServlet(
            new ServletHolder(new ServletContainer(config)),
            "/*"
        );

        return server;
    }

}
