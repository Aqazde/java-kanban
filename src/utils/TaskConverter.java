package utils;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskConverter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String toString(Task task) {
        String epicId = (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : "";
        String startTime = (task.getStartTime() != null) ? task.getStartTime().format(FORMATTER) : "";
        String duration = String.valueOf(task.getDuration().toMinutes());

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(), // Для упрощения создан новый метод, связанный с Task (убрано instanceof)
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                duration,
                startTime,
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
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[5]));
        LocalDateTime startTime = parts[6].isEmpty() ? null : LocalDateTime.parse(parts[6], FORMATTER);
        Task task;

        switch (type) {
            case TASK:
                task = new Task(name, description);
                break;
            case EPIC:
                task = new Epic(name, description);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[7]);
                task = new Subtask(name, description, epicId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        task.setId(id);
        task.setStatus(status);
        task.setDuration(duration);
        task.setStartTime(startTime);
        return task;
    }
}
