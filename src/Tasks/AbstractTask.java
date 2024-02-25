package Tasks;

import Enum.*;

public abstract class AbstractTask {
    private int id;
    private String title;
    private String description;
    private State state;

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
}
