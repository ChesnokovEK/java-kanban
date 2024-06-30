package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.State;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;

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

class EpicsHandlerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private final Gson gson = HttpTaskServer.getGson();
    private HttpClient client;

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
    public void testGetEpics() throws IOException, InterruptedException {
        manager.createEpic(new Epic("Epic 1", "Description of Epic 1"));

        // создаём HTTP-клиент и запрос
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Epic> actualEpics = gson.fromJson(response.body(),
                new TypeToken<List<Epic>>() {
                }.getType());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertNotNull(actualEpics, "Эпики не возвращаются");
        assertEquals(1, actualEpics.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetEpicSubtasks() throws IOException, InterruptedException {
        manager.createEpic(new Epic("Epic 1", "Description of Epic 1"));
        Epic epic = new LinkedList<>(manager.getAllEpics()).getLast();
        manager.createSubTask(
                new SubTask(
                    0,
                    "SubTask 1",
                    "SubTask 1 of Epic 1",
                    State.NEW,
                    LocalDateTime.now(),
                    5,
                    epic.getId()
                )
        );

        // создаём HTTP-клиент и запрос
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
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
    public void testFindEpic() throws IOException, InterruptedException {
        Epic expectedEpic = new Epic(
                0,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        manager.createEpic(expectedEpic);


        // создаём HTTP-клиент и запрос
        URI url = URI.create("http://localhost:8080/epics/" + expectedEpic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic actualEpic = gson.fromJson(response.body(), Epic.class);
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertEquals(expectedEpic, actualEpic, "Эпики не совпадают");
        assertEquals(expectedEpic.getTitle(), actualEpic.getTitle(), "Некорректное имя эпика");

    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        String epicJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> expectedEpics = manager.getAllEpics();
        assertNotNull(expectedEpics, "Эпики не возвращаются");
        assertEquals(1, expectedEpics.size(), "Некорректное количество эпиков");
        assertEquals(epic.getTitle(), new LinkedList<>(expectedEpics).getFirst().getTitle(), "Некорректное имя эпика");
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic expectedEpic = new Epic(
                0,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        manager.createEpic(expectedEpic);
        expectedEpic.setTitle("New name");
        String epicJson = gson.toJson(expectedEpic);

        // создаём HTTP-клиент и запрос
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic actualEpic = gson.fromJson(response.body(), Epic.class);
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        assertNotNull(actualEpic, "Эпик не возвращается");
        assertEquals(expectedEpic.getTitle(), actualEpic.getTitle(), "Некорректное имя эпика");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(
                0,
                "Epic-1",
                "Epic-1 Description",
                LocalDateTime.parse("2019-01-01T00:00"),
                40
        );
        manager.createEpic(epic);

        // создаём HTTP-клиент и запрос
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(204, response.statusCode());

        List<Epic> expectedEpics = manager.getAllEpics();
        assertEquals(0, expectedEpics.size(), "Эпик не удалился");
    }
}
