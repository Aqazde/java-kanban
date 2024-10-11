package managers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
class ManagersTest {
    // Возвращение экземпляра ТаскМэнеджер
    @Test
    void testGetDefaultReturnsInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "TaskManager не должен быть null");
        assertTrue(taskManager instanceof InMemoryTaskManager, "Должен быть экземпляр InMemoryTaskManager");
    }

    // Возвращение ээкземпляра HistoryManager
    @Test
    void testGetDefaultHistoryReturnsInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "HistoryManager не должен быть null");
        assertTrue(historyManager instanceof InMemoryHistoryManager, "Должен быть экземпляр " +
                "InMemoryHistoryManager");
    }
}
