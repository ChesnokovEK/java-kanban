package Manager;

import Tasks.AbstractTask;
import Tasks.Task;
import Tasks.SubTask;
import Tasks.Epic;

import java.util.*;

public interface TaskManager {
    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubTask(int id);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubTasks();

    Task getTaskById(int id);

    Epic getEpicById(int id);

    SubTask getSubTaskById(int id);

    Collection<Task> getAllTasks();

    Collection<Epic> getAllEpics();

    Collection<SubTask> getAllSubTasks();

    Collection<SubTask> getAllSubTasksInEpic(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    List<AbstractTask> getHistory();
}
