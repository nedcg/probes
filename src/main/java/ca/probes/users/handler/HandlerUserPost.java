package ca.probes.users.handler;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

import java.util.UUID;

import static ca.probes.HandlerCommons.badInput;
import static ca.probes.HandlerCommons.ok;

public class HandlerUserPost implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        final UserRegistration registrationData = ctx.getBodyAsJson().mapTo(UserRegistration.class);

        if (registrationData.getEmail() == null || registrationData.getEmail().isBlank() ||
                registrationData.getPassword() == null || registrationData.getPassword().isBlank()) {
            badInput(ctx);
            return;
        }

        UUID id = UUID.randomUUID();

        Session session = ctx.session();
        JsonObject createdUser = JsonObject.mapFrom(registrationData).put("id", id.toString());
        session.put(id.toString(), createdUser);

        ok(ctx, createdUser.encode());
    }

}