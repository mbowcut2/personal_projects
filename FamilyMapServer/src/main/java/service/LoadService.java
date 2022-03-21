package service;

import dao.*;
import model.Person;
import model.User;
import model.Event;
import request.ClearRequest;
import request.LoadRequest;
import result.ClearResult;
import result.LoadResult;

public class LoadService {

    /**
     * Clears all data from the database (just like the /clear API), and then loads the posted user, person, and event data into the database.
     */



    public LoadResult process(LoadRequest request) throws DataAccessException {

        /**
         * Uses the DAO classes to clear the database and repopulate the database with the info passed in the request.
         */
        ClearRequest clearRequest = new ClearRequest();
        ClearService clearService = new ClearService();
        clearService.process(clearRequest);
        Database db = new Database();

        db.openConnection();

        try {

            //AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            EventDao eDao = new EventDao(db.getConnection());
            UserDao uDao = new UserDao(db.getConnection());
            PersonDao pDao = new PersonDao(db.getConnection());

            for(int i = 0; i < request.getEvents().length; i++) {
                eDao.insert(request.getEvents()[i]);
            }

            for(int i = 0; i < request.getUsers().length; i++) {
                uDao.insert(request.getUsers()[i]);
            }

            for(int i = 0; i < request.getPersons().length; i++) {
                pDao.insert(request.getPersons()[i]);
            }


            db.closeConnection(true);


            LoadResult result =  new LoadResult(true, "Successfully added " + request.getUsers().length + " users, " + request.getPersons().length + " persons, and " + request.getEvents().length +  " events to the database."); //TODO: check success criteria
            return result;
        }
        catch (Exception e) {
            db.closeConnection(false);
            LoadResult result =  new LoadResult(false, "Error: " + e); //TODO: add the failed message
            e.printStackTrace();
            return result;
        }

    }
}
