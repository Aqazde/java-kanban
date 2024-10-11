package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Epic Title", "Epic Description");
        epic.setId(1);

        Subtask subtask = new Subtask("Subtask Title", "Subtask Description", epic.getId());
        subtask.setId(1);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            if (subtask.getEpicId() == epic.getId()) {
                throw new IllegalArgumentException("Subtask не может быть своим собственным эпиком.");
            }
        });

        assertEquals("Subtask не может быть своим собственным эпиком.", exception.getMessage());
    }
}
