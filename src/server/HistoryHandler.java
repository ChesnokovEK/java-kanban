package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import tasks.AbstractTask;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private static final Pattern HISTORY_PATTERN = Pattern.compile("^/history$");

    @Override
    public boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException {
        if ("GET".equals(requestMethod)) {
            if (HISTORY_PATTERN.matcher(path).matches()) {
                getHistory(httpExchange);
                return true;
            }
        }

        return false;
    }

    private void getHistory(HttpExchange httpExchange) throws IOException {
        List<AbstractTask> tasks = taskManager.getHistory();
        sendJson(httpExchange, 200, gson.toJson(tasks));
    }
}
