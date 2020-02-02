package ca.probes.users;

import ca.probes.users.handler.HandlerUserGet;
import ca.probes.users.handler.HandlerUserPost;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.util.function.Supplier;

public class RouterUsers implements Supplier<Router> {

    private final Vertx vertx;
    private final String rootPath;

    public RouterUsers(Vertx vertx, String rootPath) {
        this.vertx = vertx;
        this.rootPath = rootPath;
    }

    @Override
    public Router get() {
        Router router = Router.router(vertx);
        router.post(rootPath).handler(new HandlerUserPost());
        router.get(rootPath).handler(new HandlerUserGet());

        return router;
    }
}
