package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Task;
import utils.DurationTypeAdapter;
import utils.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HistoryHandlerTest {
    private static HttpTaskServer taskServer;
    private static TaskManager manager;
    private static final HttpClient client = HttpClient.newHttpClient();

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    @BeforeAll
    static void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterAll
    static void shutDown() {
        taskServer.stop();
    }

    @Test
    void shouldGetEmptyHistoryInitially() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertTrue(history.isEmpty());
    }

    @Test
    void shouldGetHistoryWithTasks() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description");
        manager.createTask(task);

        manager.getTaskById(task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Должен быть статус 200 (OK)");

        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertNotNull(history, "История не должна быть null");
        assertEquals(1, history.size(), "В истории должна быть одна задача");

        Task retrievedTask = history.get(0);
        assertEquals(task.getTitle(), retrievedTask.getTitle(), "Название задачи должно совпадать");
        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Описание задачи " +
                "должно совпадать");

        List<Task> managerHistory = manager.getHistory();
        assertEquals(managerHistory.size(), history.size(), "История в менеджере и ответе должна совпадать");
        assertEquals(managerHistory.get(0).getId(), history.get(0).getId(), "ID задач в истории " +
                "должны совпадать");
    }
}
