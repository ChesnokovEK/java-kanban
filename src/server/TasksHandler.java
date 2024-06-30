package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import enums.HttpMethodTypes;
import exceptions.InputParsingException;
import exceptions.NotFoundException;
import exceptions.OverlapException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private static final Pattern TASKS_PATTERN = Pattern.compile("^/tasks$");
    private static final Pattern TASKS_ID_PATTERN = Pattern.compile("^/tasks/\\d+$");

    @Override
    public boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException {
        switch (HttpMethodTypes.valueOf(requestMethod)) {
            case GET:
                if (TASKS_PATTERN.matcher(path).matches()) {
                    getTasks(httpExchange);
                    return true;
                } else if (TASKS_ID_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/tasks/".length());
                    findTask(httpExchange, parseInt(pathId));
                    return true;
                }
                break;

            case POST:
                if (TASKS_PATTERN.matcher(path).matches()) {
                    Task task = parseTask(getRequestBodyText(httpExchange));
                    editTask(httpExchange, task);
                    return true;
                }
                break;

            case DELETE:
                if (TASKS_ID_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/tasks/".length());
                    deleteTask(httpExchange, parseInt(pathId));
                    return true;
                }
                break;
        }

        return false;
    }

    private Task parseTask(String json) {
        try {
            return gson.fromJson(json, Task.class);
        } catch (JsonSyntaxException ex) {
            throw new InputParsingException("Ошибка преобразования к типу Task: " + json, ex);
        }
    }

    private void getTasks(HttpExchange httpExchange) throws IOException {
        Collection<Task> tasks = taskManager.getAllTasks();
        sendJson(httpExchange, 200, gson.toJson(tasks));
    }

    private void findTask(HttpExchange httpExchange, int id) throws IOException {
        Optional<Task> taskOpt = Optional.ofNullable(taskManager.getTaskById(id));
        Task task = taskOpt.orElseThrow(() -> new NotFoundException("Задача #" + id + " не найдена"));
        sendJson(httpExchange, 200, gson.toJson(task));
    }

    private void editTask(HttpExchange httpExchange, Task task) throws IOException {
        if (taskManager.getTaskById(task.getId()) == null) {
            taskManager.createTask(task);
            task = (new LinkedList<>(taskManager.getAllTasks())).getLast();
            if (!taskManager.getPrioritizedTasks().contains(task)) {
                throw new OverlapException(String.format("Задача #%d пересекается с другими", task.getId()));
            }
        } else {
            taskManager.updateTask(task);
        }
        sendJson(httpExchange, 201, gson.toJson(task));
    }

    private void deleteTask(HttpExchange httpExchange, int id) throws IOException {
        taskManager.removeTask(id);
        httpExchange.sendResponseHeaders(204, -1);
    }
}
