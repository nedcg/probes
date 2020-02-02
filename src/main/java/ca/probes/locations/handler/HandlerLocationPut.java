package ca.probes.locations.handler;

import ca.probes.HandlerCommons;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.Arrays;

import static ca.probes.HandlerCommons.*;

public class HandlerLocationPut implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        String userId = getUserId(ctx);
        JsonObject latLng = ctx.getBodyAsJson();

        Double lat, lng;

        if (latLng == null || userId == null) {
            badInput(ctx);
            return;
        }

        try {
            lat = latLng.getDouble("lat");
            lng = latLng.getDouble("lng");

            if (lat < -90.0 || lat > 90.0 || lng < -180.0 || lng > 180.0) {
                badInput(ctx);
                return;
            }
        } catch (Exception e) {
            badInput(ctx);
            return;
        }

        Session session = ctx.session();
        session.put(userId, new JsonArray(Arrays.asList(lat, lng)));

        ok(ctx);
    }
}
