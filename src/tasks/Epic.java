package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class Epic extends Task {
    private List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        this.subtasks = new ArrayList<>();
        this.endTime = null;
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateTimeAndDuration();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateTimeAndDuration();
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }

        boolean allNew = subtasks.stream().allMatch(s -> s.getStatus() == Status.NEW);
        boolean allDone = subtasks.stream().allMatch(s -> s.getStatus() == Status.DONE);

        if (allNew) {
            setStatus(Status.NEW);
        } else if (allDone) {
            setStatus(Status.DONE);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }

    public void updateTimeAndDuration() {
        if (subtasks.isEmpty()) {
            setDuration(Duration.ZERO);
            setStartTime(null);
            endTime = null;
            return;
        }

        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null) {
                if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                    earliestStart = subtask.getStartTime();
                }
            }
            if (subtask.getEndTime() != null) {
                if (latestEnd == null || subtask.getEndTime().isAfter(latestEnd)) {
                    latestEnd = subtask.getEndTime();
                }
            }
            totalDuration = totalDuration.plus(subtask.getDuration());
        }

        setDuration(totalDuration);
        setStartTime(earliestStart);
        this.endTime = latestEnd;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration().toMinutes() + " min" +
                ", startTime=" + getStartTime() +
                ", endTime=" + endTime +
                ", subtasks=" + subtasks +
                '}';
    }
}
