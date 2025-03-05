package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileManager;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        fileManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getAllTasks().isEmpty(), "Загруженный менеджер должен быть пустым");
    }

    @Test
    void testSaveAndLoadMultipleTasks() {
        Task task1 = new Task("Учеба", "Закончить курс");
        fileManager.createTask(task1);

        Epic epic1 = new Epic("Проект", "Создать приложение");
        fileManager.createEpic(epic1);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size(), "Должна быть одна сохраненная задача");
        assertEquals(1, loadedManager.getAllEpics().size(), "Должен быть один сохраненный эпик");
    }
}
