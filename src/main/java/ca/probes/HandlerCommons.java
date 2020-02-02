package ca.probes;

import io.vertx.ext.web.RoutingContext;

public class HandlerCommons {

    public static String getUserId(RoutingContext ctx) {
        return ctx.request().getHeader("x-user-id");
    }

    public static void ok(RoutingContext ctx) {
        ok(ctx, null);
    }

    public static void ok(RoutingContext ctx, String body) {
        ctx.response().setStatusCode(200);
        if (body == null) {
            ctx.response().end();
        } else {
            ctx.response().end(body);
        }
    }

    public static void badInput(RoutingContext ctx) {
        ctx.response().setStatusCode(400);
        ctx.response().end();
    }

    public static void notFound(RoutingContext ctx) {
        ctx.response().setStatusCode(404);
        ctx.response().end();
    }
}
