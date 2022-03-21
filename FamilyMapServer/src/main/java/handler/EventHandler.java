package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.EventByIdRequest;
import request.EventRequest;
import request.LoginRequest;
import result.EventByIdResult;
import result.EventResult;
import result.PersonByIdResult;
import result.Result;
import service.AuthService;
import service.EventByIdService;
import service.EventService;

import java.io.*;
import java.net.HttpURLConnection;

public class EventHandler implements HttpHandler{
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
                    String authtoken = reqHeaders.getFirst("Authorization");
                    //TODO: perform .validate() in service class.
                    boolean valid = false;
                    try {
                        valid = new AuthService().validate(authtoken);
                    } catch (DataAccessException e) {
                        e.printStackTrace();
                    }
                    if (valid) {
                        success = true;
                        if (byId) {
                            EventByIdRequest request = new EventByIdRequest(uriArray[2], authtoken);
                            EventByIdService service = new EventByIdService();
                            EventByIdResult result = service.process(request);

                            if (result.isSuccess()) {
                                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                            }
                            else {
                                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            }

                                OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                                gson.toJson(result, resBody);
                                resBody.close();
                                exchange.getResponseBody().close();
                        }

                        else {
                            EventRequest request = new EventRequest(authtoken);
                            EventService service = new EventService();

                            EventResult result = service.process(request);
                            if(result.isSuccess()) {
                                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                                success = true;
                            }
                            else {
                                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                            }
                            OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                            gson.toJson(result, resBody);
                            resBody.close();
                            exchange.getResponseBody().close();
                        }
                    }
                    /*else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                        Result result;
                        if(byId) {
                            result = new EventByIdResult(false, "Error: Invalid authtoken");
                        }
                        else {
                            result = new EventResult(false, "Error: Invalid authtoken");
                        }
                        OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                        gson.toJson(result, resBody);
                        resBody.close();
                    }*/
                }
            }
            //exchange.getResponseBody().close();
            if (!success) {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                Result result;
                if(byId) {
                    result = new EventByIdResult(false, "Error: Invalid authtoken");
                }
                else {
                    result = new EventResult(false, "Error: Invalid authtoken");
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
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            EventResult result = new EventResult(false, "Error: " + e);
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