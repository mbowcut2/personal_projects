package com.example.familymapclient;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.familymapclient.utils.DataCache;
import com.example.familymapclient.utils.ServerProxy;

import model.*;
import request.EventRequest;
import request.LoginRequest;
import request.PersonRequest;
import result.LoginResult;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedList;

public class modelTest {



    /*@Before
    public void setup() {

    }*/

    /**
     * These tests assume the LoadData.json file has been loaded into the database
     */

    @Test
    public void relationshipImmediate() throws MalformedURLException {
        DataCache dataCache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy("localhost","8080");


        LoginResult result = proxy.login(new LoginRequest("sheila","parker"));

        dataCache.setPersons(proxy.getPeople(new PersonRequest(result.getAuthtoken())).getData());
        dataCache.setEvents(proxy.getEvents(new EventRequest(result.getAuthtoken())).getData());
        dataCache.setFilters();
        dataCache.setPersonEvents();
        Person person = dataCache.getPerson(result.getPersonID());
        dataCache.setAncestors(person);

        assertEquals(dataCache.getPerson(person.getMotherID()).getFirstName(), "Betty");
        assertEquals(dataCache.getPerson(person.getFatherID()).getFirstName(), "Blaine");
        assertEquals(dataCache.getPerson(person.getSpouseID()).getFirstName(), "Davis");

    }

    @Test
    public void relationshipAncestors() throws MalformedURLException {
        DataCache dataCache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy("localhost","8080");

        LoginResult result = proxy.login(new LoginRequest("sheila","parker"));

        dataCache.setPersons(proxy.getPeople(new PersonRequest(result.getAuthtoken())).getData());
        dataCache.setEvents(proxy.getEvents(new EventRequest(result.getAuthtoken())).getData());
        dataCache.setFilters();
        dataCache.setPersonEvents();
        Person person = dataCache.getPerson(result.getPersonID());
        dataCache.setAncestors(person);

        HashSet<Person> momSide = dataCache.getMaternalAncestors();
        HashSet<Person> dadSide = dataCache.getPaternalAncestors();


        assertEquals(18,dadSide.size());
        assertEquals(18,momSide.size());

    }

    @Test
    public void filterSuccess() throws MalformedURLException {
        DataCache dataCache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy("localhost","8080");

        LoginResult result = proxy.login(new LoginRequest("sheila","parker"));

        dataCache.setPersons(proxy.getPeople(new PersonRequest(result.getAuthtoken())).getData());
        dataCache.setEvents(proxy.getEvents(new EventRequest(result.getAuthtoken())).getData());
        dataCache.setFilters();
        dataCache.setPersonEvents();
        Person person = dataCache.getPerson(result.getPersonID());
        dataCache.setAncestors(person);

        dataCache.setMale(false);
        dataCache.setFilters();
        dataCache.setPersonEvents();

        assertNotNull(dataCache.getEventsByID(person.getMotherID()));
        assertNotEquals(dataCache.getEventsByID(person.getMotherID()).size(), 0);

    }

    @Test
    public void filterFail() throws MalformedURLException {
        DataCache dataCache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy("localhost","8080");

        LoginResult result = proxy.login(new LoginRequest("sheila","parker"));

        dataCache.setPersons(proxy.getPeople(new PersonRequest(result.getAuthtoken())).getData());
        dataCache.setEvents(proxy.getEvents(new EventRequest(result.getAuthtoken())).getData());
        dataCache.setFilters();
        dataCache.setPersonEvents();
        Person person = dataCache.getPerson(result.getPersonID());
        dataCache.setAncestors(person);

        dataCache.setMale(false);
        dataCache.setFilters();
        dataCache.setPersonEvents();

        assertNotNull(dataCache.getEventsByID(person.getFatherID()));
        assertEquals(dataCache.getEventsByID(person.getFatherID()).size(), 0);

    }

    @Test
    public void chronologicalSuccess() throws MalformedURLException {
        DataCache dataCache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy("localhost","8080");

        LoginResult result = proxy.login(new LoginRequest("sheila","parker"));

        dataCache.setPersons(proxy.getPeople(new PersonRequest(result.getAuthtoken())).getData());
        dataCache.setEvents(proxy.getEvents(new EventRequest(result.getAuthtoken())).getData());
        dataCache.setFilters();
        dataCache.setPersonEvents();
        Person person = dataCache.getPerson(result.getPersonID());
        dataCache.setAncestors(person);

        dataCache.setSelectedPerson(person);

        LinkedList<Event> events = dataCache.getLifeStory();
        for (int i = 0; i < events.size() - 1; i++) {
            assertTrue(events.get(i).getYear() <= events.get(i+1).getYear());
        }

    }

    @Test
    public void chronologicalFail() throws MalformedURLException {
        DataCache dataCache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy("localhost","8080");

        LoginResult result = proxy.login(new LoginRequest("sheila","parker"));

        dataCache.setPersons(proxy.getPeople(new PersonRequest(result.getAuthtoken())).getData());
        dataCache.setEvents(proxy.getEvents(new EventRequest(result.getAuthtoken())).getData());
        dataCache.setFilters();
        dataCache.setPersonEvents();
        Person person = dataCache.getPerson(result.getPersonID());
        dataCache.setAncestors(person);

        Event [] events = dataCache.getEvents();
        boolean ordered = true;

        for (int i = 0; i < events.length - 1; i++) {
            if (events[i].getYear() > events[i+1].getYear()) {
                    ordered = false;
            }
        }

        assertFalse(ordered);

    }

    @Test
    public void searchSuccess() throws MalformedURLException {
        DataCache dataCache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy("localhost","8080");

        LoginResult result = proxy.login(new LoginRequest("sheila","parker"));

        dataCache.setPersons(proxy.getPeople(new PersonRequest(result.getAuthtoken())).getData());
        dataCache.setEvents(proxy.getEvents(new EventRequest(result.getAuthtoken())).getData());
        dataCache.setFilters();
        dataCache.setPersonEvents();
        Person person = dataCache.getPerson(result.getPersonID());
        dataCache.setAncestors(person);

        LinkedList<Event> eventQuery = dataCache.searchEvents("asteroid");
        LinkedList<Person> personQuery = dataCache.searchPersons("sheila");

        assertNotEquals(personQuery.size(), 0);
        assertNotEquals(eventQuery.size(), 0);

        assertEquals(personQuery.size(), 1);
        assertEquals(eventQuery.size(), 2);

    }

    @Test
    public void searchFail() throws MalformedURLException {
        DataCache dataCache = DataCache.getInstance();
        ServerProxy proxy = new ServerProxy("localhost","8080");

        LoginResult result = proxy.login(new LoginRequest("sheila","parker"));

        dataCache.setPersons(proxy.getPeople(new PersonRequest(result.getAuthtoken())).getData());
        dataCache.setEvents(proxy.getEvents(new EventRequest(result.getAuthtoken())).getData());
        dataCache.setFilters();
        dataCache.setPersonEvents();
        Person person = dataCache.getPerson(result.getPersonID());
        dataCache.setAncestors(person);

        LinkedList<Event> eventQuery = dataCache.searchEvents("abc");
        LinkedList<Person> personQuery = dataCache.searchPersons("abc");

        assertEquals(personQuery.size(), 0);
        assertEquals(eventQuery.size(), 0);

    }
}
