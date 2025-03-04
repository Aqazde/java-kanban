package managers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        task3 = new Task("Task 3", "Description 3");
        task3.setId(3);
    }

    @Test
    void testTaskHistoryPreservation() {
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
        historyManager.add(null);

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История не должна содержать null-задачи");
    }

    @Test
    void testAddTaskToHistory() {
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи");
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void testRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну задачу");
        assertEquals(task2, history.get(0), "Оставшаяся задача должна быть task2");
    }

    @Test
    void testAddDuplicateTaskToHistory() {
        historyManager.add(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать только одну задачу(без дубликата)");
        assertEquals(task1, history.get(0));
    }

    @Test
    void testRemoveTaskFromBeginning() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1); // Удаляем из начала

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(task2, task3), history, "История не совпадает");
    }

    @Test
    void testRemoveTaskFromMiddle() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2); // Удаляем из середины

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(task1, task3), history, "История не совпадает");
    }

    @Test
    void testRemoveTaskFromEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3); // Удаляем из конца

        List<Task> history = historyManager.getHistory();
        assertEquals(List.of(task1, task2), history, "История не совпадает");
    }
}
