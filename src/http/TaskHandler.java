package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import managers.NotFoundException;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
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
            if (pathParts.length == 2) { // /tasks
                List<Task> tasks = taskManager.getAllTasks();
                sendText(exchange, gson.toJson(tasks), 200);
            } else if (pathParts.length == 3) { // /tasks/{id}
                int taskId = parseId(pathParts[2]);
                Task task = taskManager.getTaskById(taskId);
                sendText(exchange, gson.toJson(task), 200);
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8),
                    Task.class);

            if (task.getId() == 0) {
                taskManager.createTask(task);
                sendText(exchange, gson.toJson(task), 201);
            } else {
                taskManager.updateTask(task);
                sendText(exchange, gson.toJson(task), 200);
            }
        } catch (JsonSyntaxException e) {
            sendServerError(exchange, "Invalid JSON format");
        } catch (IllegalArgumentException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) { // /tasks
            taskManager.deleteAllTasks();
            sendText(exchange, "All tasks deleted", 200);
        } else if (pathParts.length == 3) { // /tasks/{id}
            int taskId = parseId(pathParts[2]);
            taskManager.deleteTask(taskId);
            sendText(exchange, "Task deleted", 200);
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
