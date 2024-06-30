package tasks;

import enums.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class AbstractTask implements Cloneable {
    private int id;
    private String title;
    private String description;
    private State state;
    private LocalDateTime startTime;
    private Duration duration;

    public AbstractTask(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.state = task.getState();
        this.startTime = task.getStartTime();
        this.duration = task.getDuration();
    }

    public AbstractTask(int id, String title, String description, State state, LocalDateTime startTime, long duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.state = state;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public AbstractTask(int id, String title, String description, LocalDateTime startTime, long duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.state = State.NEW;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public AbstractTask(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.now();
    }

    public AbstractTask(String title, String description, State state) {
        this.title = title;
        this.description = description;
        this.state = state;
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.now();
    }

    public AbstractTask(String title, String description) {
        this.title = title;
        this.description = description;
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public abstract String toString();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask task = (AbstractTask) o;
        return id == task.id
                && Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && state == task.state
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, state, startTime.toString(), duration.toString());
    }

    @Override
    public AbstractTask clone() {
        try {
            return (AbstractTask) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
