package service;

import dao.*;
import request.FillRequest;
import result.FillResult;
import com.google.gson.Gson;
import java.io.File;
import model.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Random;
import java.util.UUID;


public class FillService {

    /**
     * Populates the server's database with generated data for the specified user name. The required "username" parameter must be a user already registered with the server. If there is any data in the database already associated with the given user name, it is deleted. The optional “generations” parameter lets the caller specify the number of generations of ancestors to be generated, and must be a non-negative integer (the default is 4, which results in 31 new persons each with associated events).
     */

    private Locations locations;
    private Names femaleNames;
    private Names maleNames;
    private Names surnames;
    private Person [] persons = new Person [0];
    private Event [] events = new Event [0];
    private int gen;
    private User user;
    private Database db = new Database();

    public FillResult process(FillRequest request) throws FileNotFoundException, DataAccessException {
        /**
         * Populates the database with information for the specified user. Returns a FillResult object.
         */
        this.gen = request.getGenerations();

        /*
        TODO: validate username exists, clear database for username.
         */

        String fNamesPath = "json/fnames.json";
        String locationsPath = "json/locations.json";
        String mNamesPath = "json/mnames.json";
        String sNamesPath = "json/snames.json";

        Gson gson = new Gson();

        db.openConnection();

        UserDao uDao = new UserDao(db.getConnection());
        this.user = uDao.find(request.getUsername());

        db.closeConnection(true);

        if(this.user == null) {
            FillResult result = new FillResult(false, "Error: invalid username");
            return result;
        }



        File femaleNameFile = new File(fNamesPath);
        File maleNameFile = new File(mNamesPath);
        File surnameFile = new File(sNamesPath);
        File locationsFile = new File(locationsPath);

        FileReader femaleNameReader = new FileReader(femaleNameFile);
        FileReader maleNameReader = new FileReader(maleNameFile);
        FileReader surnameReader = new FileReader(surnameFile);
        FileReader locationsReader = new FileReader(locationsFile);

        this.locations = gson.fromJson(locationsReader, Locations.class);
        this.femaleNames = gson.fromJson(femaleNameReader, Names.class);
        this.maleNames = gson.fromJson(maleNameReader, Names.class);
        this.surnames = gson.fromJson(surnameReader, Names.class);

        //random index from array

        generate(request.getGenerations(), request.getUsername());
        db.openConnection();

        try {
            EventDao eDao = new EventDao(db.getConnection());
            PersonDao pDao = new PersonDao(db.getConnection());
            eDao.clear(request.getUsername());
            pDao.clear(request.getUsername());

            for (int i = 0; i < this.persons.length; i++) {
                //System.out.println(this.persons[i].getPersonID());
                pDao.insert(this.persons[i]);
            }

            for (int i = 0; i < this.events.length; i++) {
                eDao.insert(this.events[i]);
            }

            db.closeConnection(true);

        } catch (DataAccessException e) {
            db.closeConnection(false);
            e.printStackTrace();
            return new FillResult(false, "Error: " + e);
        }

        return new FillResult(true, "Successfully added " + this.persons.length + " persons and " + events.length + " events to the database");

    }

