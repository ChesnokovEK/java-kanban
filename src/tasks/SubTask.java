package tasks;

import enums.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends AbstractTask {
    private int relatedEpicId;

    public SubTask(SubTask subTask) {
        super(subTask.getId(), subTask.getTitle(), subTask.getDescription(),
                subTask.getState(), subTask.getStartTime(), subTask.getDuration().toMinutes());
        this.relatedEpicId = subTask.getRelatedEpicId();
    }

    public SubTask(String title, String description, int relatedEpicId) {
        super(title, description);
        setRelatedEpicId(relatedEpicId);
        setState(State.NEW);
        setDuration(Duration.ZERO);
        setStartTime(LocalDateTime.now());
    }

    public SubTask(int id, String title, String description, int relatedEpicId) {
        super(id, title, description);
        setRelatedEpicId(relatedEpicId);
        setState(State.NEW);
        setDuration(Duration.ZERO);
        setStartTime(LocalDateTime.now());
    }

    public SubTask(int id, String title, String description, int relatedEpicId, LocalDateTime dateTime, long duration, State state) {
        super(id, title, description, state, dateTime, duration);
        setRelatedEpicId(relatedEpicId);
    }

    public int getRelatedEpicId() {
        return relatedEpicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subtask = (SubTask) o;
        return relatedEpicId == subtask.relatedEpicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relatedEpicId);
    }

    @Override
    public String toString() {
        return System.lineSeparator() + "SubTask {" + System.lineSeparator()
                + "\tid='" + getId() + "'"
                + System.lineSeparator() + "\ttitle='" + getTitle() + "'"
                + ", " + System.lineSeparator() + "\tdescription='" + getDescription() + "'"
                + ", " + System.lineSeparator() + "\tstate='" + getState() + "'"
                + ", " + System.lineSeparator() + "\trelatedEpicId='" + getRelatedEpicId() + "'"
                + ", " + System.lineSeparator() + "\tstartTime='" + getStartTime() + "'"
                + ", " + System.lineSeparator() + "\tendTime='" + getEndTime() + "'"
                + ", " + System.lineSeparator() + "\tduration='" + getDuration().toMinutes() + "'"
                + System.lineSeparator() + "}";
    }

    protected void setRelatedEpicId(int relatedEpicId) {
        this.relatedEpicId = relatedEpicId;
    }
}
