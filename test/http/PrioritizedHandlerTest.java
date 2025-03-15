package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Status;
import tasks.Task;
import utils.DurationTypeAdapter;
import utils.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PrioritizedHandlerTest {
    private static HttpTaskServer taskServer;
    private static TaskManager manager;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
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

    @BeforeEach
    void clearTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void shouldReturnEmptyPrioritizedListWhenNoTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Должен быть статус 200 (OK)");
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertTrue(tasks.isEmpty(), "Список приоритетных задач должен быть пустым");
    }

    @Test
    void shouldReturnTasksInPrioritizedOrder() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Earliest task");
        task1.setStatus(Status.NEW);
        task1.setStartTime(LocalDateTime.now().plusHours(1));
        task1.setDuration(Duration.ofMinutes(30));
        sendTaskToServer(task1);

        Task task2 = new Task("Task 2", "Later task");
        task2.setStatus(Status.NEW);
        task2.setStartTime(LocalDateTime.now().plusHours(3));
        task2.setDuration(Duration.ofMinutes(30));
        sendTaskToServer(task2);

        Task task3 = new Task("Task 3", "No start time");
        task3.setStatus(Status.NEW);
        sendTaskToServer(task3);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Должен быть статус 200 (OK)");

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertEquals(3, tasks.size(), "Должно быть 3 задачи в приоритетном порядке");
        assertEquals("Task 1", tasks.get(0).getTitle(), "Первая задача должна быть самой ранней");
        assertEquals("Task 2", tasks.get(1).getTitle(), "Вторая задача должна быть позже");
        assertEquals("Task 3", tasks.get(2).getTitle(), "Задача без времени должна быть в конце " +
                "списка");
    }

    private void sendTaskToServer(Task task) throws IOException, InterruptedException {
        String jsonTask = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ошибка при создании задачи");
    }
}
