package com.example.familymapclient.utils;

import com.google.android.gms.maps.model.Polyline;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.Event;
import model.Person;


public class DataCache {

    private static DataCache instance;

    public static DataCache getInstance() {
        if(instance == null) {
            instance = new DataCache();
        }

        return instance;
    }

    private DataCache() {
    }

    private HashMap<String, Person> persons = new HashMap<>();
    private HashMap<String, Event> events = new HashMap<>();
    private HashMap<String, Event> filteredEvents = new HashMap<>();
    private HashMap<String, LinkedList<Event>> personEvents = new HashMap<>();
    private HashSet<Person> paternalAncestors = new HashSet<>(); //TODO: incredible
    private HashSet<Person> maternalAncestors = new HashSet<>(); //TODO: foresight
    private Person selectedPerson;
    private Event selectedEvent = null;
    private Person mapPerson;
    private Event mapEvent;
    private LinkedList<Polyline> lines= new LinkedList<>();
    private HashMap<String, Float> colorMap;

    //SETTINGS

    private boolean lifeStory = true;
    private boolean familyTree = true;
    private boolean spouse = true;
    private boolean fatherSide = true;
    private boolean motherSide = true;
    private boolean male = true;
    private boolean female = true;

    public LinkedList<Polyline> getLines() {
        return lines;
    }

    public void addLine(Polyline line) {
        this.lines.add(line);
    }

    public void clearLines() {
        this.lines.clear();
    }

    public Event getMapEvent() {
        return mapEvent;
    }

    public void setMapEvent(Event mapEvent) {
        this.mapEvent = mapEvent;
    }

    public Person getMapPerson() {
        return mapPerson;
    }

