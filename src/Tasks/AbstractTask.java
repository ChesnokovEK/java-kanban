package Tasks;

import Enum.*;

import java.util.Objects;

public abstract class AbstractTask implements Cloneable {
    private int id;
    private String title;
    private String description;
    private State state;

    public AbstractTask(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.state = task.getState();
    }

    public AbstractTask(int id, String title, String description, State state) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.state = state;
    }

    public AbstractTask(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }

    public AbstractTask(String title, String description, State state) {
        this.title = title;
        this.description = description;
        this.state = state;
    }

    public AbstractTask(String title, String description) {
        this.title = title;
        this.description = description;
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

    public abstract String toString();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask task = (AbstractTask) o;
        return id == task.id
                && Objects.equals(title, task.title)
                && Objects.equals(description, task.description)
                && state == task.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, state);
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
