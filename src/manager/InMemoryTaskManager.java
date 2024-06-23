package manager;

import enums.TaskType;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static int id = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Comparator<AbstractTask> abstractTaskComparator = Comparator.comparing(AbstractTask::getStartTime);
    private final Set<AbstractTask> prioritizedTasksSet = new TreeSet<>(abstractTaskComparator);

    @Override
    public void createTask(Task task) {
        if (idExists(task)) {
            task.setId(generateId());
        }

        tasks.put(task.getId(), new Task(task));

        if (Objects.nonNull(task.getStartTime())) {
            addTaskToPrioritizedList(tasks.get(task.getId()));
        }

    }

    public boolean idExists(AbstractTask task) {
        return tasks.containsKey(task.getId()) || subTasks.containsKey(task.getId()) || epics.containsKey(task.getId());
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

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId())) {
            addTaskToPrioritizedList(task);
            tasks.put(task.getId(), new Task(task));
            return;
        }

        System.out.println("Ошибка. Попытка обновить задачу с не существующим id");
    }

    @Override
    public void removeTask(int taskId) {
        if (tasks.containsKey(taskId)) {
            prioritizedTasksSet.removeIf(task -> task.getId() == taskId);
            tasks.remove(taskId);
            historyManager.remove(taskId);
            return;
        }

        System.out.println("Ошибка. Попытка удалить задачу с не существующим id");
    }

    @Override
    public void removeAllTasks() {
        tasks.keySet().stream().forEach(id -> {
            prioritizedTasksSet.remove(tasks.get(id));
            historyManager.remove(id);
        });

        tasks.clear();

    }

    @Override
    public void createSubTask(SubTask subTask) {
        Epic epic = epics.get(subTask.getRelatedEpicId());
        SubTask task = new SubTask(subTask);

        if (epic != null) {
            if (idExists(task)) {
                task.setId(generateId());
            }

            subTask.setId(task.getId());
            subTasks.put(task.getId(), task);
            epic.addRelatedSubTask(task);

            if (Objects.nonNull(task.getStartTime())) {
                addTaskToPrioritizedList(task);
            }

            return;
        }

        System.out.println("Ошибка. Попытка добавить подзадачу к несуществующему эпику");
    }

    @Override
    public SubTask getSubTaskById(int subTaskId) {
        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Ошибка. Попытка получить подзадачу с не существующим id");
        }

        if (subTasks.containsKey(subTaskId)) {
            historyManager.add(subTasks.get(subTaskId));
        }

        return subTasks.get(subTaskId);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
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
        addTaskToPrioritizedList(task);
    }

    @Override
    public void removeSubTask(int subTaskId) {
        Epic epic = getEpicById(getSubTaskById(subTaskId).getRelatedEpicId());

        prioritizedTasksSet.removeIf(task -> task.getId() == subTaskId);

        if (subTasks.containsKey(subTaskId)) {
            epic.removeRelatedSubTaskById(subTaskId);
            subTasks.remove(subTaskId);
            historyManager.remove(subTaskId);
            return;
        }

        System.out.println("Ошибка. Попытка удалить подзадачу с не существующим id");
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.keySet().stream().forEach(id -> {
            prioritizedTasksSet.remove(subTasks.get(id));
            historyManager.remove(id);
        });
        subTasks.clear();

        epics.values().stream().forEach(Epic::removeAllRelatedSubTasks);
    }

    @Override
    public void createEpic(Epic epic) {
        if (idExists(epic)) {
            epic.setId(generateId());
        }

        epics.put(epic.getId(), new Epic(epic));
    }

    @Override
    public Epic getEpicById(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Ошибка. Попытка получить эпик с не существующим id");
        }

        if (epics.containsKey(epicId)) {
            historyManager.add(epics.get(epicId));
        }

        return epics.get(epicId);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpic(Epic epic) {
        if (getEpicById(epic.getId()) == null) {
            System.out.println("Ошибка. Попытка обновить эпик, с не существующим id");
            return;
        }
        epics.put(epic.getId(), new Epic(epic));
    }

    @Override
    public void removeEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            getAllSubTasksInEpic(epicId).stream().forEach(subTask -> {
                removeSubTask(subTask.getId());
                historyManager.remove(subTask.getId());
            });

            epics.remove(epicId);
            historyManager.remove(epicId);
            return;
        }

        System.out.println("Эпик с таким id не существует");
    }

    @Override
    public void removeAllEpics() {
        epics.keySet().stream().forEach(historyManager::remove);

        subTasks.keySet().stream().forEach(historyManager::remove);

        epics.clear();
        subTasks.clear();
    }

    @Override
    public List<SubTask> getAllSubTasksInEpic(int epicId) {
        return new ArrayList<>(getEpicById(epicId).getAllRelatedSubTasks());
    }

    @Override
    public List<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }

    public int generateId() {
        return ++id;
    }

    @Override
    public List<AbstractTask> getPrioritizedTasks() {
        return prioritizedTasksSet.stream().toList();
    }

    public boolean checkTimeOverlap(AbstractTask abstractTask) {
        List<AbstractTask> tasks = List.copyOf(getPrioritizedTasks());
        if (!tasks.isEmpty()) {
            for (AbstractTask task : tasks) {
                if (
                        abstractTask.getStartTime().isAfter(task.getEndTime())
                        || abstractTask.getEndTime().isBefore(task.getStartTime())
                ) {
                    continue;
                }
                return false;
            }
        }

        return true;
    }

    protected boolean isTaskOverlap(AbstractTask abstractTask) {
        if (!checkTimeOverlap(abstractTask)) {
            System.out.println("Есть пересечение по времени с другими задачами");
            return true;
        }
        return false;
    }

    private void addTaskToPrioritizedList(AbstractTask abstractTask) {
        if (!isTaskOverlap(abstractTask)) {
            prioritizedTasksSet.removeIf(task -> task.getId() == abstractTask.getId() & !task.equals(abstractTask));
            prioritizedTasksSet.add(abstractTask);
        }
    }
}
