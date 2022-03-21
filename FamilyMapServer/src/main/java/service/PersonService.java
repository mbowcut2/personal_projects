package service;

import dao.*;
import model.Event;
import model.Person;
import model.User;
import request.PersonRequest;
import result.EventResult;
import result.PersonResult;

public class PersonService {
    /**
     * Returns ALL family members of the current user. The current user is determined from the provided auth token.
     */

    public PersonResult process(PersonRequest request) throws DataAccessException {
        /** returns ALL family members of the current user.
         *
         */
        Person[] persons;
        Database db = new Database();
        db.openConnection();

        try {
            User user = new UserDao(db.getConnection()).find(new AuthTokenDao(db.getConnection()).find(request.getAuthtoken()));
            //db.closeConnection(true);
            if (user != null) {

                PersonDao pDao = new PersonDao(db.getConnection());
                persons = pDao.findAll(user.getUsername());
                if (persons.length == 0) {
                    db.closeConnection(false);
                    return new PersonResult(false, "No Family members found.");
                }
                db.closeConnection(true);
                return new PersonResult(persons);
            }
            else {
                db.closeConnection(false);
                return new PersonResult(false, "Error: invalid Username");
            }

        }
        catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new PersonResult(false, "Error: " + e);
        }
    }

}
