package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.ClearRequest;

import java.io.*;
import java.net.HttpURLConnection;

import result.ClearResult;
import result.FillResult;
import result.RegisterResult;
import service.ClearService;
import com.google.gson.Gson;

public class ClearHandler implements HttpHandler{

    /**
     * No AuthToken or request body.
     * @param exchange
     * @throws IOException
     */


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        Gson gson = new Gson();
        try {
            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                ClearRequest request = new ClearRequest();
                ClearService service = new ClearService();
                ClearResult result = service.process(request);
                System.out.println("clear result: " );
                System.out.println(gson.toJson(result));

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                gson.toJson(result, resBody);
                resBody.close();

                success = true;
            }
            if (!success) {
                //exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                ClearResult result = new ClearResult(false, "Error: Invalid input");

                OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                gson.toJson(result, resBody);
                resBody.close();

                exchange.getResponseBody().close();

            }
        } catch (IOException | DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            ClearResult result = new ClearResult(false, "Error: " + e);

            OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
            gson.toJson(result, resBody);
            resBody.close();

            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }
}
