package wonderland.interview.risk.decision;

import wonderland.interview.risk.decision.application.RiskDecisionApplication;
import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class JettyServerExtension implements BeforeAllCallback, AfterAllCallback {

    private final Server server = RiskDecisionApplication.createServer();

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        server.start();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (this.server.isRunning()) {
            server.stop();
        }
    }
}