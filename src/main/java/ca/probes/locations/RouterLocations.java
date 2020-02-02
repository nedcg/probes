package ca.probes.locations;

import ca.probes.locations.handler.HandlerLocationGet;
import ca.probes.locations.handler.HandlerLocationPut;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.util.function.Supplier;

public class RouterLocations implements Supplier<Router> {

    private final Vertx vertx;
    private final String rootPath;

    public RouterLocations(Vertx vertx, String rootPath) {
        this.vertx = vertx;
        this.rootPath = rootPath;
    }

    @Override
    public Router get() {
        Router router = Router.router(vertx);

        router.put(rootPath).handler(new HandlerLocationPut());
        router.get(rootPath).handler(new HandlerLocationGet());

        return router;
    }
}
