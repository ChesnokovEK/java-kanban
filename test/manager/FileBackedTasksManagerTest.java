package manager;

import enums.State;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    File file;
    Path path;
    LocalDateTime localDateTime;

    @BeforeEach
    public void beforeEach() {
        try {
            file = File.createTempFile("Test", ".csv");
            path = file.toPath();
            manager = FileBackedTasksManager.loadFromFile(file);
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
        Epic epic = new Epic(generateId(), "Epic Title", "Epic Description", generateLocalDateTime(), 0);
        manager.createEpic(epic);
        SubTask subtask = new SubTask(generateId(), "SubTask Title", "SubTask Description", epic.getId(), generateLocalDateTime(), 0, State.NEW);
        manager.createSubTask(subtask);
        epic.addRelatedSubTask(subtask);
        assertEquals(List.of(firstTask, secondTask), manager.getAllTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
        assertEquals(List.of(subtask), manager.getAllSubTasks());
    }

    @Test
    public void shouldCorrectlyLoad() {
        Task firstTask = new Task(generateId(), "Task Title", "Task Description", State.NEW);
        manager.createTask(firstTask);
        Task secondTask = new Task(generateId(), "Task Title", "Task Description", State.NEW);
        manager.createTask(secondTask);
        Epic epic = new Epic(generateId(), "Epic Title", "Epic Description", generateLocalDateTime(), 0);
        manager.createEpic(epic);
        SubTask subtask = new SubTask(generateId(), "SubTask Title", "SubTask Description", epic.getId(), generateLocalDateTime(), 0, State.NEW);
        manager.createSubTask(subtask);
        epic.addRelatedSubTask(subtask);

        assertFalse(manager.getAllTasks().isEmpty());
        assertEquals(List.of(firstTask, secondTask), manager.getAllTasks());
        assertEquals(List.of(subtask), manager.getAllSubTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
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

    @Test
    public void shouldCorrectlyCreateTaskFromString() {
        Task task = new Task(
                0,
                "Task-1",
                "Task-1 Description",
                State.NEW,
                LocalDateTime.parse("2023-01-01T00:00"),
                40
        );
        Epic epic = new Epic(
                1,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2022-01-01T00:00"),
                40
        );
        SubTask subTask = new SubTask(
                2,
                "SubTask-1",
                "SubTask-1 Description",
                1,
                LocalDateTime.parse("2021-01-01T00:00"),
                40,
                State.NEW
        );
        epic.addRelatedSubTask(subTask);

        final String taskString = "0,TASK,Task-1,NEW,Task-1 Description,2023-01-01T00:00,40,";
        final String SubTaskString = "2,SUBTASK,SubTask-1,NEW,SubTask-1 Description,2021-01-01T00:00,40,1";
        final String epicString = "1,EPIC,Epic-1,NEW,Epic-1 Description,2022-01-01T00:00,40,";

        Path tempPath = Path.of("tempData.csv");
        File tempFile = new File(String.valueOf(tempPath));

        try (FileWriter writer = new FileWriter(String.valueOf(tempFile))) {
            writer.write(System.lineSeparator());
            writer.write(taskString + System.lineSeparator());
            writer.write(epicString + System.lineSeparator());
            writer.write(SubTaskString + System.lineSeparator());
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл", e);
        }

        manager = FileBackedTasksManager.loadFromFile(tempFile);

        assertEquals(List.of(task), manager.getAllTasks());
        assertEquals(List.of(subTask), manager.getAllSubTasks());
        assertEquals(List.of(epic), manager.getAllEpics());
    }

    @Test
    public void shouldCorrectlyFindIntersections(){
        Task task = new Task(
                0,
                "Task-1",
                "Task-1 Description",
                State.NEW,
                LocalDateTime.parse("2020-01-01T00:00"),
                40
        );
        Task overlapTask = new Task(
                3,
                "Task-1",
                "Task-1 Description",
                State.NEW,
                LocalDateTime.parse("2019-12-31T00:00"),
                60L*24+1
        );
        Epic epic = new Epic(
                1,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        SubTask subTask = new SubTask(
                2,
                "SubTask-1",
                "SubTask-1 Description",
                1,
                LocalDateTime.parse("2018-01-01T00:00"),
                40,
                State.NEW
        );
        epic.addRelatedSubTask(subTask);
        manager.createTask(task);
        manager.createTask(overlapTask);
        manager.createEpic(epic);
        manager.createSubTask(subTask);

        assertTrue(manager.getPrioritizedTasks().contains(task));
        assertTrue(manager.getPrioritizedTasks().contains(subTask));
        assertTrue(manager.getPrioritizedTasks().contains(epic));
        assertFalse(manager.getPrioritizedTasks().contains(overlapTask));
    }

    @Test
    public void shouldThrowSaveException() {
        Path tempPath = Path.of("wrongFile.csv");
        File tempFile = new File(String.valueOf(tempPath));

        try {
            Files.deleteIfExists(tempPath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        assertThrows(ManagerSaveException.class, () -> FileBackedTasksManager.loadFromFile(tempFile));
    }
}