package ca.probes.locations;

import ca.probes.DefaultHandlers;
import ca.probes.Server;
import ca.probes.users.RouterUsers;
import ca.probes.users.handler.UserRegistration;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class ServerTest {

    private Vertx vertx;
    private int port;
    private final String host = "localhost";

    @Before
    public void setUp(TestContext context) throws Exception {
        vertx = Vertx.vertx();
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
        DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject().put("http.port", port));

        final Router router = Router.router(vertx);
        DefaultHandlers defaultHandlers = new DefaultHandlers(vertx);
        defaultHandlers.apply(router);

        router.mountSubRouter("/locations", new RouterLocations(vertx, "/").get())
                .mountSubRouter("/users", new RouterUsers(vertx, "/").get());

        vertx.deployVerticle(new Server(router),
                options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) throws Exception {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void shouldAddALocation(TestContext context) {
        final Async async = context.async();

        final JsonObject body = new JsonObject().put("lat", 0.0).put("lng", 0.0);

        WebClient.create(vertx)
                .put(port, host, "/locations")
                .putHeader("x-user-id", "sample")
                .sendJsonObject(body, result -> {
                    if (!result.failed()) {
                        HttpResponse<Buffer> response = result.result();
                        context.assertEquals(response.statusCode(), 200);
                    } else {
                        context.fail("http request failed - locations");
                    }
                    async.complete();
                });
    }

    @Test
    public void shouldReturnBadInputWhenNoBody(TestContext context) {
        final Async async = context.async();

        final JsonObject body = null;

        WebClient.create(vertx)
                .put(port, host, "/locations")
                .putHeader("x-user-id", "sample")
                .sendJsonObject(body, result -> {
                    if (!result.failed()) {
                        HttpResponse<Buffer> response = result.result();
                        context.assertEquals(response.statusCode(), 400);
                    } else {
                        context.fail("http request failed - locations");
                    }
                    async.complete();
                });
    }

    @Test
    public void shouldReturnBadInputWhenNoUserId(TestContext context) {
        final Async async = context.async();

        final JsonObject body = new JsonObject().put("lat", 0.0).put("lng", 0.0);

        WebClient.create(vertx)
                .put(port, host, "/locations")
                .sendJsonObject(body, result -> {
                    if (!result.failed()) {
                        HttpResponse<Buffer> response = result.result();
                        context.assertEquals(response.statusCode(), 400);
                    } else {
                        context.fail("http request failed - locations");
                    }
                    async.complete();
                });
    }

    @Test
    public void shouldReturnBadInputOnInvalidInput(TestContext context) {
        final Async async = context.async();

        final JsonObject body = new JsonObject().put("lat", -91.0).put("lng", 0.0);

        WebClient.create(vertx)
                .put(port, host, "/locations")
                .sendJsonObject(body, result -> {
                    if (!result.failed()) {
                        HttpResponse<Buffer> response = result.result();
                        context.assertEquals(response.statusCode(), 400);
                    } else {
                        context.fail("http request failed - locations");
                    }
                    async.complete();
                });
    }

    @Test
    public void shouldReturnSavedLocation(TestContext context) {
        final Async async = context.async();

        final JsonObject body = new JsonObject().put("lat", 90.0).put("lng", 180.0);

        // forward session
        final WebClient webClient = WebClientSession.create(WebClient.create(vertx));

        String userId = "sample";
        String requestURI = "/locations";

        Promise<HttpResponse<Buffer>> promise = Promise.promise();
        webClient.put(port, host, requestURI)
                .putHeader("x-user-id", userId)
                .sendJsonObject(body, promise);

        promise.future().onComplete(event -> {
            webClient.get(port, host, requestURI)
                    .putHeader("x-user-id", userId)
                    .send(result -> {
                        if (result.failed()) {
                            context.fail("http request failed - locations");
                        } else {
                            HttpResponse<Buffer> response = result.result();
                            context.assertEquals(response.statusCode(), 200);
                            JsonArray jsonBody = response.bodyAsJsonArray();
                            context.assertEquals(jsonBody.getDouble(0), 90.0);
                            context.assertEquals(jsonBody.getDouble(1), 180.0);
                        }
                        async.complete();
                    });
        });
    }

    @Test
    public void shouldReturnNotFoundWhenNoDataInSession(TestContext context) {
        final Async async = context.async();

        String userId = "sample";
        String requestURI = "/locations";

        WebClient.create(vertx)
                .get(port, host, requestURI)
                .putHeader("x-user-id", userId)
                .send(result -> {
                    if (result.failed()) {
                        context.fail("http request failed - locations");
                    } else {
                        HttpResponse<Buffer> response = result.result();
                        context.assertEquals(response.statusCode(), 404);
                    }
                    async.complete();
                });
    }

    @Test
    public void shouldSignupAUser(TestContext context) {
        final Async async = context.async();

        final UserRegistration body = new UserRegistration("eduardo@mail.com", "eduardo123");

        WebClient.create(vertx)
                .post(port, host, "/users")
                .putHeader("x-user-id", "sample")
                .sendJsonObject(JsonObject.mapFrom(body), result -> {
                    if (!result.failed()) {
                        HttpResponse<Buffer> response = result.result();
                        context.assertEquals(response.statusCode(), 200);
                        JsonObject responseBody = response.bodyAsJsonObject();
                        context.assertNotNull(responseBody.getString("id"));
                        context.assertNotNull(responseBody.getString("email"));
                        context.assertNotNull(responseBody.getString("password"));
                    } else {
                        context.fail("http request failed - users");
                    }
                    async.complete();
                });
    }

    @Test
    public void shouldGetUserInformation(TestContext context) {
        final Async async = context.async();

        final UserRegistration body = new UserRegistration("eduardo@mail.com", "eduardo123");

        // forward session
        final WebClient webClient = WebClientSession.create(WebClient.create(vertx));

        Promise<HttpResponse<Buffer>> promise = Promise.promise();
        webClient
                .post(port, host, "/users")
                .putHeader("x-user-id", "sample")
                .sendJsonObject(JsonObject.mapFrom(body), promise);

        promise.future().onComplete(postResult -> {
            webClient
                    .get(port, host, "/users")
                    .putHeader("x-user-id", postResult.result().bodyAsJsonObject().getString("id"))
                    .send(getResult -> {
                        if (!getResult.failed()) {
                            HttpResponse<Buffer> response = getResult.result();
                            context.assertEquals(response.statusCode(), 200);
                            JsonObject responseBody = response.bodyAsJsonObject();
                            context.assertNotNull(responseBody.getString("id"));
                            context.assertNotNull(responseBody.getString("email"));
                            context.assertNotNull(responseBody.getString("password"));
                        } else {
                            context.fail("http request failed - users");
                        }
                        async.complete();
                    });
        });
    }
}