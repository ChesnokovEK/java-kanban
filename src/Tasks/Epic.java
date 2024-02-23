package Tasks;

import java.util.ArrayList;
import Enum.*;

public class Epic extends AbstractTask {
    private ArrayList<Integer> relatedSubTaskIds = new ArrayList<>();
    private ArrayList<SubTask> relatedSubTasks = new ArrayList<>();

    public Epic(int id, String title, String description, State state) {
        super(id, title, description, state);
    }

    public Epic(String title, String description, State state) {
        super(title, description, state);
    }
    public Epic(String title, String description) {
        super(title, description);
        this.state = State.NEW;
    }

    public void addRelatedSubTaskId(int subTaskId) {
        relatedSubTaskIds.add(subTaskId);
    }

    public void addRelatedSubTask(SubTask subTask) {
        relatedSubTasks.add(subTask);
    }

    public void removeRelatedSubTaskId(int subTaskId) {
        relatedSubTaskIds.remove(subTaskId);
    }

    public void removeRelatedSubTask(int subTaskId) {
        relatedSubTasks.remove(subTaskId);
    }
    public ArrayList<Integer> getRelatedSubTaskId() {
        return relatedSubTaskIds;
    }

    public ArrayList<SubTask> getAllRelatedSubTasks() {
        return relatedSubTasks;
    }

    protected void setRelatedSubTaskIds(ArrayList<Integer> relatedSubTaskIds) {
        this.relatedSubTaskIds = relatedSubTaskIds;
    }
    protected void setRelatedSubTasks(ArrayList<SubTask> relatedSubTasks) {
        this.relatedSubTasks = relatedSubTasks;
    }

    protected void clear(){
        this.relatedSubTaskIds = new ArrayList<>();
        this.relatedSubTasks = new ArrayList<>();
        this.state = State.NEW;
    }

    @Override
    public String toString() {
        return "\nEpic {\n"
                + "\tid='" + id + "'"
                + "\n\ttitle='" + title + "'"
                + ", \n\tdescription='" + description + "'"
                + ", \n\tstate='" + state + "'"
                + ", \n\trelatedSubTaskIds=" + relatedSubTaskIds
                + "\n}";
    }
}
