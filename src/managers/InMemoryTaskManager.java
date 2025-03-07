package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;

import java.util.*;
import java.time.LocalDateTime;

public class InMemoryTaskManager implements TaskManager {
    protected int currentId = 1;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = new InMemoryHistoryManager();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
    );

    public Task createTask(Task task) {
        if (!isTimeSlotAvailable(task)) {
            System.out.println("Ошибка: задача пересекается по времени с другой.");
            return null;
        }
        task.setId(currentId++);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(currentId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Эпик с id " + subtask.getEpicId() + " не существует");
            return null;
        }
        if (!isTimeSlotAvailable(subtask)) {
            System.out.println("Ошибка: подзадача пересекается по времени с другой.");
            return null;
        }
        subtask.setId(currentId++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        epic.updateStatus();
        prioritizedTasks.add(subtask);
        return subtask;
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            prioritizedTasks.remove(tasks.get(task.getId()));
            if (isTimeSlotAvailable(task)) {
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            } else {
                System.out.println("Ошибка: новая версия задачи пересекается по времени.");
            }
        } else {
            System.out.println("Задача с id " + task.getId() + " не существует");
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            epic.updateStatus();
        } else {
            System.out.println("Эпик с id " + epic.getId() + " не существует");
        }
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            if (isTimeSlotAvailable(subtask)) {
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.updateStatus();
                }
                prioritizedTasks.add(subtask);
            } else {
                System.out.println("Ошибка: новая версия подзадачи пересекается по времени.");
            }
        } else {
            System.out.println("Подзадача с id " + subtask.getId() + " не существует");
        }
    }

    public List<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic != null ? epic.getSubtasks() : new ArrayList<>();
    }

    public void deleteTask(int taskId) {
        Task task = tasks.remove(taskId);
        if (task != null) {
            historyManager.remove(taskId);
            prioritizedTasks.remove(task);
        }
    }

    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
                epic.updateStatus();
            }
            historyManager.remove(subtaskId);
            prioritizedTasks.remove(subtask);
        } else {
            System.out.println("Подзадача с id " + subtaskId + " не существует");
        }
    }

    public void deleteEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
            historyManager.remove(epicId);
        } else {
            System.out.println("Эпик с id " + epicId + " не существует");
        }
    }

    // Удаление всех Задач
    public void deleteAllTasks() {
        for (int taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.clear();
        prioritizedTasks.clear();
    }

    // Удаление всех Эпиков
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                historyManager.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
            subtasks.clear();
            historyManager.remove(epic.getId());
        }
        epics.clear();
    }

    // Удаление всех подзадач
    public void deleteAllSubtasks() {
        for (int subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        subtasks.clear();
        prioritizedTasks.removeIf(task -> task.getType() == TaskType.SUBTASK);
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public boolean isTimeSlotAvailable(Task newTask) {
        if (newTask.getStartTime() == null) {
            return true;
        }
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        return prioritizedTasks.stream()
                .filter(task -> task.getStartTime() != null && task.getEndTime() != null)
                .noneMatch(existingTask -> {
                    LocalDateTime existingStart = existingTask.getStartTime();
                    LocalDateTime existingEnd = existingTask.getEndTime();
                    return !(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd));
                });
    }
}
