package managers;

import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    @Test
    void testTaskHistoryPreservation() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи");

        assertEquals("Task 1", history.get(0).getTitle(), "Первая задача в истории должна быть Task 1");
        assertEquals("Task 2", history.get(1).getTitle(), "Вторая задача в истории должна быть Task 2");
    }

    @Test
    void testHistoryLimit() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        // 11 задач
        for (int i = 1; i <= 11; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История должна содержать только 10 задач");

        assertEquals("Task 2", history.get(0).getTitle(), "Первая задача должна быть Task 2 (Task 1 была удалена)");
        assertEquals("Task 11", history.get(9).getTitle(), "Последняя задача должна быть Task 11");
    }

    @Test
    void testAddingNullTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        historyManager.add(null);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не должна содержать null-задачи");
    }
}
