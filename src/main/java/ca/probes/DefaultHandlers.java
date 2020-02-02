package ca.probes;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

import java.util.function.Function;

public class DefaultHandlers implements Function<Router, Router> {

    private final Vertx vertx;

    public DefaultHandlers(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public Router apply(Router router) {
        router.route().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx, "ca.probes.session")));
        return router;
    }
}