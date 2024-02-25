package Tasks;

import java.util.*;
import Enum.*;

public class Epic extends AbstractTask {
    private Map<Integer, SubTask> relatedSubTasks = new HashMap<>();

    public Epic(Epic epic, Collection<SubTask> taskCollection) {
        super(epic.getId(), epic.getTitle(), epic.getDescription());

        for (SubTask subTask : taskCollection) {
            addRelatedSubTask(subTask);
        }

        setState();
    }

    public Epic(Epic epic) {
        super(epic.getId(), epic.getTitle(), epic.getDescription());

        for (SubTask subTask : epic.getAllRelatedSubTasks()) {
            addRelatedSubTask(subTask);
        }

        setState();
    }

    public Epic(String title, String description) {
        super(title, description);
    }

    public void addRelatedSubTask(SubTask subTask) {
        relatedSubTasks.put(subTask.getId(), new SubTask(subTask));
        calculateState(getAllRelatedSubTasks());
    }

    public void removeRelatedSubTaskById(int subTaskId) {
        relatedSubTasks.remove(subTaskId);
    }

    public Collection<SubTask> getAllRelatedSubTasks() {
        return relatedSubTasks.values();
    }

    public void removeAllRelatedSubTasks() {
        relatedSubTasks.clear();
        setState();
    }

    @Override
    public String toString() {
        List<Integer> subTasksId = new ArrayList<>();

        for (SubTask subTask : getAllRelatedSubTasks()) {
            subTasksId.add(subTask.getId());
        }

        return "\nEpic {\n"
                + "\tid='" + getId() + "'"
                + "\n\ttitle='" + getTitle() + "'"
                + ", \n\tdescription='" + getDescription() + "'"
                + ", \n\tstate='" + getState() + "'"
                + ", \n\trelatedSubTasksId=" + subTasksId
                + "\n}";
    }

    protected void setRelatedSubTasks(Map<Integer, SubTask> relatedSubTasks) {
        this.relatedSubTasks = relatedSubTasks;
    }

    protected void setState() {
        calculateState(getAllRelatedSubTasks());
    }

    private void calculateState(Collection<SubTask> relatedSubTasks) {
        if (relatedSubTasks.size() == 0) {
            setState(State.NEW);
            return;
        }

        List<State> statesOfSubTasks = new ArrayList<>();

        for (SubTask subTask : relatedSubTasks) {
            statesOfSubTasks.add(subTask.getState());
        }

        if (statesOfSubTasks.stream().distinct().toList().size() > 1) {
            setState(State.IN_PROGRESS);
            return;
        }

        setState(statesOfSubTasks.stream().distinct().toList().get(0));
    }
}
