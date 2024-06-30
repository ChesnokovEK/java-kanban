package server;

import com.sun.net.httpserver.HttpExchange;
import exceptions.InputParsingException;
import exceptions.NotFoundException;
import exceptions.OverlapException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ErrorHandler {
    public void handle(HttpExchange httpExchange, Throwable throwable) {
        try {
            if (throwable instanceof NotFoundException) {
                writeResponse(httpExchange, throwable.getMessage(), 404);
            } else if (throwable instanceof OverlapException) {
                writeResponse(httpExchange, throwable.getMessage(), 406);
            } else if (throwable instanceof InputParsingException) {
                writeResponse(httpExchange, throwable.getMessage(), 400);
            } else {
                throwable.printStackTrace();
                writeResponse(httpExchange, throwable.getMessage(), 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeResponse(HttpExchange httpExchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = httpExchange.getResponseBody()) {
            httpExchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
        }
        httpExchange.close();
    }
}
