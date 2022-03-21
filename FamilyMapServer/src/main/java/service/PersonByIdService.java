package service;

import dao.*;
import model.Person;
import request.PersonByIdRequest;
import result.EventByIdResult;
import result.PersonByIdResult;

public class PersonByIdService {

    /**
     * Returns the single Person object with the specified ID.
     */


    public PersonByIdResult process(PersonByIdRequest request) throws DataAccessException {
        Database db = new Database();

        db.openConnection();

        try {
            AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            String username = aDao.find(request.getAuthtoken());
            PersonDao pDao = new PersonDao(db.getConnection());
            Person person = pDao.find(request.getPersonID());
            if (person == null) {
                db.closeConnection(false);
                return new PersonByIdResult(false, "Error: invalid personID");
            }
            if (!person.getAssociatedUsername().equals(username)) {
                db.closeConnection(false);
                return new PersonByIdResult(false, "Error: Person is not in family tree");
            }
            db.closeConnection(true);
            return new PersonByIdResult(person);
        }

        catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new PersonByIdResult(false, "Error: " + e);
        }
    }
}
