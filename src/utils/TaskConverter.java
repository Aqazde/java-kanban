package utils;

import tasks.*;

public class TaskConverter {

    public static String toString(Task task) {
        String epicId = (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : "";
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(), // Для упрощения создан новый метод, связанный с Task (убрано instanceof)
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                epicId
        );
    }

    public static Task fromString(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Task task;
        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                task = new Subtask(name, description, epicId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        task.setId(id);
        task.setStatus(status);
        return task;
    }
}
