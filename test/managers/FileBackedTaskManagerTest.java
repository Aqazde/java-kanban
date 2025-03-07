package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasks.Subtask;
import tasks.Status;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
    void testSaveAndLoadFromFile() {
        Task task = new Task("Учеба", "Написать дипломную работу");
        fileManager.createTask(task);

        Epic epic = new Epic("Диплом", "Создать приложение и написать отчет");
        fileManager.createEpic(epic);

        Subtask subtask = new Subtask("Выбрать модель", "Сравнить модели", epic.getId());
        fileManager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(1, loadedManager.getAllTasks().size(), "Должна быть одна сохраненная задача");
        assertEquals(1, loadedManager.getAllEpics().size(), "Должен быть один сохраненный эпик");
        assertEquals(1, loadedManager.getAllSubtasks().size(), "Должна быть одна сохраненная " +
                "подзадача");

        assertEquals(task, loadedManager.getAllTasks().get(0), "Задачи не совпадают");
        assertEquals(epic, loadedManager.getAllEpics().get(0), "Эпики не совпадают");
        assertEquals(subtask, loadedManager.getAllSubtasks().get(0), "Подзадачи не совпадают");

        task.setStatus(Status.DONE);
        fileManager.updateTask(task);

        FileBackedTaskManager updatedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(Status.DONE, updatedManager.getTaskById(task.getId()).getStatus(), "Статус задачи " +
                "должен обновиться");

        fileManager.deleteTask(task.getId());
        FileBackedTaskManager afterDeleteManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(afterDeleteManager.getAllTasks().isEmpty(), "Задачи должны быть удалены");
    }
}
