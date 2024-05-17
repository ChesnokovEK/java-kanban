package tasks;

import enums.*;

import java.util.Objects;

public class SubTask extends AbstractTask {
    private int relatedEpicId;

    public SubTask(SubTask subTask) {
        super(subTask.getId(), subTask.getTitle(), subTask.getDescription(), subTask.getState());
        this.relatedEpicId = subTask.getRelatedEpicId();
    }

    public SubTask(String title, String description, int relatedEpicId) {
        super(title, description);
        setRelatedEpicId(relatedEpicId);
        setState(State.NEW);
    }

    public SubTask(int id, String title, String description, int relatedEpicId) {
        super(id, title, description);
        setRelatedEpicId(relatedEpicId);
        setState(State.NEW);
    }

    public SubTask(int id, String title, String description, int relatedEpicId, State state) {
        super(id, title, description, state);
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
        return "\nSubTask {\n"
                + "\tid='" + getId() + "'"
                + "\n\ttitle='" + getTitle() + "'"
                + ", \n\tdescription='" + getDescription() + "'"
                + ", \n\tstate='" + getState() + "'"
                + ", \n\trelatedEpicId='" + getRelatedEpicId() + "'"
                + "\n}";
    }

    protected void setRelatedEpicId(int relatedEpicId) {
        this.relatedEpicId = relatedEpicId;
    }
}
