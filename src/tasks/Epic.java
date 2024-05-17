package tasks;

import java.util.*;

import enums.*;

public class Epic extends AbstractTask {
    private Map<Integer, SubTask> relatedSubTasks = new LinkedHashMap<>();

    public Epic(Epic epic) {
        super(epic.getId(), epic.getTitle(), epic.getDescription());

        for (SubTask subTask : epic.getAllRelatedSubTasks()) {
            addRelatedSubTask(subTask);
        }

        setState();
    }

    public Epic(String title, String description) {
        super(title, description);
        setState();
    }

    public Epic(int id, String title, String description) {
        super(id, title, description);
        setState();
    }

    public void addRelatedSubTask(SubTask subTask) {
        relatedSubTasks.put(subTask.getId(), new SubTask(subTask));
        setState();
    }

    public void removeRelatedSubTaskById(int subTaskId) {
        relatedSubTasks.remove(subTaskId);
        setState();
    }

    public List<SubTask> getAllRelatedSubTasks() {
        return new ArrayList<>(relatedSubTasks.values());
    }

    public void removeAllRelatedSubTasks() {
        relatedSubTasks.clear();
        setState();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(relatedSubTasks, epic.relatedSubTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), relatedSubTasks);
    }

    @Override
    public String toString() {
        List<Integer> subTasksId = new ArrayList<>();

        for (SubTask subTask : getAllRelatedSubTasks()) {
            subTasksId.add(subTask.getId());
        }

        return "\nEpic {\n" + "\tid='" + getId() + "'" + "\n\ttitle='" + getTitle() + "'" + ", \n\tdescription='" + getDescription() + "'" + ", \n\tstate='" + getState() + "'" + ", \n\trelatedSubTasksId=" + subTasksId + "\n}";
    }

    protected void setRelatedSubTasks(Map<Integer, SubTask> relatedSubTasks) {
        this.relatedSubTasks = relatedSubTasks;
    }

    protected void setState() {
        calculateState(getAllRelatedSubTasks());
    }

    private void calculateState(List<SubTask> relatedSubTasks) {
        if (relatedSubTasks.isEmpty()) {
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
