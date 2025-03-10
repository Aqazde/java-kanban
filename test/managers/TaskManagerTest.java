package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Test
    void testCreateSubtaskWithGeneratedId() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        manager.createEpic(epic);

        // подзадача с сгенерированным id
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        manager.createSubtask(subtask1);

        // подзадача с установленным id
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", epic.getId());
        subtask2.setId(1);
        manager.createSubtask(subtask2);


        Subtask retrievedSubtask1 = manager.getSubtaskById(subtask1.getId());
        Subtask retrievedSubtask2 = manager.getSubtaskById(subtask2.getId());

        // retrievedSubtask2 не совпадает id с автоматически сгенерированным id
        assertNotEquals(retrievedSubtask1.getId(), retrievedSubtask2.getId(), "Подзадачи должны иметь разные id");

        // Подзадачи были добавлены корректно
        assertNotNull(retrievedSubtask1, "Первая подзадача должна быть добавлена в менеджер");
        assertNotNull(retrievedSubtask2, "Вторая подзадача должна быть добавлена в менеджер");
    }

    @Test
    void testSubtaskImmutabilityOnAdd() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        manager.createEpic(epic);
        Subtask originalSubtask = new Subtask("Original Subtask", "Original Description", epic.getId());
        manager.createSubtask(originalSubtask);

        // Получаем подзадачу из менеджера
        Subtask retrievedSubtask = manager.getSubtaskById(originalSubtask.getId());

        assertEquals(originalSubtask.getId(), retrievedSubtask.getId(), "ID подзадач должны совпадать");
        assertEquals(originalSubtask.getTitle(), retrievedSubtask.getTitle(), "Заголовки подзадач должны совпадать");
        assertEquals(originalSubtask.getDescription(), retrievedSubtask.getDescription(), "Описание подзадач должно совпадать");
        assertEquals(originalSubtask.getEpicId(), retrievedSubtask.getEpicId(), "ID эпика подзадач должны совпадать");
    }

    @Test
    void testDeleteNonExistentTask() {
        manager.deleteTask(999);
        assertTrue(manager.getAllTasks().isEmpty(), "Задача не должна быть добавлена" +
                "(ее не существует изначально)");
    }

    @Test
    void testDeleteSubtaskUpdatesEpic() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Epic Description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description", epic.getId());
        manager.createSubtask(subtask);
        assertEquals(1, epic.getSubtasks().size(), "В эпике должна быть одна подзадача");

        manager.deleteSubtask(subtask.getId());
        assertEquals(0, epic.getSubtasks().size(), "После удаления подзадачи эпик не должен содержать подзадач");
    }

    @Test
    void testUpdateTaskWithSetters() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Original Title", "Original Description");
        manager.createTask(task);

        Task retrievedTask = manager.getTaskById(task.getId());
        assertNotNull(retrievedTask);

        retrievedTask.setTitle("Updated Title");
        retrievedTask.setDescription("Updated Description");

        Task updatedTask = manager.getTaskById(task.getId());
        assertEquals("Updated Title", updatedTask.getTitle(), "Заголовок должен быть обновлен");
        assertEquals("Updated Description", updatedTask.getDescription(), "Описание должно быть обновлено");
    }

    @Test
    void testGetPrioritizedTasksSorting() {
        Task task1 = new Task("Task 1", "First task");
        task1.setStartTime(LocalDateTime.now().plusHours(3));
        task1.setDuration(Duration.ofMinutes(60));
        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Earlier task");
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofMinutes(60));
        manager.createTask(task2);

        Task task3 = new Task("Task 3", "No start time");
        manager.createTask(task3);

        List<Task> prioritized = new ArrayList<>(manager.getPrioritizedTasks());
        assertEquals(task2, prioritized.get(0), "Первая задача должна быть самой ранней");
        assertEquals(task1, prioritized.get(1), "Вторая задача должна быть следующей");
        assertEquals(task3, prioritized.get(2), "Задача без времени должна быть в конце");
    }

    @Test
    void testGetPrioritizedTasksWithSubtasks() {
        Epic epic = new Epic("Epic 1", "Test Epic");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "First subtask", epic.getId());
        subtask1.setStartTime(LocalDateTime.now().plusHours(2));
        subtask1.setDuration(Duration.ofMinutes(30));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Earlier subtask", epic.getId());
        subtask2.setStartTime(LocalDateTime.now().plusHours(1));
        subtask2.setDuration(Duration.ofMinutes(45));
        manager.createSubtask(subtask2);

        List<Task> prioritized = new ArrayList<>(manager.getPrioritizedTasks());
        assertEquals(subtask2, prioritized.get(0), "Ранее запланированная подзадача должна быть первой");
        assertEquals(subtask1, prioritized.get(1), "Позже запланированная подзадача должна идти следом");
    }
}
