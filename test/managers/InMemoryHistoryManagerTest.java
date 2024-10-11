package managers;
import org.junit.jupiter.api.Test;
import tasks.Task;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    // Проверяем, что история содержит обе задачи
    @Test
    void testTaskHistoryPreservation() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task 1", "Description 1");
        manager.createTask(task1);
        manager.getTaskById(task1.getId());

        Task task2 = new Task("Task 2", "Description 2");
        manager.createTask(task2);
        manager.getTaskById(task2.getId());

        assertEquals(2, manager.getHistory().size(), "История должна содержать две задачи");

        assertEquals("Task 1", manager.getHistory().get(0).getTitle(), "Первая задача в истории должна быть Task 1");
        assertEquals("Task 2", manager.getHistory().get(1).getTitle(), "Вторая задача в истории должна быть Task 2");
    }
}