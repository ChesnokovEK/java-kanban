package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.InputParsingException;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;
    protected ErrorHandler errorHandler = new ErrorHandler();

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try (httpExchange) {
            try {
                String path = httpExchange.getRequestURI().getPath();
                String requestMethod = httpExchange.getRequestMethod();

                if (!processRequest(httpExchange, path, requestMethod)) {
                    sendText(httpExchange, 405, String.format("%s %s: неверный запрос", requestMethod, path));
                }
            } catch (Throwable t) {
                errorHandler.handle(httpExchange, t);
            }
        }
    }

    protected abstract boolean processRequest(HttpExchange httpExchange, String path, String requestMethod) throws IOException;

    protected static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InputParsingException("Ошибка преобразования к типу int: " + value, e);
        }
    }

    protected static String getRequestBodyText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected static void sendJson(HttpExchange httpExchange, int responseCode, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(responseCode, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected static void sendText(HttpExchange httpExchange, int responseCode, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "text/plain;charset=utf-8");
        httpExchange.sendResponseHeaders(responseCode, resp.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected static void printError(Throwable e) {
        for (; e != null; e = e.getCause()) {
            System.out.println(e.getMessage());
            if (e.getCause() == null)
                e.printStackTrace();
        }
    }
}
