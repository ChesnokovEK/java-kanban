package manager;

import enums.State;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    File file;
    Path path;

    @BeforeEach
    public void beforeEach() {
        try {
            file = File.createTempFile("Test", ".csv");
            path = file.toPath();
            manager = new FileBackedTasksManager(file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(path);
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Test
    public void shouldCorrectlySave() {
        Task firstTask = new Task(generateId(), "Task Title", "Task Description", State.NEW);
        manager.createTask(firstTask);
        Task secondTask = new Task(generateId(), "Task Title", "Task Description", State.NEW);
        manager.createTask(secondTask);
        Epic epic = new Epic(generateId(), "Epic Title", "Epic Description");
        manager.createEpic(epic);
        SubTask subtask = new SubTask(generateId(), "SubTask Title", "SubTask Description", epic.getId(), State.NEW);
        manager.createSubTask(subtask);
        epic.addRelatedSubTask(subtask);
        assertEquals(List.of(firstTask, secondTask), manager.getAllTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
        assertEquals(List.of(subtask), manager.getAllSubTasks());
    }

    @Test
    public void shouldCorrectlyLoad() {
        manager.createTask(new Task(generateId(), "Title", "Description", State.NEW));
        manager.createTask(new Task(generateId(), "Title", "Description", State.NEW));
        manager.createEpic(new Epic(generateId(), "Title", "Description"));
        manager.createSubTask(new SubTask(generateId(), "Title", "Description", 2, State.NEW));
        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(file);

        assertFalse(manager.getAllTasks().isEmpty());
        assertEquals(manager.getAllTasks(), loadedManager.getAllTasks());
        assertEquals(manager.getAllEpics(), loadedManager.getAllEpics());
        assertEquals(manager.getAllSubTasks(), loadedManager.getAllSubTasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyTasksEpicsSubtasks() {
        FileBackedTasksManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, manager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, manager.getAllSubTasks());
    }

    @Test
    public void shouldSaveAndLoadEmptyHistory() {
        manager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(Collections.EMPTY_LIST, manager.getHistory());
    }
}