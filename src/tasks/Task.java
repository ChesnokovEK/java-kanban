package tasks;

import Enum.*;

public class Task extends AbstractTask {
    public Task(Task task) {
        super(task.getId(), task.getTitle(), task.getDescription(), task.getState());
    }

    public Task(String title, String description) {
        super(title, description);
        setState(State.NEW);
    }

    public Task(int id, String description, String title, State state) {
        super(id, description, title, state);
    }

    @Override
    public String toString() {
        return "\nTask {\n"
                + "\tid='" + getId() + "'"
                + "\n\ttitle='" + getTitle() + "'"
                + ", \n\tdescription='" + getDescription() + "'"
                + ", \n\tstate='" + getState() + "'"
                + "\n}";
    }
}
