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
        task1.setId(1);

        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи");

        assertEquals(task1, history.get(0), "Первая задача в истории должна быть Task 1");
        assertEquals(task2, history.get(1), "Вторая задача в истории должна быть Task 2");
    }

    // Больше нет лимита в листе, удален тест проверки лимита историй

    @Test
    void testAddingNullTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        historyManager.add(null);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не должна содержать null-задачи");
    }

    @Test
    void testAddTaskToHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);

        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи");
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void testRemoveTaskFromHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);

        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну задачу");
        assertEquals(task2, history.get(0), "Оставшаяся задача должна быть task2");
    }

    @Test
    void testAddDuplicateTaskToHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        Task task = new Task("Task 1", "Description 1");
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну задачу(без дубликата)");
        assertEquals(task, history.get(0));
    }
}
