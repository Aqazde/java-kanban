package utils;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskConverter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String toString(Task task) {
        String startTime = (task.getStartTime() != null) ? task.getStartTime().format(FORMATTER) : "";
        String duration = String.valueOf(task.getDuration().toMinutes());

        if (task.getType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%s,%s,%d",
                    subtask.getId(),
                    subtask.getType(),
                    subtask.getTitle(),
                    subtask.getStatus(),
                    subtask.getDescription(),
                    duration,
                    startTime,
                    subtask.getEpicId()
            );
        } else {
            return String.format("%d,%s,%s,%s,%s,%s,%s",
                    task.getId(),
                    task.getType(),
                    task.getTitle(),
                    task.getStatus(),
                    task.getDescription(),
                    duration,
                    startTime
            );
        }
    }

    public static Task fromString(String line) {
        String[] parts = line.split(",", -1); // для сохранения пустых значений

        if (parts.length < 7) {
            throw new IllegalArgumentException("Некорректный формат строки (мало данных): " + line);
        }

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
                if (parts.length < 8 || parts[7].isEmpty()) {
                    throw new IllegalArgumentException("Некорректный формат строки для Subtask: " + line);
                }
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
