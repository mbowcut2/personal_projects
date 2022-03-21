package handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.FillRequest;
import request.LoginRequest;
import result.FillResult;
import result.LoginResult;
import service.FillService;

import java.io.*;
import java.net.HttpURLConnection;

public class FillHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        boolean success = false;
        Gson gson = new Gson();
        try {

            if (exchange.getRequestMethod().toUpperCase().equals("POST")) {
                Headers reqHeaders = exchange.getRequestHeaders();

                String uri = exchange.getRequestURI().toString();
                String [] uriArray = uri.split("/");
                System.out.println(uriArray.length);

                for(int i = 0; i < uriArray.length; i++) {
                    System.out.println(uriArray[i]);
                }

                /*
                array: ["", "fill", "username", "generations"]
                if length = 4, then take in the requested generations
                else: do the standard 4 gen
                 */



                InputStream reqBody = exchange.getRequestBody();
                String reqData = readString(reqBody);

                String username = uriArray[2];
                int generations;
                if(uriArray.length == 4) {
                    generations = Integer.parseInt(uriArray[3]);
                }
                else { generations = 4; }




                //FillRequest request = (FillRequest) gson.fromJson(reqData, FillRequest.class);
                FillRequest request = new FillRequest(username, generations);
                FillService service = new FillService();

                FillResult result = service.process(request);

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                OutputStreamWriter resBody = new OutputStreamWriter(exchange.getResponseBody());
                gson.toJson(result, resBody); //TODO: what's wrong with this??
                //resBody.write(gson.toJson(result).getBytes()); //TODO: is this right?
                resBody.close();

                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                exchange.getResponseBody().close();

                success = true;




            } else {
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
            }
            //exchange.getResponseBody().close();
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