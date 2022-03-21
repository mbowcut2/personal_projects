package passoff;

import dao.*;
import model.AuthToken;
import model.Event;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import request.EventByIdRequest;
import request.EventRequest;
import request.PersonRequest;
import result.EventByIdResult;
import result.EventResult;
import result.PersonResult;
import service.PersonService;

public class PersonServiceTest {

    Person person1 = new Person(
            "Person1",
            "foo",
            "Foo",
            "Bar",
            "m",
            "Father",
            "Mother",
            "Spouse"
    );

    Person person2 = new Person(
            "Person2",
            "foo",
            "Foo",
            "Bar",
            "m",
            "Father",
            "Mother",
            "Spouse"
    );

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
    PersonDao pDao;
    AuthTokenDao aDao;
    UserDao uDao;
    PersonService service;
    PersonRequest request;
    PersonResult result;

    @BeforeEach
    public void setup() throws DataAccessException {

        pDao = new PersonDao(db.getConnection());
        aDao = new AuthTokenDao(db.getConnection());
        uDao = new UserDao(db.getConnection());

        pDao.insert(person1);
        pDao.insert(person2);
        aDao.insert(authtoken);
        uDao.insert(user);

        db.closeConnection(true);

    }

    @AfterEach
    public void tearDown() throws DataAccessException {

        pDao = new PersonDao(db.getConnection());
        aDao = new AuthTokenDao(db.getConnection());
        uDao = new UserDao(db.getConnection());
        pDao.clear();
        aDao.clear();
        uDao.clear();

        db.closeConnection(true);

    }

    @Test
    public void processPass() throws DataAccessException {

        request = new PersonRequest( "token");
        service = new PersonService();
        result = service.process(request);

        Assertions.assertTrue(result.isSuccess());

        Assertions.assertEquals(2, result.getData().length);


    }

    @Test
    public void processFail() throws DataAccessException { //user not in tree

        request = new PersonRequest("badToken");
        service = new PersonService();
        result = service.process(request);

        Assertions.assertFalse(result.isSuccess());



    }



}
