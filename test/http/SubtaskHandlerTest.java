package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Subtask;
import utils.DurationTypeAdapter;
import utils.LocalDateTimeTypeAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SubtaskHandlerTest {
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

    @BeforeEach
    void clearSubtasks() {
        manager.deleteAllSubtasks();
    }

    @Test
    void shouldCreateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "Description");
        String jsonEpic = gson.toJson(epic);

        HttpRequest epicRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> epicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, epicResponse.statusCode(), "Ошибка при создании эпика");

        Epic createdEpic = gson.fromJson(epicResponse.body(), Epic.class);
        assertNotNull(createdEpic, "Созданный эпик не должен быть null");

        Subtask subtask = new Subtask("Subtask", "Description", createdEpic.getId());
        String jsonSubtask = gson.toJson(subtask);

        HttpRequest subtaskRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonSubtask))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> subtaskResponse = client.send(subtaskRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, subtaskResponse.statusCode(), "Ошибка при создании подзадачи");

        // Проверка тела ответа
        Subtask createdSubtask = gson.fromJson(subtaskResponse.body(), Subtask.class);
        assertNotNull(createdSubtask, "Созданная подзадача не должна быть null");
        assertEquals(subtask.getTitle(), createdSubtask.getTitle(), "Название подзадачи должно совпадать");
        assertEquals(subtask.getDescription(), createdSubtask.getDescription(), "Описание подзадачи " +
                "должно совпадать");

        // Проверка, что подзадача сохранена в менеджере
        Subtask savedSubtask = manager.getSubtaskById(createdSubtask.getId());
        assertNotNull(savedSubtask, "Подзадача должна быть сохранена в менеджере");
        assertEquals(createdSubtask.getId(), savedSubtask.getId(), "ID подзадачи должно совпадать");
    }
}
