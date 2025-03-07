package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void setUp() {
        manager = createManager();
    }

    @Test
    void testAddAndGetTasksById() {
        Task task = new Task("Task 1", "Description for Task 1");
        Epic epic = new Epic("Epic 1", "Description for Epic 1");
        manager.createTask(task);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description for Subtask 1", epic.getId());
        manager.createSubtask(subtask);

        assertEquals(task, manager.getTaskById(task.getId()), "Задача не была корректно сохранена");
        assertEquals(epic, manager.getEpicById(epic.getId()), "Эпик не был корректно сохранен");
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()), "Подзадача не была корректно сохранена");
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Task 1", "Description");
        manager.createTask(task);
        task.setDescription("Updated Description");
        manager.updateTask(task);

        assertEquals("Updated Description", manager.getTaskById(task.getId()).getDescription(),
                "Описание задачи не обновилось");
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic("Epic 1", "Description");
        manager.createEpic(epic);
        epic.setDescription("Updated Epic Description");
        manager.updateEpic(epic);

        assertEquals("Updated Epic Description", manager.getEpicById(epic.getId()).getDescription(),
                "Описание эпика не обновилось");
    }

    @Test
    void testUpdateSubtask() {
        Epic epic = new Epic("Epic 1", "Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description", epic.getId());
        manager.createSubtask(subtask);

        subtask.setDescription("Updated Subtask Description");
        manager.updateSubtask(subtask);

        assertEquals("Updated Subtask Description", manager.getSubtaskById(subtask.getId()).getDescription(),
                "Описание подзадачи не обновилось");
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("Task 1", "Description");
        manager.createTask(task);
        manager.deleteTask(task.getId());

        assertNull(manager.getTaskById(task.getId()), "Задача не удалена");
    }

    @Test
    void testDeleteEpic() {
        Epic epic = new Epic("Epic 1", "Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description", epic.getId());
        manager.createSubtask(subtask);

        manager.deleteEpic(epic.getId());

        assertNull(manager.getEpicById(epic.getId()), "Эпик не удален");
        assertNull(manager.getSubtaskById(subtask.getId()), "Подзадача не удалена вместе с эпиком");
    }

    @Test
    void testDeleteSubtask() {
        Epic epic = new Epic("Epic 1", "Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description", epic.getId());
        manager.createSubtask(subtask);
        manager.deleteSubtask(subtask.getId());

        assertNull(manager.getSubtaskById(subtask.getId()), "Подзадача не удалена");
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task("Task 1", "Description");
        Task task2 = new Task("Task 2", "Description");
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size(), "Должно быть две задачи");
    }

    @Test
    void testGetAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description");
        Epic epic2 = new Epic("Epic 2", "Description");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        List<Epic> epics = manager.getAllEpics();
        assertEquals(2, epics.size(), "Должно быть два эпика");
    }

    @Test
    void testGetAllSubtasks() {
        Epic epic = new Epic("Epic 1", "Description");
        manager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(2, subtasks.size(), "Должно быть две подзадачи");
    }

    @Test
    void testDeleteAllTasks() {
        manager.createTask(new Task("Task 1", "Description"));
        manager.deleteAllTasks();

        assertTrue(manager.getAllTasks().isEmpty(), "Все задачи не удалены");
    }

    @Test
    void testDeleteAllEpics() {
        manager.createEpic(new Epic("Epic 1", "Description"));
        manager.deleteAllEpics();

        assertTrue(manager.getAllEpics().isEmpty(), "Все эпики не удалены");
    }

    @Test
    void testDeleteAllSubtasks() {
        Epic epic = new Epic("Epic 1", "Description");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask 1", "Description", epic.getId());
        manager.createSubtask(subtask);
        manager.deleteAllSubtasks();

        assertTrue(manager.getAllSubtasks().isEmpty(), "Все подзадачи не удалены");
    }

    @Test
    void testTaskTimeOverlapPrevention() {
        Task task1 = new Task("Task 1", "First task");
        task1.setStartTime(LocalDateTime.now().plusHours(1));
        task1.setDuration(Duration.ofMinutes(60));
        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Overlapping task");
        task2.setStartTime(task1.getStartTime().plusMinutes(30));
        task2.setDuration(Duration.ofMinutes(60));

        assertThrows(IllegalArgumentException.class, () -> manager.createTask(task2),
                "Ошибка: нельзя добавлять пересекающиеся задачи.");
    }
}
