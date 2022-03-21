package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.FillRequest;
import request.LoginRequest;
import request.RegisterRequest;
import result.FillResult;
import result.LoginResult;
import result.RegisterResult;
import service.FillService;
import service.RegisterService;

import java.io.*;
import java.net.HttpURLConnection;

public class RegisterHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        /**
         * Use RegisterService to create the new user / add to db
         * Use FillService to generate the 4 generations.
         */

        RegisterResult result;
        boolean success = false;
        Gson gson = new Gson();
        try {

            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {

                InputStream reqBody = exchange.getRequestBody();
                String reqData = readString(reqBody);


                RegisterRequest request = (RegisterRequest) gson.fromJson(reqData, RegisterRequest.class);
                RegisterService service = new RegisterService();
                result = service.process(request);
                if (result.isSuccess()) {

                    FillRequest fill = new FillRequest(request.getUsername(), 4);
                    new FillService().process(fill);

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                    gson.toJson(result, resBody);
                    resBody.close();
                    exchange.getResponseBody().close();

                    success = true;
                }

                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    //result = new RegisterResult(false, "Error: Invalid Request.");

                    OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                    gson.toJson(result, resBody);
                    resBody.close();

                    exchange.getResponseBody().close();

                }
            }

        } catch (IOException | DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            result = new RegisterResult(false, "Error: " + e);

            OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
            gson.toJson(result, resBody);
            resBody.close();

            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }

    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }
}