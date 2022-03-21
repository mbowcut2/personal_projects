package request;

import model.Event;
import model.Person;
import model.User;

public class LoadRequest {

    /**
     * URL Path: /load
     * Description: Clears all data from the database (just like the /clear API), and then loads the posted user, person, and event data into the database.
     * HTTP Method: POST
     * Auth Token Required: No
     * Request Body: The “users” property in the request body contains an array of users to be created. The “persons” and “events” properties contain family history information for these users.  The objects contained in the “persons” and “events” arrays should be added to the server’s database.  The objects in the “users” array have the same format as those passed to the /user/register API with the addition of the personID.  The objects in the “persons” array have the same format as those returned by the /person/[personID] API.  The objects in the “events” array have the same format as those returned by the /event/[eventID] API.
     */

    private User [] users;
    private Person [] persons;
    private Event [] events;

    public LoadRequest(User[] users, Person[] persons, Event[] events) {

        /**
         * @param users
         * @param persons
         * @param events
         */

        this.users = users;
        this.persons = persons;
        this.events = events;
    }

    public User[] getUsers() {
        return users;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }

    public Person[] getPersons() {
        return persons;
    }

    public void setPersons(Person[] persons) {
        this.persons = persons;
    }

    public Event[] getEvents() {
        return events;
    }

    public void setEvents(Event[] events) {
        this.events = events;
    }
}
