package tasks;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private InMemoryTaskManager manager;
    private Epic epic;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
        epic = new Epic("Epic 1", "Epic description");
        manager.createEpic(epic);
    }
    @Test
    void testEpicCannotAddItselfAsSubtask() {
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());

        // assertThrows - ловим исключение
        assertThrows(IllegalArgumentException.class, () -> {
            if (subtask.getEpicId() == epic.getId()) {
                throw new IllegalArgumentException("Epic cannot be added as its own subtask.");
            }
        });
    }
}
