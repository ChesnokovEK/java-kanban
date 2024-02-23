package Tasks;

import Enum.*;

public class SubTask extends AbstractTask {
    private int relatedEpicId;

    public SubTask(int id, String title, String description, State state, int relatedEpicId) {
        super(id, title, description, state);
        this.relatedEpicId = relatedEpicId;
    }

    public SubTask(String title, String description, State state, int relatedEpicId) {
        super(title, description, state);
        this.relatedEpicId = relatedEpicId;
    }

    public SubTask(String title, String description, int relatedEpicId) {
        super(title, description);
        this.relatedEpicId = relatedEpicId;
        this.state = State.NEW;
    }


    public int getRelatedEpicId() {
        return relatedEpicId;
    }

    protected void setRelatedEpicId(int relatedEpicId) {
        this.relatedEpicId = relatedEpicId;
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
}
