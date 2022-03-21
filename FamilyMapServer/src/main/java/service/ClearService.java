package service;

import dao.*;
import model.Event;
import request.ClearRequest;
import result.ClearResult;

/*
TODO your service classes should return result classes
 */

public class ClearService {

    /**
     * Deletes ALL data from the database, including user accounts, auth tokens, and generated person and event data.
     *
     */

    public ClearResult process(ClearRequest request) throws DataAccessException {
        /**
         * Clears the database.
         */

        Database db = new Database();
        db.openConnection();

        try {

            new AuthTokenDao(db.getConnection()).clear();
            new EventDao(db.getConnection()).clear();
            new PersonDao(db.getConnection()).clear();
            new UserDao(db.getConnection()).clear();

            db.closeConnection(true);


            return new ClearResult(true, "Clear succeeded.");
        }
        catch (Exception e) {
            db.closeConnection(false);
            e.printStackTrace();
            return new ClearResult(false, "Error: " + e);
        }
    }
}
