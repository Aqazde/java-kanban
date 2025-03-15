package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
import managers.NotFoundException;
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
public class TaskHandlerTest {
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
    void clearTasks() {
        manager.deleteAllTasks();
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now().plusDays(1));

        String jsonTask = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Должен быть статус 201 (Created)");

        Task createdTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(createdTask, "Созданная задача не должна быть null");
        assertEquals(task.getTitle(), createdTask.getTitle(), "Название задачи должно совпадать");
        assertEquals(task.getDescription(), createdTask.getDescription(), "Описание задачи должно совпадать");

        Task savedTask = manager.getTaskById(createdTask.getId());
        assertNotNull(savedTask, "Задача должна быть сохранена в менеджере");
        assertEquals(createdTask.getId(), savedTask.getId(), "ID задачи должно совпадать");
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description");
        task.setStatus(Status.NEW);
        manager.createTask(task);

        task.setDescription("Updated Description");
        String jsonTask = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Должен быть статус 201 (Created)");

        Task updatedTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(updatedTask, "Обновленная задача не должна быть null");
        assertEquals("Updated Description", updatedTask.getDescription(), "Описание задачи" +
                " должно быть обновлено");
    }

    @Test
    void shouldReturn406ForTaskTimeConflict() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description");
        task1.setStartTime(LocalDateTime.now().plusHours(1));
        task1.setDuration(Duration.ofMinutes(30));
        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Description");
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(30));
        String jsonTask = gson.toJson(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Должен быть статус 406 (Conflict)");
    }


    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description");
        task.setStatus(Status.NEW);
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Должен быть статус 200 (OK)");

        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {}.getType());
        assertNotNull(tasks, "Список задач не должен быть null");
        assertEquals(1, tasks.size(), "Должна быть одна задача");
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description");
        task.setStatus(Status.NEW);
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Должен быть статус 200 (OK)");

        Task retrievedTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(retrievedTask, "Задача не должна быть null");
        assertEquals(task.getId(), retrievedTask.getId(), "ID задачи должно совпадать");
    }

    @Test
    void shouldReturn404ForNonExistentTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Должен быть статус 404 (Not Found)");
    }

    @Test
    void shouldDeleteAllTasks() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description");
        task.setStatus(Status.NEW);
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Должен быть статус 200 (OK)");

        List<Task> tasks = manager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Список задач должен быть пустым");
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description");
        task.setStatus(Status.NEW);
        manager.createTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Должен быть статус 200 (OK)");

        assertThrows(NotFoundException.class, () -> manager.getTaskById(task.getId()),
                "Задача должна быть удалена");
    }
}
