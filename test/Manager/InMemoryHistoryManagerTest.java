package Manager;

import Tasks.AbstractTask;
import Tasks.Task;
import Enum.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager manager;
    private int id = 0;

    public int generateId() {
        return ++id;
    }

    public Task createTask() {
        return new Task("Description", "Title");
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
        Task task2 = new Task(newTaskId, "Description" + newTaskId, "Title", State.NEW);
        Task task3 = createTask();

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);

        List<AbstractTask> history = manager.getHistory();
        assertEquals(List.of(task1, task2, task3), history);
    }

    @Test
    public void shouldAddTasksToHistory() {
        Task task1 = createTask();
        Task task2 = createTask();
        Task task3 = createTask();

        manager.add(task1);
        manager.add(task2);
        manager.add(task3);
        assertEquals(List.of(task1, task2, task3), manager.getHistory());
    }

    @Test
    public void shouldLimitSizeOfHistoryTo10() {
        Task task1 = createTask();

        for (int i = 0; i < 12; i++) {
            manager.add(task1);
        }

        assertEquals(10, manager.getHistory().size());
    }

    @Test
    public void shouldSavePreviousVersionOfTask() {
        Task task1 = createTask();
        Task task2 = (Task) task1.clone();

        manager.add(task1);
        task1.setState(State.IN_PROGRESS);
        manager.add(task1);
        assertEquals(List.of(task2, task1), manager.getHistory());
    }
}