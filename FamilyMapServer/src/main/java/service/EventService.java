package service;

import dao.*;
import model.Event;
import model.User;
import request.EventRequest;
import result.EventResult;
import result.PersonResult;

public class EventService {
    /**
     *  Returns ALL events for ALL family members of the current user. The current user is determined from the provided auth token.
     */


    public EventResult process(EventRequest request) throws DataAccessException {
        /**
         * Gets events for all family members.
         */
        Event [] events;
        Database db = new Database();
        db.openConnection();

        try {
            User user = new UserDao(db.getConnection()).find(new AuthTokenDao(db.getConnection()).find(request.getAuthtoken()));
            //db.closeConnection(true);
            if (user != null) {

                EventDao eDao = new EventDao(db.getConnection());
                events = eDao.findAll(user.getUsername());
                if (events.length == 0) {
                    db.closeConnection(false);
                    return new EventResult(false, "No Events found.");
                }
                db.closeConnection(true);
                return new EventResult(events);
            }
            else {
                db.closeConnection(false);
                return new EventResult(false, "invalid Username");
            }

        }
        catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new EventResult(false, "Error: " + e);
        }
    }
}
