package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import model.Person;
import request.EventByIdRequest;
import request.PersonByIdRequest;
import request.PersonRequest;
import result.EventByIdResult;
import result.PersonByIdResult;
import result.PersonResult;
import result.Result;
import service.AuthService;
import service.PersonByIdService;

import java.io.*;
import java.net.HttpURLConnection;

import service.PersonService;

public class PersonHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
        boolean success = false;

        try {

            String uri = exchange.getRequestURI().toString();
            String [] uriArray = uri.split("/");
            System.out.println(uriArray.length);
            boolean byId = (uriArray.length == 3);


            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");
                    boolean valid = false;
                    try {
                        valid = new AuthService().validate(authToken);
                    } catch (DataAccessException e) {
                        e.printStackTrace();
                    }
                    if (valid) {
                        success = true;
                        if (byId) {

                            PersonByIdRequest request = new PersonByIdRequest(uriArray[2], authToken);
                            PersonByIdService service = new PersonByIdService();
                            PersonByIdResult result = service.process(request);

                            if (result.isSuccess()) {
                                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            } else {
                                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            }

                            OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                            gson.toJson(result, resBody);
                            resBody.close();

                            exchange.getResponseBody().close();
                        } else {
                            PersonRequest request = new PersonRequest(authToken);
                            PersonService service = new PersonService();

                            PersonResult result = service.process(request);
                            if (result.isSuccess()) {
                                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            } else {
                                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            }
                            OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                            gson.toJson(result, resBody);
                            resBody.close();

                            exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                            exchange.getResponseBody().close();

                        }
                    }

                }
                   /* else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                        Result result;
                        if (byId) {
                            result = new PersonByIdResult(false, "Error: Invalid authtoken");
                        }
                        else {
                            result = new PersonResult(false, "Error: Invalid authtoken");
                        }
                        OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                        gson.toJson(result, resBody);
                        resBody.close();
                    } */
            }

            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                Result result;
                if (byId) {
                    result = new PersonByIdResult(false, "Error: Invalid authtoken");
                }
                else {
                    result = new PersonResult(false, "Error: Invalid authtoken");
                }
                OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                gson.toJson(result, resBody);
                resBody.close();
                exchange.getResponseBody().close();
            }
        }
        catch (IOException | DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            exchange.getResponseBody().close();
            e.printStackTrace();
            PersonResult result = new PersonResult(false, "Error: " + e);
            OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
            gson.toJson(result, resBody);
            resBody.close();
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