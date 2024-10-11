import managers.InMemoryTaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Отучиться в универе", "Закрыть курсы успешно");
        manager.createTask(task1);
        Task task2 = new Task("Уборка", "Убрать квартиру");
        manager.createTask(task2);

        Epic epic1 = new Epic("Сделать уроки", "Уроки по курсам");
        manager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Выполнить тех. задание", "Выполнение тех. задания спринта",
                epic1.getId());
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Начать новый спринт", "Пройдя проверку тех. задания, " +
                "начать изучение 5-го спринта", epic1.getId());
        manager.createSubtask(subtask2);

        Epic epic2 = new Epic("Диплом", "Университетская дипломная работа");
        manager.createEpic(epic2);
        Subtask subtask3 = new Subtask("Выбрать тему", "Выбрать тему дипломной работы", epic2.getId());
        manager.createSubtask(subtask3);


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
        System.out.println("\nСтатус эпика 'Сделать уроки' после удаления подзадачи:");
        System.out.println(epic1.getStatus());

        manager.deleteEpic(epic1.getId());
        System.out.println("\nСписок эпиков после удаления эпика 'Сделать уроки':");
        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
        }
        System.out.println("\nСписок всех подзадач после удаления эпика 'Сделать уроки':");
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

        System.out.println("\nУдаление всех подзадач");
        manager.deleteAllSubtasks();
        System.out.println("После удаления всех подзадач:");
        System.out.println("Все задачи: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Все подзадачи: " + manager.getAllSubtasks());

        System.out.println("\nУдаление всех эпиков");
        manager.deleteAllEpics();
        System.out.println("После удаления всех эпиков:");
        System.out.println("Все задачи: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Все подзадачи: " + manager.getAllSubtasks());
    }
}
