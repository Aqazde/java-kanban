package TaskManager;

import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class TaskManager {
    private int currentId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Task createTask(String title, String description) {
        Task task = new Task(title, description);
        task.setId(currentId++);
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(String title, String description) {
        Epic epic = new Epic(title, description);
        epic.setId(currentId++);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask createSubtask(String title, String description, int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Эпик с id " + epicId + " не существует");
            return null;
        }
        Subtask subtask = new Subtask(title, description, epicId);
        subtask.setId(currentId++);
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        epic.updateStatus();
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
        tasks.put(task.getId(), task);
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
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.updateStatus();
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
        tasks.remove(taskId);
    }

    public void deleteSubtask(int subtaskId) {
        Subtask subtask = subtasks.remove(subtaskId);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
                epic.updateStatus();
            }
        } else {
            System.out.println("Подзадача с id " + subtaskId + " не существует");
        }
    }

    public void deleteEpic(int epicId) {
        Epic epic = epics.remove(epicId);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        } else {
            System.out.println("Эпик с id " + epicId + " не существует");
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
        deleteAllEpics();
        subtasks.clear();
    }

    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.getSubtasks()) {
                subtasks.remove(subtask.getId());
            }
        }
        epics.clear();
    }

    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            return subtasks.get(id);
        }
    }
}
