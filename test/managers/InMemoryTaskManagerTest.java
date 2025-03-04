package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private InMemoryTaskManager manager;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
    }
    // проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    public void testAddAndGetTasksById() {
        Task task = new Task("Task 1", "Description for Task 1");
        Epic epic = new Epic("Epic 1", "Description for Epic 1");
        manager.createTask(task);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description for Subtask 1", epic.getId());
        manager.createSubtask(subtask);

        Task retrievedTask = manager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals("Task 1", retrievedTask.getTitle());
        assertEquals("Description for Task 1", retrievedTask.getDescription());

        Epic retrievedEpic = manager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals("Epic 1", retrievedEpic.getTitle());
        assertEquals("Description for Epic 1", retrievedEpic.getDescription());

        Subtask retrievedSubtask = manager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals("Subtask 1", retrievedSubtask.getTitle());
        assertEquals("Description for Subtask 1", retrievedSubtask.getDescription());
    }

    // проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
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

    // создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
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
    void testUpdateTask() {
        Task task = new Task("Task 1", "Description for Task 1");
        manager.createTask(task);

        task.setDescription("Updated Description");
        manager.updateTask(task);

        Task updatedTask = manager.getTaskById(task.getId());
        assertEquals("Updated Description", updatedTask.getDescription(),
                "Описание задачи не обновилось");
    }

    @Test
    void testUpdateSubtask() {
        Epic epic = new Epic("Epic 1", "Description for Epic 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description for Subtask 1", epic.getId());
        manager.createSubtask(subtask);

        subtask.setDescription("Updated Subtask Description");
        manager.updateSubtask(subtask);

        Subtask updatedSubtask = manager.getSubtaskById(subtask.getId());
        assertEquals("Updated Subtask Description", updatedSubtask.getDescription(),
                "Описание подзадачи не обновилось");
    }

    @Test
    void testUpdateEpic() {
        Epic epic = new Epic("Epic 1", "Description for Epic 1");
        manager.createEpic(epic);

        epic.setDescription("Updated Epic Description");
        manager.updateEpic(epic);

        Epic updatedEpic = manager.getEpicById(epic.getId());
        assertEquals("Updated Epic Description", updatedEpic.getDescription(),
                "Описание эпика не обновилось");
    }

    @Test
    void testDeleteTask() {
        Task task = new Task("Task 1", "Description for Task 1");
        manager.createTask(task);
        manager.deleteTask(task.getId());

        assertNull(manager.getTaskById(task.getId()), "Задача не удалена");
    }

    @Test
    void testDeleteEpic() {
        Epic epic = new Epic("Epic 1", "Description for Epic 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description for Subtask 1", epic.getId());
        manager.createSubtask(subtask);

        manager.deleteEpic(epic.getId());

        assertNull(manager.getEpicById(epic.getId()), "Эпик не удален");
        assertNull(manager.getSubtaskById(subtask.getId()),
                "Подзадача не удалена");
    }

    @Test
    void testDeleteSubtask() {
        Epic epic = new Epic("Epic 1", "Description for Epic 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description for Subtask 1", epic.getId());
        manager.createSubtask(subtask);
        manager.deleteSubtask(subtask.getId());

        assertNull(manager.getSubtaskById(subtask.getId()), "Подзадача не удалена");
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task("Task 1", "Description for Task 1");
        Task task2 = new Task("Task 2", "Description for Task 2");
        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> tasks = manager.getAllTasks();
        assertEquals(2, tasks.size(), "Должно быть две задачи");
    }

    @Test
    void testGetAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Description for Epic 1");
        Epic epic2 = new Epic("Epic 2", "Description for Epic 2");
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        List<Epic> epics = manager.getAllEpics();
        assertEquals(2, epics.size(), "Должно быть два эпика");
    }

    @Test
    void testGetAllSubtasks() {
        Epic epic = new Epic("Epic 1", "Description for Epic 1");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description for Subtask 1", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description for Subtask 2", epic.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        List<Subtask> subtasks = manager.getAllSubtasks();
        assertEquals(2, subtasks.size(), "Должно быть две подзадачи");
    }

    @Test
    void testDeleteAllTasks() {
        Task task = new Task("Task 1", "Description for Task 1");
        manager.createTask(task);
        manager.deleteAllTasks();

        assertTrue(manager.getAllTasks().isEmpty(), "Все задачи не удалены");
    }

    @Test
    void testDeleteAllEpics() {
        Epic epic = new Epic("Epic 1", "Description for Epic 1");
        manager.createEpic(epic);
        manager.deleteAllEpics();

        assertTrue(manager.getAllEpics().isEmpty(), "Все эпики не удалены");
    }

    @Test
    void testDeleteAllSubtasks() {
        Epic epic = new Epic("Epic 1", "Description for Epic 1");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description for Subtask 1", epic.getId());
        manager.createSubtask(subtask);
        manager.deleteAllSubtasks();

        assertTrue(manager.getAllSubtasks().isEmpty(), "Все подзадачи не удалены");
    }

    @Test
    void testDeleteNonExistentTask() {
        manager.deleteTask(999);
        assertTrue(manager.getAllTasks().isEmpty(), "Задача не должна быть добавлена" +
                "(ее не существует изначально)");
    }

    // id не остается после удаления и подзадача исчезает с эпика
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
}
