package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.DurationTypeAdapter;
import utils.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] responseBytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\": \"Resource not found\"}", 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendText(exchange, "{\"error\": \"Task time conflicts with existing tasks\"}", 406);
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        sendText(exchange, "{\"error\": \"" + message + "\"}", 500);
    }
}
