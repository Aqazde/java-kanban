package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
