package server;

import com.google.gson.Gson;
import enums.State;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseHttpHandlerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private final Gson gson = HttpTaskServer.getGson();
    private HttpClient client;
    private final int NOT_EXISTING_ID = Integer.MAX_VALUE;

    @BeforeEach
    public void setUp() throws IOException {
        manager = Managers.getInMemoryTaskManager();
        taskServer = new HttpTaskServer(manager, gson);
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
        taskServer = null;
    }

    @Test
    public void shouldReturnCode400() throws Exception {
        String brJson = gson.toJson("Bad request");

        URI badRequest = URI.create("http://localhost:8080/tasks");
        HttpRequest taskRequest = HttpRequest.newBuilder().uri(badRequest).POST(HttpRequest.BodyPublishers.ofString(brJson)).build();
        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, taskResponse.statusCode());
    }

    @Test
    public void shouldReturnCode404() throws Exception {
        URI taskURL = URI.create(String.format("http://localhost:8080/tasks/%d", NOT_EXISTING_ID));
        URI subTaskURL = URI.create(String.format("http://localhost:8080/subtasks/%d", NOT_EXISTING_ID));
        URI epicURL = URI.create(String.format("http://localhost:8080/epics/%d", NOT_EXISTING_ID));
        URI subTaskOfEpicURL = URI.create(String.format("http://localhost:8080/epics/%d/subtasks", NOT_EXISTING_ID));

        HttpRequest taskRequest = HttpRequest.newBuilder().uri(taskURL).GET().build();
        HttpRequest subTaskRequest = HttpRequest.newBuilder().uri(subTaskURL).GET().build();
        HttpRequest epicRequest = HttpRequest.newBuilder().uri(epicURL).GET().build();
        HttpRequest subTaskOfEpicRequest = HttpRequest.newBuilder().uri(subTaskOfEpicURL).GET().build();

        HttpResponse<String> taskResponse = client.send(taskRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> subTaskResponse = client.send(subTaskRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> EpicResponse = client.send(epicRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> subTaskOfEpicResponse = client.send(subTaskOfEpicRequest, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, taskResponse.statusCode());
        assertEquals(404, subTaskResponse.statusCode());
        assertEquals(404, EpicResponse.statusCode());
        assertEquals(404, subTaskOfEpicResponse.statusCode());
    }

    @Test
    public void shouldReturnCode406WhenSubTasksOverlaps() throws Exception {
        Epic epic = new Epic(
                0,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        SubTask subTask1 = new SubTask(
                1,
                "SubTask 1",
                "SubTask 1 of Epic 1",
                State.NEW,
                LocalDateTime.now(),
                5,
                epic.getId()
        );
        SubTask subTask2 = new SubTask(
                2,
                "SubTask 2",
                "SubTask 2 of Epic 1",
                State.NEW,
                subTask1.getStartTime(),
                5,
                epic.getId()
        );

        manager.createEpic(epic);

        String subTask1Json = gson.toJson(subTask1);
        String subTask2Json = gson.toJson(subTask2);

        // создаём HTTP-клиент и запрос
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTask1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTask2Json)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response1.statusCode());
        assertEquals(406, response2.statusCode());

    }

    @Test
    public void shouldReturnCode406WhenTasksOverlaps() throws Exception {
        Task task1 = new Task(
                1,
                "Task 1",
                "Description pf Task 1",
                State.NEW,
                LocalDateTime.now(),
                5
        );
        Task task2 = new Task(
                2,
                "Task 2",
                "Description pf Task 2",
                State.NEW,
                task1.getStartTime(),
                5
        );

        String task1Json = gson.toJson(task1);
        String task2Json = gson.toJson(task2);

        // создаём HTTP-клиент и запрос
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request1 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpRequest request2 = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response1.statusCode());
        assertEquals(406, response2.statusCode());

    }

    @Test
    public void shouldReturnCode500() throws Exception {
        Task task1 = new Task(
                1,
                "Task 1",
                "Description pf Task 1",
                State.NEW,
                LocalDateTime.now(),
                5
        );

        String task1Json = gson.toJson(task1);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(500, response.statusCode());

    }
}
