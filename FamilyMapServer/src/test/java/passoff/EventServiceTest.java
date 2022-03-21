package passoff;

import dao.*;
import model.AuthToken;
import model.Event;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import request.EventByIdRequest;
import request.EventRequest;
import result.EventByIdResult;
import result.EventResult;
import service.EventService;


public class EventServiceTest {

    Event event1 = new Event("FooBar1",
            "foo",
            "bar",
            123f,
            123f,
            "Fooland",
            "Bartown",
            "Moon Landing",
            1969);

    Event event2 = new Event("FooBar2",
            "foo",
            "bar",
            123f,
            123f,
            "Fooland",
            "Bartown",
            "Moon Landing",
            1969);

    User user = new User(
            "foo",
            "bar",
            "foo@bar.com",
            "Foo",
            "Bar",
            "m",
            "bar"
    );

    AuthToken authtoken = new AuthToken("foo","token");
    Database db = new Database();
    EventDao eDao;
    AuthTokenDao aDao;
    UserDao uDao;
    EventService service;
    EventRequest request;
    EventResult result;

    @BeforeEach
    public void setup() throws DataAccessException {

        eDao = new EventDao(db.getConnection());
        aDao = new AuthTokenDao(db.getConnection());
        uDao = new UserDao(db.getConnection());

        eDao.insert(event1);
        eDao.insert(event2);
        aDao.insert(authtoken);
        uDao.insert(user);

        db.closeConnection(true);

    }

    @AfterEach
    public void tearDown() throws DataAccessException {

        eDao = new EventDao(db.getConnection());
        aDao = new AuthTokenDao(db.getConnection());
        uDao = new UserDao(db.getConnection());
        eDao.clear();
        aDao.clear();
        uDao.clear();

        db.closeConnection(true);

    }

    @Test
    public void processPass() throws DataAccessException {

        request = new EventRequest( "token");
        service = new EventService();
        result = service.process(request);

        Assertions.assertTrue(result.isSuccess());

        Assertions.assertEquals(2, result.getData().length);


    }

    @Test
    public void processFail() throws DataAccessException { //user not in tree

        request = new EventRequest("badToken");
        service = new EventService();
        result = service.process(request);

        Assertions.assertFalse(result.isSuccess());



    }



}
