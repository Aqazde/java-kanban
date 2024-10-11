package tasks;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TaskTest {
    // проверьте, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    public void testTaskEqualityById() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);

        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(1);

        assertEquals(task1, task2);

        task2.setId(2);
        assertNotEquals(task1, task2);
    }

    // проверьте, что наследники класса Task равны друг другу, если равен их id (epic)
    @Test
    public void testTaskEpicEqualityById() {
        Epic epic1 = new Epic("Epic1", "Epic description");
        epic1.setId(1);

        Epic epic2 = new Epic("Epic2", "Epic description");
        epic2.setId(1);
        assertEquals(epic1, epic2);

        epic2.setId(2);
        assertNotEquals(epic1, epic2);
    }

    // проверьте, что наследники класса Task равны друг другу, если равен их id (subTasks)
    @Test
    public void testTaskSubtaskEqualityById() {
        Subtask subtask1 = new Subtask("Subtask1", "Subtask description", 1);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask description", 1);


        assertEquals(subtask1, subtask2);

        subtask2.setId(2);
        assertNotEquals(subtask1, subtask2);
    }
}