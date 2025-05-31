package http;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if ("GET".equals(method) && "/history".equals(path)) {
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
        List<Task> history = taskManager.getHistory();
        String response = gson.toJson(history);
        sendText(exchange, response, 200);
    }
}
