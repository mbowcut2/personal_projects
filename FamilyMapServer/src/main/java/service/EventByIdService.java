package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.Event;
import request.EventByIdRequest;
import result.EventByIdResult;
import result.PersonByIdResult;

public class EventByIdService {

    /**
     * Returns a EventByIdResult object for the specified eventID.
     */


    public EventByIdResult process(EventByIdRequest request) throws DataAccessException {
        /**
         * uses the EventDAO class to find the event corresponding to the eventID. Returns a result.
         */

        Database db = new Database();

        db.openConnection();

        try {
            EventDao eDao = new EventDao(db.getConnection());
            Event event = eDao.find(request.getEventID());
            AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            String username = aDao.find(request.getAuthtoken());

            if (event == null) {
                db.closeConnection(false);
                return new EventByIdResult(false, "Error: invalid personID");
            }

            if (!event.getAssociatedUsername().equals(username)) {
                db.closeConnection(false);
                return new EventByIdResult(false, "Error: Event not in user family tree.");
            }

            db.closeConnection(true);
            return new EventByIdResult(event);
        }

        catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new EventByIdResult(false, "Error: " + e);
        }
    }





}
