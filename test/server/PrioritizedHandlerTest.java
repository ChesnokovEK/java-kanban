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

class PrioritizedHandlerTest {
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
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
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
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> actualPrioritizedTasks = gson.fromJson(response.body(),
                new TypeToken<List<Task>>() {
                }.getType());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertNotNull(actualPrioritizedTasks, "Приоритетные задачи не возвращаются");
        assertEquals(1, actualPrioritizedTasks.size(), "Некорректное количество приоритетных задач");
    }
}