    private void generateParents(Person child, int gen, int birthYear) {
        /**
         * handle person/events for all parents -- recursive
         */

        if(gen == 1) {

            Person father = new Person(
                    child.getFatherID(),
                    this.user.getUsername(),
                    this.maleNames.getData()[new Random().nextInt(this.maleNames.getData().length)],
                    child.getLastName(),
                    "m",
                    null,
                    null,
                    child.getMotherID());

            addPerson(father);

            Person mother = new Person(
                    child.getMotherID(),
                    this.user.getUsername(),
                    this.femaleNames.getData()[new Random().nextInt(this.femaleNames.getData().length)],
                    this.surnames.getData()[new Random().nextInt(this.surnames.getData().length)],
                    "f",
                    null,
                    null,
                    child.getFatherID());

            addPerson(mother);

            int index = new Random().nextInt(this.locations.getData().length);

            Event fatherBirth = new Event(
                    UUID.randomUUID().toString(),
                    child.getAssociatedUsername(),
                    child.getFatherID(),
                    this.locations.getData()[index].getLatitude(),
                    this.locations.getData()[index].getLongitude(),
                    this.locations.getData()[index].getCountry(),
                    this.locations.getData()[index].getCity(),
                    "Birth",
                    birthYear - 25

            );

            index = new Random().nextInt(this.locations.getData().length);

            Event fatherMarriage = new Event(
                    UUID.randomUUID().toString(),
                    child.getAssociatedUsername(),
                    child.getFatherID(),
                    this.locations.getData()[index].getLatitude(),
                    this.locations.getData()[index].getLongitude(),
                    this.locations.getData()[index].getCountry(),
                    this.locations.getData()[index].getCity(),
                    "Marriage",
                    birthYear - 5

            );

            index = new Random().nextInt(this.locations.getData().length);

            Event fatherDeath = new Event(
                    UUID.randomUUID().toString(),
                    child.getAssociatedUsername(),
                    child.getFatherID(),
                    this.locations.getData()[index].getLatitude(),
                    this.locations.getData()[index].getLongitude(),
                    this.locations.getData()[index].getCountry(),
                    this.locations.getData()[index].getCity(),
                    "Death",
                    birthYear + 22

            );

            index = new Random().nextInt(this.locations.getData().length);

            Event motherBirth = new Event(
                    UUID.randomUUID().toString(),
                    child.getAssociatedUsername(),
                    child.getMotherID(),
                    this.locations.getData()[index].getLatitude(),
                    this.locations.getData()[index].getLongitude(),
                    this.locations.getData()[index].getCountry(),
                    this.locations.getData()[index].getCity(),
                    "Birth",
                    birthYear - 23

            );

            index = new Random().nextInt(this.locations.getData().length);

            Event motherMarriage = new Event(
                    UUID.randomUUID().toString(),
                    child.getAssociatedUsername(),
                    child.getMotherID(),
                    fatherMarriage.getLatitude(),
                    fatherMarriage.getLongitude(),
                    fatherMarriage.getCountry(),
                    fatherMarriage.getCity(),
                    "Marriage",
                    fatherMarriage.getYear()

            );

            index = new Random().nextInt(this.locations.getData().length);

            Event motherDeath = new Event(
                    UUID.randomUUID().toString(),
                    child.getAssociatedUsername(),
                    child.getMotherID(),
                    this.locations.getData()[index].getLatitude(),
                    this.locations.getData()[index].getLongitude(),
                    this.locations.getData()[index].getCountry(),
                    this.locations.getData()[index].getCity(),
                    "Death",
                    birthYear + 27

            );

            addEvent(fatherBirth);
            addEvent(fatherMarriage);
            addEvent(fatherDeath);
            addEvent(motherBirth);
            addEvent(motherMarriage);
            addEvent(motherDeath);

            return;

        }


        Person father = new Person(
                child.getFatherID(),
                this.user.getUsername(),
                this.maleNames.getData()[new Random().nextInt(this.maleNames.getData().length)],
                child.getLastName(),
                "m",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                child.getMotherID());

        addPerson(father);

        Person mother = new Person(
                child.getMotherID(),
                this.user.getUsername(),
                this.femaleNames.getData()[new Random().nextInt(this.femaleNames.getData().length)],
                this.surnames.getData()[new Random().nextInt(this.surnames.getData().length)],
                "f",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                child.getFatherID());

        addPerson(mother);

        int index = new Random().nextInt(this.locations.getData().length);

        Event fatherBirth = new Event(
                UUID.randomUUID().toString(),
                child.getAssociatedUsername(),
                child.getFatherID(),
                this.locations.getData()[index].getLatitude(),
                this.locations.getData()[index].getLongitude(),
                this.locations.getData()[index].getCountry(),
                this.locations.getData()[index].getCity(),
                "Birth",
                birthYear - 25

        );

        index = new Random().nextInt(this.locations.getData().length);

        Event fatherMarriage = new Event(
                UUID.randomUUID().toString(),
                child.getAssociatedUsername(),
                child.getFatherID(),
                this.locations.getData()[index].getLatitude(),
                this.locations.getData()[index].getLongitude(),
                this.locations.getData()[index].getCountry(),
                this.locations.getData()[index].getCity(),
                "Marriage",
                birthYear - 5

        );

        index = new Random().nextInt(this.locations.getData().length);

        Event fatherDeath = new Event(
                UUID.randomUUID().toString(),
                child.getAssociatedUsername(),
                child.getFatherID(),
                this.locations.getData()[index].getLatitude(),
                this.locations.getData()[index].getLongitude(),
                this.locations.getData()[index].getCountry(),
                this.locations.getData()[index].getCity(),
                "Death",
                birthYear + 22

        );

        index = new Random().nextInt(this.locations.getData().length);

        Event motherBirth = new Event(
                UUID.randomUUID().toString(),
                child.getAssociatedUsername(),
                child.getMotherID(),
                this.locations.getData()[index].getLatitude(),
                this.locations.getData()[index].getLongitude(),
                this.locations.getData()[index].getCountry(),
                this.locations.getData()[index].getCity(),
                "Birth",
                birthYear - 23

        );

        Event motherMarriage = new Event(
                UUID.randomUUID().toString(),
                child.getAssociatedUsername(),
                child.getMotherID(),
                fatherMarriage.getLatitude(),
                fatherMarriage.getLongitude(),
                fatherMarriage.getCountry(),
                fatherMarriage.getCity(),
                "Marriage",
                fatherMarriage.getYear()

        );

        index = new Random().nextInt(this.locations.getData().length);

        Event motherDeath = new Event(
                UUID.randomUUID().toString(),
                child.getAssociatedUsername(),
                child.getMotherID(),
                this.locations.getData()[index].getLatitude(),
                this.locations.getData()[index].getLongitude(),
                this.locations.getData()[index].getCountry(),
                this.locations.getData()[index].getCity(),
                "Death",
                birthYear + 27

        );

        addEvent(fatherBirth);
        addEvent(fatherMarriage);
        addEvent(fatherDeath);
        addEvent(motherBirth);
        addEvent(motherMarriage);
        addEvent(motherDeath);

        generateParents(father, gen-1, birthYear - 25);
        generateParents(mother, gen-1, birthYear - 25);

    }


