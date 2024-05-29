package manager;

import tasks.AbstractTask;
import tasks.Task;
import enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager manager;
    private int id = 0;

    public int generateId() {
        return id++;
    }

    public Task createTask() {
        return new Task(generateId(), "Description", "Title", State.NEW);
    }

    @BeforeEach
    public void beforeEach() {
        manager = Managers.getDefaultHistory();
    }

    @Test
    public void shouldCreateInMemoryHistoryManager() {
        assertNotNull(manager.getHistory());
    }

    @Test
    public void shouldNotConflictRegardlessOfTheIdAssignmentMethod() {
        Task task1 = createTask();
        int newTaskId = generateId();
        Task task2 = new Task(newTaskId, "Description", "Title", State.NEW);
        Task task3 = createTask();

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        List<AbstractTask> history = manager.getHistory();
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
        assertTrue(history.contains(task3));
    }

    @Test
    public void shouldAddTasksToHistory() {
        List<Task> tasks = new ArrayList<>();
        int TASKS_LIST_SIZE = 10;

        for (int i = 0; i < TASKS_LIST_SIZE; i++) {
            Task task = new Task(i, "Description" + i, "Title", State.NEW);
            tasks.add(task);
            manager.add(task);
        }

        assertEquals(tasks, manager.getHistory());
    }

    @Test
    public void shouldNotLimitSizeOfHistoryTo10() {
        int i;
        for (i = 0; i < 12; i++) {
            manager.add(new Task(i, "Description" + i, "Title", State.NEW));
        }

        assertEquals(i, manager.getHistory().size());
    }

    @Test
    public void shouldKeepOnlyLatestVersionOfTask() {
        Task task = new Task(generateId(), "Description", "Title", State.NEW);
        Task beforeUpdate = (Task) task.clone();

        manager.add(task);
        task.setState(State.IN_PROGRESS);
        manager.add(task);
        assertEquals(1, manager.getHistory().size());
        assertEquals(task, manager.getHistory().get(0));
        assertNotEquals(beforeUpdate, manager.getHistory().get(0));
    }

    @Test
    public void shouldRemoveTaskFromHistory() {
        List<Task> tasks = new ArrayList<>();
        int TASKS_LIST_SIZE = 3;

        for (int i = 0; i < TASKS_LIST_SIZE; i++) {
            Task task = new Task(i, "Description" + i, "Title", State.NEW);
            tasks.add(task);
            manager.add(task);
        }

        assertEquals(TASKS_LIST_SIZE, manager.getHistory().size());
        manager.remove(0);
        assertNotEquals(TASKS_LIST_SIZE, manager.getHistory().size());
        assertFalse(manager.getHistory().contains(tasks.get(0)));
    }
}