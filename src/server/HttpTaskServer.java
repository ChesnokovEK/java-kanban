package server;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        this(Managers.getInMemoryTaskManager(), getGson());
    }

    public HttpTaskServer(TaskManager taskManager, Gson gson) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubTasksHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicsHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        HttpTaskServer taskServer = new HttpTaskServer();
        taskServer.start();
        System.out.println("Нажмите Enter для завершения работы сервера ...");

        scanner.nextLine();
        taskServer.stop();
    }

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void start() {
        httpServer.start();
        System.out.println("Запущен TaskServer на порту " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Остановили TaskServer на порту " + PORT);
    }
}