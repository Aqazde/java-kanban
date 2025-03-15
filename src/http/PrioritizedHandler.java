package http;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(method) && "/prioritized".equals(path)) {
                handleGet(exchange);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            sendServerError(exchange, "Internal server error: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String response = gson.toJson(prioritizedTasks);
        sendText(exchange, response, 200);
    }
}
