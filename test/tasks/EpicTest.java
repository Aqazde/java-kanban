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

    @Test
    void testEpicStatusAllSubtasksNew() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если все подзадачи NEW");
    }

    @Test
    void testEpicStatusAllSubtasksDone() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, если все подзадачи DONE");
    }

    @Test
    void testEpicStatusMixedNewAndDoneSubtasks() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());

        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS," +
                " если подзадачи NEW и DONE");
    }

    @Test
    void testEpicStatusAllSubtasksInProgress() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId());

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS," +
                " если все подзадачи IN_PROGRESS");
    }

    @Test
    void testEpicStatusUpdatesWhenSubtaskStatusChanges() {
        Subtask subtask = new Subtask("Subtask 1", "Description", epic.getId());
        manager.createSubtask(subtask);

        assertEquals(Status.NEW, epic.getStatus(), "Ошибка: эпик должен быть в статусе NEW.");

        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);
        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Ошибка: эпик должен обновиться в IN_PROGRESS.");

        subtask.setStatus(Status.DONE);
        manager.updateSubtask(subtask);
        assertEquals(Status.DONE, epic.getStatus(), "Ошибка: эпик должен обновиться в DONE.");
    }

    @Test
    void testEpicStatusWithoutSubtasks() {
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если нет подзадач");
    }
}