    public void setMapPerson(Person mapPerson) {
        this.mapPerson = mapPerson;
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public boolean isLifeStory() {
        return lifeStory;
    }

    public void setLifeStory(boolean lifeStory) {
        this.lifeStory = lifeStory;
    }

    public boolean isFamilyTree() {
        return familyTree;
    }

    public void setFamilyTree(boolean familyTree) {
        this.familyTree = familyTree;
    }

    public boolean isSpouse() {
        return spouse;
    }

    public void setSpouse(boolean spouse) {
        this.spouse = spouse;
    }

    public boolean isFatherSide() {
        return fatherSide;
    }

    public void setFatherSide(boolean fatherSide) {
        this.fatherSide = fatherSide;
    }

    public boolean isMotherSide() {
        return motherSide;
    }

    public void setMotherSide(boolean motherSide) {
        this.motherSide = motherSide;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public HashMap<String, Float> getColorMap() {
        return colorMap;
    }

    public void setColorMap(HashMap<String, Float> colorMap) {
        this.colorMap = colorMap;
    }

    public HashSet<Person> getPaternalAncestors() {
        return paternalAncestors;
    }

    public HashSet<Person> getMaternalAncestors() {
        return maternalAncestors;
    }

    public Person getSelectedPerson() {
        return selectedPerson;
    }

    public void setSelectedPerson(Person selectedPerson) { //TODO: this function should fill the ancestors for selected person.

        this.selectedPerson = selectedPerson;

        setAncestors(selectedPerson);

    }

    public HashMap<String, LinkedList<Event>> getPersonEvents() {
        return personEvents;
    }

    public void setAncestors(Person person) {

        if (person.getMotherID() == null && person.getFatherID() == null) {
            return;
        }

        Person father = persons.get(person.getFatherID());
        Person mother = persons.get(person.getMotherID());

        if(father != null) {
            paternalAncestors.add(father);
            setAncestors(father);
        }
        if(mother != null) {
            maternalAncestors.add(mother);
            setAncestors(mother);
        }

    }

    public HashSet<Person> getImmediateFamily() {
        /**
         * get's the immediate family of the selectedPerson and returns in a set
         */

        HashSet<Person> family = new HashSet<>();
        if(selectedPerson.getFatherID() != null) {
            family.add(persons.get(selectedPerson.getFatherID()));
        }
        if(selectedPerson.getMotherID() != null) {
            family.add(persons.get(selectedPerson.getMotherID()));
        }
        if(selectedPerson.getSpouseID() != null) {
            family.add(persons.get(selectedPerson.getSpouseID()));
        }

        Person [] arr = (Person[]) persons.values().toArray(new Person[0]);

        switch (selectedPerson.getGender()) {
            case ("m"):
                for(int i = 0; i < persons.values().size(); i++) {
                    if (arr[i].getFatherID() != null) {
                        if (arr[i].getFatherID().equals(selectedPerson.getPersonID())) {
                            family.add((Person) arr[i]);
                        }
                    }
                }
            case ("f"):
                for(int i = 0; i < persons.values().size(); i++) {
                    if (arr[i].getMotherID() != null) {
                        if ( arr[i].getMotherID().equals(selectedPerson.getPersonID())) {
                            family.add((Person) arr[i]);
                        }
                    }
                }
        }

        return family;
    }

    public void setPersonEvents() {
        LinkedList<Event> ev = new LinkedList<>(filteredEvents.values());
        LinkedList<Person> pe = new LinkedList<>(persons.values());

        //TODO: implement settings filters

        for (Person person : pe) {
            personEvents.put(person.getPersonID(), new LinkedList<Event>());
            for (Event event: ev) {
                if (event.getPersonID().equals(person.getPersonID())) {
                    personEvents.get(person.getPersonID()).add(event);
                }
            }
        }
    }

    public HashMap<String, Person> getPersons() {
        return persons;
    }

    public Person getPerson(String personID) {
        return persons.get(personID);
    }

    public void setPersons(Person[] arr) {
        for (Person person : arr) {
            this.persons.put(person.getPersonID(), person);
        }
    }


    public Event getEvent(String eventID) {
        return this.events.get(eventID);
    }

    public void setEvents(Event [] arr) {
        for (Event event : arr) {
            this.events.put(event.getEventID(), event);
        }
        setFilters();
    }

    public Event [] getEvents() {
        Event [] arr = new Event [events.values().size()];
        for (int i = 0; i < events.values().size(); i++) {
            arr[i] = (Event) events.values().toArray()[i];
        }
        return arr; //TODO: getting null pointer exception
    }

    private String authtoken;

    public Person getPersonById(String personID) {
        return persons.get(personID);
    }

    public LinkedList<Event> getEventsByID(String personID) {
        return personEvents.get(personID);
    }

    public Collection<Event> getFilteredEvents() {
        return filteredEvents.values();
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public HashMap<Person, Event> getFamilyTreeEvents() { //TODO: I might not use this, since the lines are drawn recursively...
        return null;
    }

    public LinkedList<Event> getLifeStory() {

        LinkedList<Event> events = getEventsByID(selectedPerson.getPersonID());


        Collections.sort(events, new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                if(o1.getYear() < o2.getYear()) {
                    return -1;
                }
                if(o1.getYear() > o2.getYear()) {
                    return 1;
                }
                else { return 0; }
            }
        });

        return events;
    }


    public LinkedList<Person> searchPersons(String searchString) {
        LinkedList<Person> results = new LinkedList<>();
        StringBuilder charSeq = new StringBuilder(searchString.toLowerCase());

        for (Person person: persons.values()) {
            if(person.getFirstName().toLowerCase().contains(charSeq)) {
                results.add(person);
            }
            else if(person.getLastName().toLowerCase().contains(charSeq)) {
                results.add(person);
            }
        }

        return results;
    }

    public LinkedList<Event> searchEvents(String searchString) {
        LinkedList<Event> results = new LinkedList<>();
        StringBuilder charSeq = new StringBuilder(searchString.toLowerCase());

        for (Event event: filteredEvents.values()) {
            if(event.getCountry().toLowerCase().contains(charSeq)) {
                results.add(event);
            }

            else if(event.getCity().toLowerCase().contains(charSeq)) {
                results.add(event);
            }

            else if(event.getEventType().toLowerCase().contains(charSeq)) {
                results.add(event);
            }

            else if(new Integer(event.getYear()).toString().toLowerCase().contains(charSeq)) {
                results.add(event);
            }


        }

        return results;
    }

    public void setFilters() {
        //copy to filtered
        for (Event event: events.values()) {
            filteredEvents.put(event.getEventID(),event);
        }

        if (!isFemale()) {
            //filter out females
            for (Event event: events.values()) {
                if(persons.get(event.getPersonID()).getGender().equals("f")) {
                    filteredEvents.remove(event.getEventID());
                }
            }

        }

        if (!isMale()) {
            //filter out males
            for (Event event: events.values()) {
                if(persons.get(event.getPersonID()).getGender().equals("m")) {
                    filteredEvents.remove(event.getEventID());
                }
            }

        }

        if(!isFatherSide()) {
            for (Event event: events.values()) {
                if(paternalAncestors.contains(persons.get(event.getPersonID()))) {
                    filteredEvents.remove(event.getEventID());
                }
            }
        }

        if(!isMotherSide()) {
            for (Event event: events.values()) {
                if(maternalAncestors.contains(persons.get(event.getPersonID()))) {
                    filteredEvents.remove(event.getEventID());
                }
            }
        }
    }


}
