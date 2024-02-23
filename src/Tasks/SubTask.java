package Tasks;

import Enum.*;

public class SubTask extends Task {
    private int relatedEpicId;

    public SubTask(String name, String description, State state, int epicId) {
        super(name, description, state);
        this.relatedEpicId = epicId;
    }

    public int getRelatedEpicId() {

        return relatedEpicId;
    }

    public void setRelatedEpicId(int relatedEpicId) {
        this.relatedEpicId = relatedEpicId;
    }
}
