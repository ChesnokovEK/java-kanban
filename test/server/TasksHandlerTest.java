package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.State;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TasksHandlerTest {
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
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = new Task(
                0,
                "Task 1",
                "Description of Task 1",
                State.NEW,
                LocalDateTime.now(),
                5
        );
        manager.createTask(task);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            List<Task> actualTasks = gson.fromJson(response.body(),
                    new TypeToken<List<Task>>() {
                    }.getType());
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            assertNotNull(actualTasks, "Задачи не возвращаются");
            assertEquals(1, actualTasks.size(), "Некорректное количество задач");
        }
    }

    @Test
    public void testFindTask() throws IOException, InterruptedException {
        Task expectedTask = new Task(
                0,
                "Task 1",
                "Description of Task 1",
                State.NEW,
                LocalDateTime.now(),
                5
        );
        manager.createTask(expectedTask);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + expectedTask.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task actualTask = gson.fromJson(response.body(), Task.class);
            // проверяем код ответа
            assertEquals(200, response.statusCode());

            assertEquals(expectedTask, actualTask, "Задачи не совпадают");
            assertEquals(expectedTask.getTitle(), actualTask.getTitle(), "Некорректное имя задачи");
        }
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(
                0,
                "Task 1",
                "Description of Task 1",
                State.NEW,
                LocalDateTime.now(),
                5
        );
        manager.createTask(task);
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            // проверяем, что создалась одна задача с корректным именем
            List<Task> expectedTasks = manager.getAllTasks();
            assertNotNull(expectedTasks, "Задачи не возвращаются");
            assertEquals(1, expectedTasks.size(), "Некорректное количество задач");
            assertEquals(task.getTitle(), expectedTasks.getFirst().getTitle(), "Некорректное имя задачи");
        }
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task expectedTask = new Task(
                0,
                "Task 1",
                "Description of Task 1",
                State.NEW,
                LocalDateTime.now(),
                5
        );
        manager.createTask(expectedTask);
        expectedTask.setTitle("New Title");
        String taskJson = gson.toJson(expectedTask);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks");
            HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task actualTask = gson.fromJson(response.body(), Task.class);
            // проверяем код ответа
            assertEquals(201, response.statusCode());

            assertNotNull(actualTask, "Задача не возвращается");
            assertEquals(expectedTask.getTitle(), actualTask.getTitle(), "Некорректное имя задачи");
        }
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(
                0,
                "Task 1",
                "Description of Task 1",
                State.NEW,
                LocalDateTime.now(),
                5
        );
        manager.createTask(task);

        // создаём HTTP-клиент и запрос
        try (HttpClient client = HttpClient.newHttpClient()) {
            URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
            HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            // проверяем код ответа
            assertEquals(204, response.statusCode());

            List<Task> expectedTasks = manager.getAllTasks();
            assertEquals(0, expectedTasks.size(), "Задача не удалилась");
        }
    }
}
