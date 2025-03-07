package managers;
import tasks.*;
import utils.TaskConverter;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write("id,type,name,status,description,duration,startTime,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(TaskConverter.toString(task) + "\n");
              }
            for (Epic epic : getAllEpics()) {
                writer.write(TaskConverter.toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(TaskConverter.toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() <= 1) return manager;

            int maxId = 0;

            for (int i = 1; i < lines.size(); i++) {
                Task task = TaskConverter.fromString(lines.get(i));

                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        manager.prioritizedTasks.add(task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        manager.epics.put(epic.getId(), epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(subtask.getId(), subtask);
                        Epic parentEpic = manager.epics.get(subtask.getEpicId());
                        if (parentEpic != null) {
                            parentEpic.addSubtask(subtask);
                        }
                        manager.prioritizedTasks.add(subtask);
                        break;
                }
                maxId = Math.max(maxId, task.getId());
            }

            manager.currentId = maxId + 1;

            for (Epic epic : manager.getAllEpics()) {
                epic.updateStatus();
                epic.updateTimeAndDuration();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
        return manager;
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int taskId) {
        super.deleteTask(taskId);
        save();
    }

    @Override
    public void deleteEpic(int epicId) {
        super.deleteEpic(epicId);
        save();
    }

    @Override
    public void deleteSubtask(int subtaskId) {
        super.deleteSubtask(subtaskId);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}
