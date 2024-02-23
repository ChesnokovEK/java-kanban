package Tasks;

import java.util.ArrayList;

import Enum.*;

public class Epic extends Task {
    private final ArrayList<SubTask> relatedSubTasks;

    public Epic(String name, String description, State state) {
        super(name, description, state);
        this.relatedSubTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubtasks() {
        return relatedSubTasks;
    }


    public void addSubtask(SubTask subtask) {
        relatedSubTasks.add(subtask);
    }

    public void removeSubtask(SubTask subtask) {
        relatedSubTasks.remove(subtask);
    }

}
