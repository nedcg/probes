package ca.probes.users.handler;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import static ca.probes.HandlerCommons.*;

public class HandlerUserGet implements Handler<RoutingContext> {
    @Override
    public void handle(RoutingContext ctx) {
        final String userId = getUserId(ctx);

        if (userId == null) {
            badInput(ctx);
            return;
        }

        Session session = ctx.session();
        JsonObject result = session.get(userId);

        ok(ctx, result.encode());
    }
}
