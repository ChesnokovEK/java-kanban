package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.AbstractTask;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    private static final Pattern PRIORITIZED_PATTERN = Pattern.compile("^/prioritized$");

    @Override
    public boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException {
        if ("GET".equals(requestMethod)) {
            if (PRIORITIZED_PATTERN.matcher(path).matches()) {
                getPrioritizedTasks(httpExchange);
                return true;
            }
        }

        return false;
    }

    private void getPrioritizedTasks(HttpExchange httpExchange) throws IOException {
        List<AbstractTask> tasks = taskManager.getPrioritizedTasks();
        sendJson(httpExchange, 200, gson.toJson(tasks));
    }
}
