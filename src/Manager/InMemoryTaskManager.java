package Manager;

import Tasks.AbstractTask;
import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), new Task(task));
    }

    @Override
    public Task getTaskById(int taskId) {
        if (!tasks.containsKey(taskId)) {
            System.out.println("Ошибка. Попытка получить задачу с не существующим id");
        }

        if (tasks.containsKey(taskId)) {
            historyManager.add(tasks.get(taskId));
        }

        return tasks.get(taskId);
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
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
            historyManager.remove(taskId);
            return;
        }

        System.out.println("Ошибка. Попытка удалить задачу с не существующим id");
    }

    public void removeAllTasks() {
        for (int id: tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();

    }

    public void createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getRelatedEpicId());
        SubTask task = new SubTask(subTask);

        if (epic != null) {
            task.setId(generateId());
            subTask.setId(task.getId());
            subTasks.put(task.getId(), task);
            epic.addRelatedSubTask(task);
            return;
        }

        System.out.println("Ошибка. Попытка добавить подзадачу к несуществующему эпику");
    }

    public SubTask getSubTaskById(int subTaskId) {
        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Ошибка. Попытка получить подзадачу с не существующим id");
        }

        if (subTasks.containsKey(subTaskId)) {
            historyManager.add(subTasks.get(subTaskId));
        }

        return subTasks.get(subTaskId);
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void updateSubTask(SubTask subTask) {
        SubTask task = new SubTask(subTask);

        if (getEpicById(task.getRelatedEpicId()) == null) {
            System.out.println("Ошибка. Эпик, с указанным в подзадаче id не существует");
            return;
        }

        if (getSubTaskById(task.getId()) == null) {
            return;
        }

        subTasks.put(task.getId(), task);
        Epic epic = getEpicById(task.getRelatedEpicId());
        epic.addRelatedSubTask(task);
        updateEpic(epic);
    }

    public void removeSubTask(int subTaskId) {
        Epic epic = getEpicById(getSubTaskById(subTaskId).getRelatedEpicId());

        if (subTasks.containsKey(subTaskId)) {
            epic.removeRelatedSubTaskById(subTaskId);
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
            return;
        }

        System.out.println("Ошибка. Попытка удалить подзадачу с не существующим id");
    }

    public void removeAllSubTasks() {
        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.removeAllRelatedSubTasks();
        }
    }

    public void createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), new Epic(epic));
    }

    public Epic getEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Ошибка. Попытка получить эпик с не существующим id");
        }

        if (epics.containsKey(epicId)) {
            historyManager.add(epics.get(epicId));
        }

        return epics.get(epicId);
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void updateEpic(Epic epic) {
        if (getEpicById(epic.getId()) == null) {
            System.out.println("Ошибка. Попытка обновить эпик, с не существующим id");
            return;
        }

        epics.put(epic.getId(), new Epic(epic));
    }

    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {

            for (SubTask subTask : getAllSubTasksInEpic(epicId)) {
                removeSubTask(subTask.getId());
            }

            epics.remove(epicId);
            historyManager.remove(epicId);
            return;
        }

        System.out.println("Эпик с таким id не существует");
    }

    public void removeAllEpics() {
        for (int id : epics.keySet()) {
            historyManager.remove(id);
        }

        for (int id : subTasks.keySet()) {
            historyManager.remove(id);
        }

        epics.clear();
        subTasks.clear();
    }

    public List<SubTask> getAllSubTasksInEpic(int epicId) {
        return new ArrayList<>(getEpicById(epicId).getAllRelatedSubTasks());
    }

    @Override
    public List<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }

    protected int generateId() {
        return id++;
    }

}
