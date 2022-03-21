package handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.LoginRequest;

import java.io.*;
import java.net.HttpURLConnection;
import com.google.gson.Gson;
import result.FillResult;
import service.LoginService;
import result.LoginResult;
import service.UserService;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        Gson gson = new Gson();
        try {

            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                InputStream reqBody = exchange.getRequestBody();
                String reqData = readString(reqBody);
                LoginRequest request = (LoginRequest) gson.fromJson(reqData, LoginRequest.class);

                if (new UserService().validateUsername(request.getUsername())) {

                    if(new UserService().validatePassword(request.getUsername(), request.getPassword())) {


                        LoginService service = new LoginService();

                        LoginResult result = service.process(request);
                        if (result.isSuccess()) {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        } else {
                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                        }
                        OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                        gson.toJson(result, resBody);
                        resBody.close();

                        success = result.isSuccess();
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                        LoginResult result = new LoginResult(false, "Error: invalid password");
                        OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                        gson.toJson(result, resBody);
                        resBody.close();
                    }
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    LoginResult result = new LoginResult(false, "Error: invalid username");
                    OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                    gson.toJson(result, resBody);
                    resBody.close();
                }
            }
            if (!success) {
                LoginResult result = new LoginResult(false, "Error: Invalid input");

                OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                gson.toJson(result, resBody);
                resBody.close();

                exchange.getResponseBody().close();

            }
        } catch (IOException | DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            LoginResult result = new LoginResult(false, "Error: " + e);

            OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
            gson.toJson(result, resBody);
            resBody.close();

            exchange.getResponseBody().close();
            e.printStackTrace();
        }
    }

    private String readString (InputStream is) throws IOException {
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
