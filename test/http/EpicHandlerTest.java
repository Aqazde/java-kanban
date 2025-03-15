package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
import managers.NotFoundException;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
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
public class EpicHandlerTest {
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
    void clearEpics() {
        manager.deleteAllEpics();
    }

    @Test
    void shouldCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic Description");

        String jsonEpic = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Должен быть статус 201 (Created)");

        Epic createdEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(createdEpic, "Созданный эпик не должен быть null");
        assertEquals(epic.getTitle(), createdEpic.getTitle(), "Название эпика должно совпадать");
        assertEquals(epic.getDescription(), createdEpic.getDescription(), "Описание эпика должно совпадать");

        Epic savedEpic = manager.getEpicById(createdEpic.getId());
        assertNotNull(savedEpic, "Эпик должен быть сохранен в менеджере");
        assertEquals(createdEpic.getId(), savedEpic.getId(), "ID эпика должно совпадать");
    }

    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        manager.createEpic(epic1);

        Epic epic2 = new Epic("Epic 2", "Description 2");
        manager.createEpic(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Должен быть статус 200 (OK)");

        List<Epic> epics = gson.fromJson(response.body(), new TypeToken<List<Epic>>() {}.getType());
        assertNotNull(epics, "Список эпиков не должен быть null");
        assertEquals(2, epics.size(), "Должны быть два эпика");

        Epic retrievedEpic1 = epics.get(0);
        Epic retrievedEpic2 = epics.get(1);

        assertEquals(epic1.getTitle(), retrievedEpic1.getTitle(), "Название первого эпика должно совпадать");
        assertEquals(epic1.getDescription(), retrievedEpic1.getDescription(), "Описание первого эпика должно" +
                " совпадать");

        assertEquals(epic2.getTitle(), retrievedEpic2.getTitle(), "Название второго эпика должно совпадать");
        assertEquals(epic2.getDescription(), retrievedEpic2.getDescription(), "Описание второго эпика должно" +
                " совпадать");
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic Description");
        manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Должен быть статус 200 (OK)");

        Epic retrievedEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(retrievedEpic, "Эпик не должен быть null");
        assertEquals(epic.getTitle(), retrievedEpic.getTitle(), "Название эпика должно совпадать");
        assertEquals(epic.getDescription(), retrievedEpic.getDescription(), "Описание эпика должно совпадать");
    }

    @Test
    void shouldReturn404ForNonExistentEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic Description");
        manager.createEpic(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Должен быть статус 200 (OK)");
        assertThrows(NotFoundException.class, () -> manager.getEpicById(epic.getId()), "Эпик должен быть" +
                " удален");
    }
}
