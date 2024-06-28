package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.State;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubTasksHandlerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager = Managers.getInMemoryTaskManager();
        taskServer = new HttpTaskServer(manager, gson);
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
        taskServer = null;
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        SubTask subTask = new SubTask(
                1,
                "SubTask 1",
                "SubTask 1 of Epic 1",
                State.NEW,
                LocalDateTime.now(),
                5,
                epic.getId()
        );
        manager.createEpic(epic);
        manager.createSubTask(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<SubTask> actualSubtasks = gson.fromJson(response.body(),
                new TypeToken<List<SubTask>>() {
                }.getType());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются");
        assertEquals(1, actualSubtasks.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testFindSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        SubTask expectedSubTask = new SubTask(
                1,
                "SubTask 1",
                "SubTask 1 of Epic 1",
                State.NEW,
                LocalDateTime.now(),
                5,
                epic.getId()
        );
        manager.createEpic(epic);
        manager.createSubTask(expectedSubTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + expectedSubTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask actualSubtask = gson.fromJson(response.body(), SubTask.class);
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertEquals(expectedSubTask, actualSubtask, "Подзадачи не совпадают");
        assertEquals(expectedSubTask.getTitle(), actualSubtask.getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        SubTask subTask = new SubTask(
                1,
                "SubTask 1",
                "SubTask 1 of Epic 1",
                State.NEW,
                LocalDateTime.now(),
                5,
                epic.getId()
        );
        manager.createEpic(epic);
        manager.createSubTask(subTask);

        String subTaskJson = gson.toJson(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<SubTask> expectedSubtasks = manager.getAllSubTasks();
        assertNotNull(expectedSubtasks, "Подзадачи не возвращаются");
        assertEquals(1, expectedSubtasks.size(), "Некорректное количество подзадач");
        assertEquals(subTask.getTitle(), new LinkedList<>(expectedSubtasks).getFirst().getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        SubTask expectedSubTask = new SubTask(
                1,
                "SubTask 1",
                "SubTask 1 of Epic 1",
                State.NEW,
                LocalDateTime.now(),
                5,
                epic.getId()
        );
        manager.createEpic(epic);
        manager.createSubTask(expectedSubTask);
        expectedSubTask.setTitle("New Title");
        String subtaskJson = gson.toJson(expectedSubTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        SubTask actualSubtask = gson.fromJson(response.body(), SubTask.class);
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        assertNotNull(actualSubtask, "Подзадача не возвращается");
        assertEquals(expectedSubTask.getTitle(), actualSubtask.getTitle(), "Некорректное имя подзадачи");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        SubTask subTask = new SubTask(
                1,
                "SubTask 1",
                "SubTask 1 of Epic 1",
                State.NEW,
                LocalDateTime.now(),
                5,
                epic.getId()
        );
        manager.createEpic(epic);
        manager.createSubTask(subTask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(204, response.statusCode());

        List<Task> expectedSubtasks = manager.getAllTasks();
        assertEquals(0, expectedSubtasks.size(), "Подзадача не удалилась");
    }
}
