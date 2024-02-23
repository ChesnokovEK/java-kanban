package Tasks;

import Enum.*;

public class Task {
    private String name;
    private String description;
    private int id;
    private State state;

    public Task(String name, String description, State state) {
        this.name = name;
        this.description = description;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public State getStatus() {
        return state;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(State state) {
        this.state = state;
    }

    public void setId(int id) {
        this.id = id;
    }
}
