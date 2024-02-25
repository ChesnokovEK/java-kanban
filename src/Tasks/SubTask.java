package Tasks;

import Enum.*;

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
