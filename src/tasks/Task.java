package tasks;

import enums.*;

import java.time.LocalDateTime;

public class Task extends AbstractTask {
    public Task(Task task) {
        super(task.getId(), task.getTitle(), task.getDescription(), task.getState(), task.getStartTime(), task.getDuration().toMinutes());
    }

    public Task(String title, String description) {
        super(title, description);
        setState(State.NEW);
    }

    public Task(int id, String title, String description, State state) {
        super(id, title, description, state, LocalDateTime.now(), 0);
    }

    public Task(int id, String title, String description, State state, LocalDateTime dateTime, long duration) {
        super(id, title, description, state, dateTime, duration);
    }

    @Override
    public String toString() {
        return System.lineSeparator() + "Task {" + System.lineSeparator()
                + "\tid='" + getId() + "'"
                + ", " + System.lineSeparator() + "\ttitle='" + getTitle() + "'"
                + ", " + System.lineSeparator() + "\tdescription='" + getDescription() + "'"
                + ", " + System.lineSeparator() + "\tstate='" + getState() + "'"
                + ", " + System.lineSeparator() + "\tstartTime='" + getStartTime() + "'"
                + ", " + System.lineSeparator() + "\tendTime='" + getEndTime() + "'"
                + ", " + System.lineSeparator() + "\tduration='" + getDuration().toMinutes() + "'"
                + System.lineSeparator() + "}";
    }
}
