package com.example.familymapclient.tasks;

import com.example.familymapclient.utils.DataCache;
import com.example.familymapclient.utils.ServerProxy;

import request.EventRequest;
import result.EventResult;

public class GetEventsTask implements Runnable{


    private final String authtoken;
    private final String serverHost;
    private final String serverPort;
    private DataCache dataCache = DataCache.getInstance();

    public GetEventsTask (String serverHost, String serverPort, String authtoken) {

        this.authtoken = authtoken;
        this.serverHost = serverHost;
        this.serverPort = serverPort;

    }

    @Override
    public void run() {

        ServerProxy proxy = new ServerProxy(serverHost, serverPort);
        EventResult result = proxy.getEvents(new EventRequest(authtoken));

        if(result.isSuccess()) {
            dataCache.setEvents(result.getData());
            sendMessage();
        }

        else {
            sendMessage();
        }

    }

    private void sendMessage() {
        return;

    }

}
