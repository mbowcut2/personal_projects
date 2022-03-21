package com.example.familymapclient.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.gson.Gson;

import request.ClearRequest;
import request.EventRequest;
import request.LoginRequest;
import request.PersonRequest;
import request.RegisterRequest;
import result.ClearResult;
import result.EventResult;
import result.LoginResult;
import result.PersonResult;
import result.RegisterResult;

public class ServerProxy { // ServerFacade  --> use ticketToRide Client class as example

    /**
     * Calls following server API's
     * login
     * register
     * getPeople
     * getEvents
     *
     * calls those methods with the request objects, gets result objects back, loads results into DataCache
     */

    private final String serverHost;
    private final String serverPort;
    private Gson gson = new Gson();

    public ServerProxy(String serverHost, String serverPort) {

        this.serverHost = serverHost;
        this.serverPort = serverPort;

    }

    public LoginResult login(LoginRequest request) throws MalformedURLException {

        try {

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept","application/json");
            http.connect();

            String reqData =
                    "{" +
                    "\"username\":"+  request.getUsername() + "," +
                    "\n" +
                    "\"password\":"+  request.getPassword() +
                    "}";

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();



            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();
                String respData = readString(respBody); //TODO define this function
                System.out.println(respData);

                return gson.fromJson(respData, LoginResult.class);



            }

            else {

                return new LoginResult(false,"ERROR: " + http.getResponseMessage());
            }




        } catch (IOException e) {
            e.printStackTrace();
            return new LoginResult(false, "Error: " + e);
        }
    }

    public RegisterResult register(RegisterRequest request) {
        try {

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept","application/json");
            http.connect();

            String reqData = //TODO: change to gson.toJson()
                    "{" +
                    "\"username\":" +  request.getUsername() + "," +
                    "\n" +
                    "\"password\":" +  request.getPassword() + "," +
                    "\n" +
                    "\"email\":" +  request.getEmail() + "," +
                    "\n" +
                    "\"firstName\":" +  request.getFirstName() + "," +
                    "\n" +
                    "\"lastName\":" +  request.getLastName() + "," +
                    "\n" +
                    "\"gender\":"  + request.getGender() +
                    "}";

            OutputStream reqBody = http.getOutputStream();
            writeString(reqData, reqBody);
            reqBody.close();



            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);
                System.out.println(respData);
                return gson.fromJson(respData, RegisterResult.class);
            }

            else {
                //InputStream respBody = http.getErrorStream();
                //String respData = readString(respBody);
                return new RegisterResult(false, "ERROR: " + http.getResponseMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new RegisterResult(false, "Error: " + e);
        }
    }

    public PersonResult getPeople(PersonRequest request) {
        try {

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", request.getAuthtoken());
            http.addRequestProperty("Accept","application/json");
            http.connect();



            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();

                String respData = readString(respBody); //TODO define this function

                System.out.println(respData);

                System.out.println(respData);
                return gson.fromJson(respData, PersonResult.class);


            }

            else {
                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream respBody = http.getErrorStream();

                String respData = readString(respBody);

                System.out.println(respData);


            }




        } catch (IOException e) {
            e.printStackTrace();
        }

        return new PersonResult(false,"Error");
    }

    public EventResult getEvents(EventRequest request) {
        try {

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");

            http.setDoOutput(false);

            http.addRequestProperty("Authorization", request.getAuthtoken());

            http.addRequestProperty("Accept","application/json");

            http.connect();



            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();

                String respData = readString(respBody);

                System.out.println(respData);

                System.out.println(respData);
                return gson.fromJson(respData, EventResult.class);
            }

            else {
                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream respBody = http.getErrorStream();

                String respData = readString(respBody);

                System.out.println(respData);


            }




        } catch (IOException e) {
            e.printStackTrace();
        }

        return new EventResult(false,"Error");
    }

    public ClearResult clear(ClearRequest request) {
        try {

            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept","application/json");
            http.connect();



            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {

                InputStream respBody = http.getInputStream();

                String respData = readString(respBody); //TODO define this function

                System.out.println(respData);

                System.out.println(respData);
                return gson.fromJson(respData, ClearResult.class);


            }

            else {
                System.out.println("ERROR: " + http.getResponseMessage());

                InputStream respBody = http.getErrorStream();

                String respData = readString(respBody);

                System.out.println(respData);


            }




        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ClearResult(false,"Error");
    }

    private static String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private static void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}


