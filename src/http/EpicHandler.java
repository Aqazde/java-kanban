package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import managers.NotFoundException;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            if ("GET".equals(method)) {
                handleGet(exchange, pathParts);
            } else if ("POST".equals(method)) {
                handlePost(exchange);
            } else if ("DELETE".equals(method)) {
                handleDelete(exchange, pathParts);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            sendServerError(exchange, "Internal server error: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String[] pathParts) throws IOException {
        try {
            if (pathParts.length == 2) { // /epics
                List<Epic> epics = taskManager.getAllEpics();
                sendText(exchange, gson.toJson(epics), 200);
        } else if (pathParts.length == 3) { // /epics/{id}
            int epicId = parseId(pathParts[2]);
                Epic epic = taskManager.getEpicById(epicId);
                sendText(exchange, gson.toJson(epic), 200);
            } else if (pathParts.length == 4 && "subtasks".equals(pathParts[3])) { // /epics/{id}/subtasks
                int epicId = parseId(pathParts[2]);
                sendText(exchange, gson.toJson(taskManager.getSubtasksByEpic(epicId)), 200);
        } else {
            exchange.sendResponseHeaders(400, 0);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            Epic epic = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8),
                    Epic.class);

            if (epic.getId() == 0) {
                taskManager.createEpic(epic);
                sendText(exchange, gson.toJson(epic), 201);
            } else {
                taskManager.updateEpic(epic);
                sendText(exchange, gson.toJson(epic), 200);
            }
        } catch (JsonSyntaxException e) {
            sendServerError(exchange, "Invalid JSON format");
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) { // /epics/{id}
            int epicId = parseId(pathParts[2]);
            taskManager.deleteEpic(epicId);
            sendText(exchange, "Epic deleted", 200);
        } else {
            exchange.sendResponseHeaders(400, 0);
        }
    }

    private int parseId(String idString) throws IOException {
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid ID format");
        }
    }
}
