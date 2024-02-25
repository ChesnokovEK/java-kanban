package Manager;

import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;
import java.util.*;

public class TaskManager {
    private int id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();

    private int generateId() {
        return id++;
    }

    public void addTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), new Task(task));
    }

    public Task getTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            System.out.println("Ошибка. Попытка получить задачу с не существующим id");
        }

        return tasks.get(taskId);
    }

    public Collection<Task> getAllTasks() {
        return  tasks.values();
    }

    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), new Task(task));
            return;
        }

        System.out.println("Ошибка. Попытка обновить задачу с не существующим id");
    }

    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            tasks.remove(taskId);
            return;
        }

        System.out.println("Ошибка. Попытка удалить задачу с не существующим id");
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void addSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getRelatedEpicId());

        if (epic != null) {
            subTask.setId(generateId());
            subTasks.put(subTask.getId(), new SubTask(subTask));
            epic.addRelatedSubTask(new SubTask (subTask));
            return;
        }

        System.out.println("Ошибка. Попытка добавить подзадачу к несуществующему эпику");
    }

    public SubTask getSubTaskById(int subTaskId) {
        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Ошибка. Попытка получить подзадачу с не существующим id");
        }

        return subTasks.get(subTaskId);
    }

    public Collection<SubTask> getAllSubTasks() {
        return subTasks.values();
    }

    public void updateSubTask(SubTask subTask) {
        if (getEpicById(subTask.getRelatedEpicId()) == null) {
            System.out.println("Ошибка. Эпик, с указанным в подзадаче id не существует");
            return;
        }

        if (getSubTaskById(subTask.getId()) == null) {
            return;
        }

        subTasks.put(subTask.getId(), new SubTask(subTask));
        getEpicById(subTask.getRelatedEpicId()).addRelatedSubTask(subTask);
    }

    public void removeSubTask(int subTaskId) {
        Epic epic = getEpicById(getSubTaskById(subTaskId).getRelatedEpicId());

        if (subTasks.containsKey(subTaskId)) {
            epic.removeRelatedSubTaskById(subTaskId);
            subTasks.remove(subTaskId);
            return;
        }

        System.out.println("Ошибка. Попытка удалить подзадачу с не существующим id");
    }

    public void removeAllSubTasks() {
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeAllRelatedSubTasks();
        }
    }

    public void addEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), new Epic(epic));
    }

    public Epic getEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Ошибка. Попытка получить эпик с не существующим id");
        }

        return epics.get(epicId);
    }

    public Collection<Epic> getAllEpics(){
        return epics.values();
    }

    public void updateEpic(Epic epic) {
        if (getEpicById(epic.getId()) == null) {
            System.out.println("Ошибка. Попытка обновить эпик, с не существующим id");
            return;
        }

        epics.put(epic.getId(), new Epic(epic, epic.getAllRelatedSubTasks()));
    }

    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {

            for (SubTask subTask : getAllSubTasksInEpic(epicId)) {
                removeSubTask(subTask.getId());
            }

            epics.remove(epicId);
            return;
        }

        System.out.println("Эпик с таким id не существует");
    }

    public void removeAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public Collection<SubTask> getAllSubTasksInEpic(int epicId) {
        return getEpicById(epicId).getAllRelatedSubTasks();
    }
}
