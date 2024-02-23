package Tasks;

import Enum.*;

public class Task extends AbstractTask {
    public Task(int id, String title, String description, State state) {
        super(id, title, description, state);
    }

    public Task(String title, String description, State state) {
        super(title, description, state);
    }

    public Task(String title, String description) {
        super(title, description);
        this.state = State.NEW;
    }

    @Override
    public String toString() {
        return "\nTask {\n"
                + "\tid='" + id + "'"
                + "\n\ttitle='" + title + "'"
                + ", \n\tdescription='" + description + "'"
                + ", \n\tstate='" + state + "'"
                + "\n}";
    }
}
