package Tasks;

import Tasks.*;
import Enum.*;

import java.util.*;

public class TaskManager {
    private int id = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private int generateId() {
        return id++;
    }

    //Методы Task
    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public Collection<Task> getAllTasks() {
        return tasks.values();
    }

    public void updateTask(Task task, State state) {
        if (task != null && tasks.containsKey(task.getId())) {
            task.setState(state);
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            return;
        }

        if (subTasks.containsKey(taskId)) {
            deleteSubTask(subTasks.get(taskId));
            return;
        }

        if (epics.containsKey(taskId)) {
            deleteEpic(epics.get(taskId));
            return;
        }

        System.out.println("Задачи с таким id не существует");
    }
    
    public void deleteAllTasks() {
        tasks.clear();
    }

    //Методы SubTask
    public void addSubTask(SubTask subTask, int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            System.out.println("Эпика с таким id не существует");
            return;
        }

        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        epic.addRelatedSubTaskId(subTask.getId());
        epic.addRelatedSubTask(subTask);
        updateEpicState(epic);
    }

    public Collection<SubTask> getAllSubTasks() {
        return subTasks.values();
    }

    public void updateSubTask(SubTask subTask, State state) {
        Epic epic = epics.get(subTask.getRelatedEpicId());

        if (epic == null) {
            System.out.println("Эпик с таким id не существует");
            return;
        }

        if (!subTasks.containsKey(subTask.getId())) {
            addSubTask(subTask, subTask.getRelatedEpicId());
        }

        if (!epic.getRelatedSubTaskId().contains(subTask.getId())) {
            epic.addRelatedSubTaskId(subTask.getId());
        }

        epic.addRelatedSubTask(subTask);
        subTask.setState(state);
        updateEpic(epic);
        subTasks.put(subTask.getId(), subTask);
    }

    private void deleteSubTask(SubTask subTask){
        Epic epic = epics.get(subTask.getRelatedEpicId());
        epic.removeRelatedSubTaskId(subTask.getId());
        subTasks.remove(subTask.getId());
        updateEpic(epic);
    }

    //Методы Epic
    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
    }

    public Collection<Epic> getAllEpics(){
        return epics.values();
    }

    public void updateEpic(Epic epic) {
        updateEpicState(epic);
        epics.put(epic.getId(), epic);
    }

    private void updateEpicState(Epic epic){
        if (epic.getRelatedSubTaskId().size() == 0) {
            epic.setState(State.NEW);
            return;
        }

        Set<State> uniqueStates = new HashSet<>();

        for (SubTask subTask : epic.getAllRelatedSubTasks()) {
            uniqueStates.add(subTask.getState());
        }

        if (uniqueStates.size() > 1) {
            epic.setState(State.IN_PROGRESS);
            return;
        }

        for (State state : uniqueStates) {
            epic.setState(state);
        }
    }

    //т.к подзадачи не могут существовать отдельно от эпиков(?) из подзадач эпиков делаем обычные задачи
    private void deleteEpic(Epic epic) {
        for (SubTask subTask : epic.getAllRelatedSubTasks()) {
            convertSubTaskToTask(subTask);
        }
        epics.remove(epic.getId());
    }

    public void deleteAllSubTasks() {
        for (Epic epic : getAllEpics()) {
            epic.clear();
        }
        subTasks.clear();
    }

    //т.к подзадачи не могут существовать отдельно от эпиков(?) из подзадач эпиков делаем обычные задачи
    public void deleteAllEpics() {
        for (int epicId : epics.keySet()) {
            Epic epic =  epics.get(epicId);
            for (SubTask subTask : epic.getAllRelatedSubTasks()) {
                convertSubTaskToTask(subTask);
            }
        }
        epics.clear();
    }

    private void convertSubTaskToTask(SubTask subTask) {
        tasks.put(
                subTask.getId(),
                new Task(
                        subTask.getId(),
                        subTask.getTitle(),
                        subTask.getDescription(),
                        subTask.getState()
                )
        );
        subTasks.remove(subTask.getId());
    }

    public ArrayList<SubTask> getAllSubTasksInEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getAllRelatedSubTasks();
    }
}