    private void generate(int gen, String username) throws DataAccessException {

        UserDao uDao = new UserDao(db.getConnection());

        String personID = uDao.find(username).getPersonID();

        db.closeConnection(true);

        Person person = new Person(
                personID, //TODO: should be existing personID -- from existing User.
                this.user.getUsername(),
                this.user.getFirstName(),
                this.user.getLastName(),
                user.getGender(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                null
        );

        int index = new Random().nextInt(this.locations.getData().length);

        Event birth = new Event(
                UUID.randomUUID().toString(),
                this.user.getUsername(),
                person.getPersonID(),
                this.locations.getData()[index].getLatitude(),
                this.locations.getData()[index].getLongitude(),
                this.locations.getData()[index].getCountry(),
                this.locations.getData()[index].getCity(),
                "Birth",
                1994
        );

        index = new Random().nextInt(this.locations.getData().length);

        Event walk = new Event(
                UUID.randomUUID().toString(),
                this.user.getUsername(),
                person.getPersonID(),
                this.locations.getData()[index].getLatitude(),
                this.locations.getData()[index].getLongitude(),
                this.locations.getData()[index].getCountry(),
                this.locations.getData()[index].getCity(),
                "Learned to walk",
                1996
        );

        index = new Random().nextInt(this.locations.getData().length);

        Event mission = new Event(
                UUID.randomUUID().toString(),
                this.user.getUsername(),
                person.getPersonID(),
                this.locations.getData()[index].getLatitude(),
                this.locations.getData()[index].getLongitude(),
                this.locations.getData()[index].getCountry(),
                this.locations.getData()[index].getCity(),
                "went on mission",
                2014
        );


        addPerson(person);
        addEvent(birth);
        addEvent(walk);
        addEvent(mission);

        generateParents(person, gen, birth.getYear());
    }

    private void addPerson(Person person) {

        Person [] copy = new Person [this.persons.length + 1];
        for(int i = 0; i < this.persons.length; i++) {
            copy[i] = this.persons[i];
        }

        copy[this.persons.length] = person;
        this.persons = copy;
    }

    private void addEvent(Event event) {

        Event [] copy = new Event [this.events.length + 1];
        for(int i = 0; i < this.events.length; i++) {
            copy[i] = this.events[i];
        }

        copy[this.events.length] = event;
        this.events = copy;
    }







}




