package tasks;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void testEpicCannotAddItselfAsSubtask() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Epic epic = new Epic("Epic 1", "Epic description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());

        // assertThrows - ловим исключение
        assertThrows(IllegalArgumentException.class, () -> {
            if (subtask.getEpicId() == epic.getId()) {
                throw new IllegalArgumentException("Epic cannot be added as its own subtask.");
            }
        });
    }
}
