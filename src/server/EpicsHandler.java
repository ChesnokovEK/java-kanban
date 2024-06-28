package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import exceptions.InputParsingException;
import exceptions.NotFoundException;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private static final Pattern EPICS_PATTERN = Pattern.compile("^/epics$");
    private static final Pattern EPICS_ID_PATTERN = Pattern.compile("^/epics/\\d+$");
    private static final Pattern EPIC_SUBTASKS_PATTERN = Pattern.compile("^/epics/\\d+/subtasks$");

    @Override
    public boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException {
        switch (requestMethod) {
            case "GET":
                if (EPICS_PATTERN.matcher(path).matches()) {
                    getEpics(httpExchange);
                    return true;
                } else if (EPICS_ID_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/epics/".length());
                    findEpic(httpExchange, parseInt(pathId));
                    return true;
                } else if (EPIC_SUBTASKS_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/epics/".length())
                            .replace("/subtasks", "");
                    getEpicSubtasks(httpExchange, parseInt(pathId));
                    return true;
                }
                break;

            case "POST":
                if (EPICS_PATTERN.matcher(path).matches()) {
                    Epic epic = parseEpic(getRequestBodyText(httpExchange));
                    editTask(httpExchange, epic);
                    return true;
                }
                break;

            case "DELETE":
                if (EPICS_ID_PATTERN.matcher(path).matches()) {
                    String pathId = path.substring("/epics/".length());
                    deleteEpic(httpExchange, parseInt(pathId));
                    return true;
                }
                break;
        }

        return false;
    }

    private Epic parseEpic(String json) {
        try {
            return gson.fromJson(json, Epic.class);
        } catch (JsonSyntaxException ex) {
            throw new InputParsingException("Ошибка преобразования к типу Epic: " + json, ex);
        }
    }

    private void getEpics(HttpExchange httpExchange) throws IOException {
        Collection<Epic> epics = taskManager.getAllEpics();
        sendJson(httpExchange, 200, gson.toJson(epics));
    }

    private void findEpic(HttpExchange httpExchange, int id) throws IOException {
        Optional<Epic> epicOpt = Optional.ofNullable(taskManager.getEpicById(id));
        Epic epic = epicOpt.orElseThrow(() -> new NotFoundException("Эпик #" + id + " не найден"));
        sendJson(httpExchange, 200, gson.toJson(epic));
    }

    private void getEpicSubtasks(HttpExchange httpExchange, int id) throws IOException {
        Collection<SubTask> subtasks = taskManager.getEpicById(id).getAllRelatedSubTasks();
        sendJson(httpExchange, 200, gson.toJson(subtasks));
    }

    private void editTask(HttpExchange httpExchange, Epic epic) throws IOException {
        if (taskManager.getEpicById(epic.getId()) == null) {
            taskManager.createEpic(epic);
            epic = (new LinkedList<>(taskManager.getAllEpics())).getLast();
        } else {
            taskManager.updateEpic(epic);
        }
        sendJson(httpExchange, 201, gson.toJson(epic));
    }

    private void deleteEpic(HttpExchange httpExchange, int id) throws IOException {
        taskManager.removeEpic(id);
        httpExchange.sendResponseHeaders(204, -1);
    }
}