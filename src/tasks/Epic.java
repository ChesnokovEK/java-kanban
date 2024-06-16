package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import enums.*;

public class Epic extends AbstractTask {
    private LocalDateTime endTime;
    private Map<Integer, SubTask> relatedSubTasks = new LinkedHashMap<>();

    public Epic(Epic epic) {
        super(epic.getId(), epic.getTitle(), epic.getDescription(), epic.getStartTime(), epic.getDuration().toMinutes());
        epic.getAllRelatedSubTasks().stream().forEach(this::addRelatedSubTask);
        setState();
        calculateDurationAndEndTime(epic.getAllRelatedSubTasks());
    }

    public Epic(String title, String description) {
        super(title, description);
        setState();
        calculateDurationAndEndTime(getAllRelatedSubTasks());
    }

    public Epic(int id, String title, String description, LocalDateTime dateTime, long duration, SubTask... relatedSubTasks) {
        super(id, title, description, dateTime, duration);
        Arrays.stream(relatedSubTasks).forEach(this::addRelatedSubTask);
        setState();
        calculateDurationAndEndTime(getAllRelatedSubTasks());
    }

    public void addRelatedSubTask(SubTask subTask) {
        relatedSubTasks.put(subTask.getId(), new SubTask(subTask));
        setState();
        calculateDurationAndEndTime(getAllRelatedSubTasks());
    }

    public void removeRelatedSubTaskById(int subTaskId) {
        relatedSubTasks.remove(subTaskId);
        setState();
        calculateDurationAndEndTime(getAllRelatedSubTasks());
    }

    public List<SubTask> getAllRelatedSubTasks() {
        return new ArrayList<>(relatedSubTasks.values());
    }

    public void removeAllRelatedSubTasks() {
        relatedSubTasks.clear();
        setState();
        calculateDurationAndEndTime(getAllRelatedSubTasks());
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

        getAllRelatedSubTasks().stream().forEach(subTask -> subTasksId.add(subTask.getId()));

        return System.lineSeparator() + "Epic {" + System.lineSeparator()
                + "\tid='" + getId() + "'"
                + System.lineSeparator()+"\ttitle='" + getTitle() + "'"
                + ", " + System.lineSeparator() + "\tdescription='" + getDescription() + "'"
                + ", " + System.lineSeparator() + "\tstate='" + getState() + "'"
                + ", " + System.lineSeparator() + "\tstartTime='" + getStartTime() + "'"
                + ", " + System.lineSeparator() + "\tendTime='" + getEndTime() + "'"
                + ", " + System.lineSeparator() + "\tduration='" + getDuration().toMinutes() + "'"
                + ", " + System.lineSeparator() + "\trelatedSubTasksId=" + subTasksId + System.lineSeparator() + "}";
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    protected void setRelatedSubTasks(Map<Integer, SubTask> relatedSubTasks) {
        this.relatedSubTasks = relatedSubTasks;
    }

    protected void setState() {
        calculateState(getAllRelatedSubTasks());
    }

    protected void setEndTime(LocalDateTime localDateTime) {
        this.endTime = localDateTime;
    }

    private void calculateState(List<SubTask> relatedSubTasks) {
        if (relatedSubTasks.isEmpty()) {
            setState(State.NEW);
            return;
        }

        List<State> statesOfSubTasks = new ArrayList<>();

        relatedSubTasks.stream().forEach(subTask -> statesOfSubTasks.add(subTask.getState()));

        if (statesOfSubTasks.stream().distinct().toList().size() > 1) {
            setState(State.IN_PROGRESS);
            return;
        }

        setState(statesOfSubTasks.stream().distinct().toList().get(0));
    }

    private void calculateDurationAndEndTime(List<SubTask> relatedSubTasks) {
        if (relatedSubTasks.isEmpty()) {
            setEndTime(getStartTime());
            return;
        }

        Duration totalDuration = Duration.ofMinutes(relatedSubTasks.stream()
                .map(subTask -> subTask.getDuration().toMinutes())
                .reduce(0L, Long::sum));

        setDuration(totalDuration);
        setEndTime(getStartTime().plusMinutes(totalDuration.toMinutes()));
    }
}
