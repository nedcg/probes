package ca.probes;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

import java.util.Arrays;
import java.util.function.Supplier;

public class Server extends AbstractVerticle {

    private final Router router;

    public Server(Router router) {
        this.router = router;
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServer httpServer;
        httpServer = vertx.createHttpServer();

        httpServer = httpServer.requestHandler(router);

        httpServer
                .listen(config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) startPromise.complete();
                            else startPromise.fail(result.cause());
                        });
    }
}
