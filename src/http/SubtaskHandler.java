package http;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import managers.NotFoundException;
import managers.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
            if (pathParts.length == 2) { // /subtasks
                List<Subtask> subtasks = taskManager.getAllSubtasks();
                sendText(exchange, gson.toJson(subtasks), 200);
            } else if (pathParts.length == 3) { // /subtasks/{id}
                int subtaskId = parseId(pathParts[2]);
                Subtask subtask = taskManager.getSubtaskById(subtaskId);
                sendText(exchange, gson.toJson(subtask), 200);
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        }
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        try {
            System.out.println("Получен POST запрос для создания Subtask");
            Subtask subtask = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), Subtask.class);

            System.out.println("Попытка создания подзадачи с Epic ID: " + subtask.getEpicId());

            if (subtask.getId() == 0) {
                taskManager.createSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 201);
                System.out.println("Подзадача успешно создана");
            } else {
                taskManager.updateSubtask(subtask);
                sendText(exchange, gson.toJson(subtask), 200);
                System.out.println("Подзадача успешно обновлена");
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Ошибка JSON: " + e.getMessage());
            sendServerError(exchange, "Invalid JSON format");
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка пересечения задач: " + e.getMessage());
            sendHasInteractions(exchange);
        } catch (Exception e) {
            System.err.println("Внутренняя ошибка сервера: " + e.getMessage());
            e.printStackTrace();
            sendServerError(exchange, "Internal Server Error: " + e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 3) { // /subtasks/{id}
            int subtaskId = parseId(pathParts[2]);
            taskManager.deleteSubtask(subtaskId);
            sendText(exchange, "Subtask deleted", 200);
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
