package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private int currentId = 1;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private HistoryManager historyManager = new InMemoryHistoryManager();

    public Task createTask(Task task) {
        task.setId(currentId++);
        tasks.put(task.getId(), task);
        historyManager.add(task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(currentId++);
        epics.put(epic.getId(), epic);
        historyManager.add(epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            System.out.println("Эпик с id " + subtask.getEpicId() + " не существует");
            return null;
        }
        subtask.setId(currentId++);
        subtasks.put(subtask.getId(), subtask);
        historyManager.add(subtask);
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
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            historyManager.add(task);
        } else {
            System.out.println("Задача с id " + task.getId() + " не существует");
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            epic.updateStatus();
            historyManager.add(epic);
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
            historyManager.add(subtask);
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

    // Удаление всех Задач
    public void deleteAllTasks() {
        tasks.clear();
    }
    // Удаление всех Эпиков
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            subtasks.clear();
        }
        epics.clear();
    }
    // Удаление всех подзадач
    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            return task;
        }
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            return subtask;
        }
        Epic epic = epics.get(id);
        if (epic != null) {
            return epic;
        }
        return null;
    }

    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            return epic;
        }
        return null;
    }

    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            return subtask;
        }
        return null;
    }


    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
