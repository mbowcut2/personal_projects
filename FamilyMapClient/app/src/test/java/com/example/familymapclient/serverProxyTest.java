package com.example.familymapclient;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.familymapclient.utils.ServerProxy;

import java.net.MalformedURLException;

import request.EventRequest;
import request.LoginRequest;
import request.PersonRequest;
import request.RegisterRequest;
import result.EventResult;
import result.LoginResult;
import result.PersonResult;
import result.RegisterResult;


public class serverProxyTest {

    ServerProxy proxy = new ServerProxy("localhost","8080");


    @Test
    public void registerSuccess() {

        RegisterResult result = proxy.register(
                new RegisterRequest(
                        "mckay",
                        "123",
                        "email",
                        "mckay",
                        "bowcut",
                        "m"
                )
        );

        assertTrue(result.isSuccess());
        assertNotNull(result.getUsername());
        assertNotNull(result.getPersonID());
        assertNotNull(result.getAuthtoken());

    }

    @Test
    public void registerFail() {

        RegisterResult result1 = proxy.register(
                new RegisterRequest(
                        "coolName",
                        "123",
                        "email",
                        "bill",
                        "murray",
                        "m"
                )
        );

        RegisterResult result2 = proxy.register(
                new RegisterRequest(
                        "coolName",
                        "123",
                        "email",
                        "bill",
                        "murray",
                        "m"
                )
        );

        assertFalse(result2.isSuccess());
        assertNull(result2.getUsername());
        assertNull(result2.getPersonID());
        assertNull(result2.getAuthtoken());

    }

    @Test
    public void loginSuccess() throws MalformedURLException {

        RegisterResult result1 = proxy.register(
                new RegisterRequest(
                        "woody",
                        "123",
                        "email",
                        "woody",
                        "goss",
                        "m"
                )
        );

        LoginResult result2 = proxy.login(new LoginRequest("woody","123"));

        assertTrue(result2.isSuccess());
        assertNotNull(result2.getAuthtoken());

    }

    @Test
    public void loginFail() throws MalformedURLException {

        LoginResult result = proxy.login(new LoginRequest("bad","info"));

        assertFalse(result.isSuccess());
        assertNull(result.getAuthtoken());

    }

    @Test
    public void getPeopleSuccess() {

        RegisterResult result1 = proxy.register(
                new RegisterRequest(
                        "joe",
                        "123",
                        "email",
                        "joe",
                        "dart",
                        "m"
                )
        );

        PersonResult result2 = proxy.getPeople(
                new PersonRequest(
                        result1.getAuthtoken()
                )
        );

        assertTrue(result2.isSuccess());
        assertNotNull(result2.getData());

    }

    @Test
    public void getPeopleFail() {

        PersonResult result = proxy.getPeople(
                new PersonRequest(
                        "notARealToken"
                )
        );

        assertFalse(result.isSuccess());
        assertNull(result.getData());

    }

    @Test
    public void getEventsSuccess() {

        RegisterResult result1 = proxy.register(
                new RegisterRequest(
                        "jack",
                        "123",
                        "email",
                        "jack",
                        "stratton",
                        "m"
                )
        );

        EventResult result2 = proxy.getEvents(
                new EventRequest(
                        result1.getAuthtoken()
                )
        );

        assertTrue(result2.isSuccess());
        assertNotNull(result2.getData());

    }

    @Test
    public void getEventsFail() {

        EventResult result = proxy.getEvents(
                new EventRequest(
                        "notARealToken"
                )
        );

        assertFalse(result.isSuccess());
        assertNull(result.getData());

    }

}
