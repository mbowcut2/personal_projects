package passoff;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.EventDao;
import model.AuthToken;
import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.EventByIdRequest;
import result.EventByIdResult;
import service.EventByIdService;

import static org.junit.jupiter.api.Assertions.*;

public class EventByIdServiceTest {

    Event event = new Event("FooBar",
            "foo",
            "bar",
            123f,
            123f,
            "Fooland",
            "Bartown",
            "Moon Landing",
            1969);
    AuthToken authtoken = new AuthToken("foo","token");
    Database db = new Database();
    EventDao eDao;
    AuthTokenDao aDao;
    EventByIdService service;
    EventByIdRequest request;
    EventByIdResult result;

    @BeforeEach
    public void setup() throws DataAccessException {

        eDao = new EventDao(db.getConnection());
        aDao = new AuthTokenDao(db.getConnection());
        eDao.insert(event);
        aDao.insert(authtoken);
        db.closeConnection(true);

    }

    @AfterEach
    public void tearDown() throws DataAccessException {

        eDao = new EventDao(db.getConnection());
        aDao = new AuthTokenDao(db.getConnection());
        eDao.clear();
        aDao.clear();
        db.closeConnection(true);

    }

    @Test
    public void processPass() throws DataAccessException {

        request = new EventByIdRequest("FooBar", "token");
        service = new EventByIdService();
        result = service.process(request);

        assertTrue(result.isSuccess());

        assertEquals(this.event.getEventID(), result.getEventID());

        assertEquals(this.event.getAssociatedUsername(), result.getAssociatedUsername());

        assertEquals(this.event.getPersonID(), result.getPersonID());

        assertEquals(this.event.getLatitude(), result.getLatitude());

        assertEquals(this.event.getLongitude(), result.getLongitude());

        assertEquals(this.event.getCity(), result.getCity());

        assertEquals(this.event.getCountry(), result.getCountry());

        assertEquals(this.event.getYear(), result.getYear());
    }

    @Test
    public void processFail() throws DataAccessException { //user not in tree

        request = new EventByIdRequest("BadId", "token");
        service = new EventByIdService();
        result = service.process(request);

        assertFalse(result.isSuccess());

        assertNull(result.getEventID());

        assertNull(result.getAssociatedUsername());

        assertNull(result.getPersonID());

        assertNull(result.getCity());

        assertNull(result.getCountry());

    }



}
