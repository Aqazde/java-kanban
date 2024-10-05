import TaskManager.TaskManager;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        Task task1 = manager.createTask("Отучиться в универе", "Закрыть курсы успешно");
        Task task2 = manager.createTask("Уборка", "Убрать квартиру");

        Epic epic1 = manager.createEpic("Сделать уроки", "Уроки по курсам");
        Subtask subtask1 = manager.createSubtask("Выполнить тех.здание", "Выполнение тех.задания " +
                "спринта", epic1.getId());
        Subtask subtask2 = manager.createSubtask("Начать новый спринт", "Пройдя проверку тех.задания, " +
                "начать изучение 5го спринта", epic1.getId());

        Epic epic2 = manager.createEpic("Диплом", "Университетская дипломная работа");
        Subtask subtask3 = manager.createSubtask("Выбрать тему", "Выбрать тему дипломной работы",
                epic2.getId());

        System.out.println("Получение списка всех задач");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("\nПолучение задачи по идентификатору");
        System.out.println(manager.getTaskById(task1.getId()));

        System.out.println("\nПолучение списка всех эпиков");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }

        System.out.println("\nПолучение списка всех подзадач");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("\nВсе подзадачи эпика 'Сделать уроки':");
        for (Subtask subtask : manager.getSubtasksByEpic(epic1.getId())) {
            System.out.println(subtask);
        }
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);
        manager.updateSubtask(subtask2);
        System.out.println("\nСтатус эпика 'Сделать уроки' после выполнения всех подзадач:");
        System.out.println(epic1.getStatus());

        manager.deleteSubtask(subtask1.getId());
        System.out.println("\nСписок подзадач после удаления первой подзадачи:");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println("Статус эпика 'Сделать уроки' после удаления подзадачи:");
        System.out.println(epic1.getStatus());

        manager.deleteEpic(epic1.getId());
        System.out.println("\nСписок эпиков после удаления эпика 'Сделать уроки':");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }
        System.out.println("Список всех подзадач после удаления эпика 'Сделать уроки':");
        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        manager.deleteTask(task1.getId());
        System.out.println("\nСписок задач, после удаления первой задачи:");
        System.out.println(manager.getAllTasks());

        System.out.println("\nУдаление всех задач");
        manager.deleteAllTasks();
        System.out.println("После удаления всех задач:");
        System.out.println("Все задачи: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Все подзадачи: " + manager.getAllSubtasks());
    }
}
