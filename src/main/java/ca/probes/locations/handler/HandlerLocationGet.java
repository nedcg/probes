package ca.probes.locations.handler;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import static ca.probes.HandlerCommons.*;

public class HandlerLocationGet implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        String userId = getUserId(ctx);

        Session session = ctx.session();
        JsonArray location = session.get(userId);

        if (location == null) {
            notFound(ctx);
            return;
        }

        ctx.response().putHeader("Content-Type", "application/json");
        ok(ctx, location.toString());
    }

}
