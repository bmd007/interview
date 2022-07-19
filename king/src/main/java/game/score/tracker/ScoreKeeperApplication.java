package game.score.tracker;

import com.sun.net.httpserver.HttpServer;
import game.score.tracker.resource.ScoreResource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ScoreKeeperApplication {

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8081), 0);
        httpServer.setExecutor(Executors.newScheduledThreadPool(200));
        httpServer.createContext("/", new ScoreResource());
        httpServer.start();
    }

}
