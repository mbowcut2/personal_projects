import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.LoginRequest;
import request.PersonByIdRequest;
import result.FillResult;
import result.LoginResult;
import result.PersonByIdResult;
import service.AuthService;

import java.io.*;
import java.net.HttpURLConnection;
import com.google.gson.Gson;
import service.PersonByIdService;

public class PersonByIdHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Gson gson = new Gson();
        boolean success = false;
        try {

            if (exchange.getRequestMethod().toUpperCase().equals("GET")) {
                Headers reqHeaders = exchange.getRequestHeaders();
                if (reqHeaders.containsKey("Authorization")) {
                    String authToken = reqHeaders.getFirst("Authorization");
                    //TODO: perform .validate() in service class.
                    boolean valid = false;
                    try {
                        valid = new AuthService().validate(authToken);
                    } catch (DataAccessException e) {
                        e.printStackTrace();
                    }
                    if (valid) {
                        InputStream reqBody = exchange.getRequestBody();
                        String reqData = readString(reqBody);


                        PersonByIdRequest request = (PersonByIdRequest) gson.fromJson(reqData, PersonByIdRequest.class);
                        PersonByIdService service = new PersonByIdService();

                        PersonByIdResult result = service.process(request);

                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        OutputStream resBody = exchange.getResponseBody();
                        //gson.toJson(result, resBody); //TODO: what's wrong with this??
                        resBody.write(gson.toJson(result).getBytes()); //TODO: is this right?
                        resBody.close();

                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                        exchange.getResponseBody().close();

                        success = true;
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    }

                } else { exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0); }
            } else { exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0); }
            exchange.getResponseBody().close();
            if (!success) {
                //exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                FillResult result = new FillResult(false, "Error: Invalid input");

                OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                gson.toJson(result, resBody);
                resBody.close();

                exchange.getResponseBody().close();

            }
        } catch (IOException | DataAccessException e) {
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, 0);
            FillResult result = new FillResult(false, "Error: " + e);

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