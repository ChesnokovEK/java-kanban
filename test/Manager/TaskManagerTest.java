package Manager;

import Tasks.AbstractTask;
import Tasks.Epic;
import Tasks.SubTask;
import Tasks.Task;
import Enum.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    public int id = 0;
    protected T manager;

    public int generateId() {
        return id++;
    }

    public Task createTask() {
        return new Task(generateId(), "Title", "Description", State.NEW);
    }

    public Epic createEpic() {
        return new Epic(generateId(), "Title", "Description");
    }

    public SubTask createSubTask(Epic epic) {
        SubTask subTask = new SubTask(generateId(), "Title", "Description", epic.getId());
        epic.addRelatedSubTask(subTask);
        return subTask;
    }

    @Test
    public void shouldCreateTaskManager() {
        TaskManager inMemoryTaskManager = Managers.getInMemoryTaskManager();
        assertNotNull(inMemoryTaskManager.getAllTasks());
        assertNotNull(inMemoryTaskManager.getHistory());
    }

    @Test
    public void shouldCreateTask() {

        Task task = createTask();
        manager.createTask(task);
        Collection<Task> tasks = manager.getAllTasks();
        assertNotNull(tasks);
        assertEquals(State.NEW, task.getState());
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void shouldCreateEpic() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Collection<Epic> epics = manager.getAllEpics();
        assertNotNull(epics);
        assertNotNull(epic.getState());
        assertEquals(State.NEW, epic.getState());
        assertEquals(epic, manager.getEpicById(epic.getId()));
    }

    @Test
    public void shouldCreateSubTask() {
        Epic epic = createEpic();
        manager.createEpic(epic);

        SubTask subTask = createSubTask(epic);
        manager.createSubTask(subTask);

        List<SubTask> subTasks = manager.getAllSubTasks();

        assertNotNull(subTasks);
        assertNotNull(subTask);
        assertEquals(epic.getId(), subTask.getRelatedEpicId());
        assertEquals(State.NEW, subTask.getState());
        assertEquals(subTask, manager.getSubTaskById(subTask.getId()));

    }

    @Test
    public void shouldUpdateTaskStateToInProgress() {
        Task task = createTask();
        manager.createTask(task);
        task.setState(State.IN_PROGRESS);
        manager.updateTask(task);
        assertEquals(State.IN_PROGRESS, manager.getTaskById(task.getId()).getState());
    }

    @Test
    public void shouldUpdateEpicStateFromSubTaskState() {
        Epic epic = new Epic("Epic-Title", "Description");
        manager.createEpic(epic);
        SubTask subTask = new SubTask("SubTask-Title", "Description", epic.getId());
        manager.createSubTask(subTask);
        assertEquals(State.NEW, manager.getEpicById(epic.getId()).getState());
        subTask.setState(State.IN_PROGRESS);
        manager.updateSubTask(subTask);
        assertEquals(manager.getSubTaskById(subTask.getId()).getState(), manager.getEpicById(epic.getId()).getState());
        subTask.setState(State.DONE);
        manager.updateSubTask(subTask);
        assertEquals(State.DONE, manager.getEpicById(epic.getId()).getState());
    }

    @Test
    public void shouldNotUpdateEpicState() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        assertEquals(State.NEW, manager.getEpicById(epic.getId()).getState());
        epic.setState(State.IN_PROGRESS);
        manager.updateEpic(epic);
        assertEquals(State.NEW, manager.getEpicById(epic.getId()).getState());
        epic.setState(State.DONE);
        manager.updateEpic(epic);
        assertEquals(State.NEW, manager.getEpicById(epic.getId()).getState());
    }

    @Test
    public void shouldUpdateSubTaskStateToInProgress() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        subtask.setState(State.IN_PROGRESS);
        manager.updateSubTask(subtask);
        assertEquals(State.IN_PROGRESS, manager.getSubTaskById(subtask.getId()).getState());
    }

    @Test
    public void shouldUpdateTaskStateToInDone() {
        Task task = createTask();
        manager.createTask(task);
        task.setState(State.DONE);
        manager.updateTask(task);
        assertEquals(State.DONE, manager.getTaskById(task.getId()).getState());
    }

    @Test
    public void shouldUpdateSubTaskStateToInDone() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        subtask.setState(State.DONE);
        manager.updateSubTask(subtask);
        assertEquals(State.DONE, manager.getSubTaskById(subtask.getId()).getState());
        assertEquals(State.DONE, manager.getEpicById(epic.getId()).getState());
    }

    @Test
    public void shouldNotUpdateTaskIfNull() {
        Task task = createTask();
        manager.createTask(task);
        manager.updateTask(null);
        assertEquals(task, manager.getTaskById(task.getId()));
    }

    @Test
    public void shouldNotUpdateEpicIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        assertThrows(NullPointerException.class, () -> manager.updateEpic(null));
        assertEquals(epic, manager.getEpicById(epic.getId()));
    }

    @Test
    public void shouldNotUpdateSubTaskIfNull() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        assertThrows(NullPointerException.class, () -> manager.updateSubTask(null));
        assertEquals(subtask, manager.getSubTaskById(subtask.getId()));
    }

    @Test
    public void shouldDeleteAllTasks() {
        Task task = createTask();
        manager.createTask(task);
        assertFalse(manager.getAllTasks().isEmpty());
        manager.removeAllTasks();
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldDeleteAllEpics() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        assertFalse(manager.getAllEpics().isEmpty());
        manager.removeAllEpics();
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void shouldDeleteAllSubTasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        epic.addRelatedSubTask(subtask);
        assertFalse(epic.getAllRelatedSubTasks().isEmpty());
        assertFalse(manager.getAllSubTasks().isEmpty());
        manager.removeAllSubTasks();
        epic.removeAllRelatedSubTasks();
        assertTrue(epic.getAllRelatedSubTasks().isEmpty());
        assertTrue(manager.getAllSubTasks().isEmpty());
    }

    @Test
    public void shouldDeleteTaskById() {
        Task task = createTask();
        manager.createTask(task);
        assertTrue(manager.getAllTasks().contains(task));
        manager.removeTask(task.getId());
        assertFalse(manager.getAllTasks().contains(task));
    }

    @Test
    public void shouldDeleteEpicById() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        assertTrue(manager.getAllEpics().contains(epic));
        manager.removeEpic(epic.getId());
        assertFalse(manager.getAllEpics().contains(epic));
    }

    @Test
    public void shouldNotDeleteTaskIfBadId() {
        Task task = createTask();
        manager.createTask(task);
        manager.removeTask(999);
        assertTrue(manager.getAllTasks().contains(task));
    }

    @Test
    public void shouldNotDeleteEpicIfBadId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        manager.removeEpic(999);
        assertTrue(manager.getAllEpics().contains(epic));
    }

    @Test
    public void shouldNotDeleteSubTaskIfBadId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        assertThrows(NullPointerException.class, () -> manager.removeSubTask(999));
        assertTrue(manager.getAllSubTasks().contains(subtask));
        assertTrue(manager.getEpicById(epic.getId()).getAllRelatedSubTasks().contains(subtask));
    }

    @Test
    public void shouldReturnEmptyListWhenRelatedSubTasksIsEmpty() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        Collection<SubTask> subtasks = manager.getAllSubTasksInEpic(epic.getId());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void shouldReturnEmptyListTasksIfNoTasks() {
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListEpicsIfNoEpics() {
        assertTrue(manager.getAllEpics().isEmpty());
    }

    @Test
    public void shouldReturnEmptyListSubTasksIfNoSubTasks() {
        assertTrue(manager.getAllSubTasks().isEmpty());
    }

    @Test
    public void shouldReturnNullIfTaskDoesNotExist() {
        assertNull(manager.getTaskById(999));
    }

    @Test
    public void shouldReturnNullIfEpicDoesNotExist() {
        assertNull(manager.getEpicById(999));
    }

    @Test
    public void shouldReturnNullIfSubTaskDoesNotExist() {
        assertNull(manager.getSubTaskById(999));
    }

    @Test
    public void shouldReturnEmptyHistory() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnEmptyHistoryIfTasksNotExist() {
        manager.getTaskById(999);
        manager.getSubTaskById(999);
        manager.getEpicById(999);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void shouldReturnHistoryWithTasks() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask = createSubTask(epic);
        manager.createSubTask(subtask);
        manager.getEpicById(epic.getId());
        manager.getSubTaskById(subtask.getId());
        List<AbstractTask> list = manager.getHistory();
        assertEquals(2, list.size());
    }

    @Test
    public void shouldNotCreateSubTaskIfBadRelatedEpicId() {
        Epic epic = createEpic();
        manager.createEpic(epic);
        SubTask subtask1 = createSubTask(epic);
        SubTask subtask2 = new SubTask(1, "Title", "Description", 1);
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);
        assertEquals(1, manager.getAllSubTasks().size());
    }

    @Test
    public void shouldNotSavePreviousVersionOfTask() {
        Task task1 = createTask();
        Task task2 = (Task) task1.clone();

        manager.createTask(task1);
        manager.getTaskById(task1.getId());
        task1.setState(State.IN_PROGRESS);
        manager.updateTask(task1);
        manager.getTaskById(task1.getId());
        assertNotEquals(List.of(task2, task1), manager.getHistory());
    }
}