package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import enums.HttpMethodTypes;
import exceptions.InputParsingException;
import exceptions.NotFoundException;
import exceptions.OverlapException;
import manager.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Pattern;

public class SubTasksHandler extends BaseHttpHandler {
    public SubTasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private static final Pattern SUBTASKS_PATTERN = Pattern.compile("^/subtasks$");
    private static final Pattern SUBTASKS_ID_PATTERN = Pattern.compile("^/subtasks/\\d+$");

    @Override
    public boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException {
        switch (HttpMethodTypes.valueOf(requestMethod)) {
            case GET:
                if (SUBTASKS_PATTERN.matcher(path).matches()) {
                    getSubtasks(httpExchange);
                    return true;
                } else if (SUBTASKS_ID_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/subtasks/".length());
                    findSubtask(httpExchange, parseInt(pathId));
                    return true;
                }
                break;

            case POST:
                if (SUBTASKS_PATTERN.matcher(path).matches()) {
                    SubTask subtask = parseSubtask(getRequestBodyText(httpExchange));
                    editSubtask(httpExchange, subtask);
                    return true;
                }
                break;

            case DELETE:
                if (SUBTASKS_ID_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/subtasks/".length());
                    deleteSubtask(httpExchange, parseInt(pathId));
                    return true;
                }
                break;
        }

        return false;
    }

    private SubTask parseSubtask(String json) {
        try {
            return gson.fromJson(json, SubTask.class);
        } catch (JsonSyntaxException e) {
            throw new InputParsingException("Ошибка преобразования к типу Subtask: " + json, e);
        }
    }

    private void getSubtasks(HttpExchange httpExchange) throws IOException {
        Collection<SubTask> subTasks = taskManager.getAllSubTasks();
        sendJson(httpExchange, 200, gson.toJson(subTasks));
    }

    private void findSubtask(HttpExchange httpExchange, int id) throws IOException {
        Optional<SubTask> subTaskOpt = Optional.ofNullable(taskManager.getSubTaskById(id));
        SubTask subTask = subTaskOpt.orElseThrow(() -> new NotFoundException("Подзадача #" + id + " не найдена"));
        sendJson(httpExchange, 200, gson.toJson(subTask));
    }

    private void editSubtask(HttpExchange httpExchange, SubTask subTask) throws IOException {
        if (taskManager.getSubTaskById(subTask.getId()) == null) {
            taskManager.createSubTask(subTask);
            subTask = (new LinkedList<>(taskManager.getAllSubTasks())).getLast();
            if (!taskManager.getPrioritizedTasks().contains(subTask)) {
                throw new OverlapException(String.format("Подзадача #%d пересекается с другими", subTask.getId()));
            }
        } else {
            taskManager.updateSubTask(subTask);
        }

        sendJson(httpExchange, 201, gson.toJson(subTask));
    }

    private void deleteSubtask(HttpExchange httpExchange, int id) throws IOException {
        taskManager.removeSubTask(id);
        httpExchange.sendResponseHeaders(204, -1);
    }
}
