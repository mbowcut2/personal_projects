package passoff;

import dao.*;
import model.AuthToken;
import model.Event;
import model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.EventByIdRequest;
import request.PersonByIdRequest;
import result.EventByIdResult;
import result.PersonByIdResult;
import service.PersonByIdService;

import static org.junit.jupiter.api.Assertions.*;

public class PersonByIdServiceTest {

    Person person = new Person(
            "FooBar",
            "foo",
            "Foo",
            "Bar",
            "m",
            "Daddy",
            "Mommy",
            "Honey"
    );
    AuthToken authtoken = new AuthToken("foo","token");
    Database db = new Database();
    PersonDao pDao;
    AuthTokenDao aDao;
    PersonByIdService service;
    PersonByIdRequest request;
    PersonByIdResult result;

    @BeforeEach
    public void setup() throws DataAccessException {

        pDao = new PersonDao(db.getConnection());
        aDao = new AuthTokenDao(db.getConnection());
        pDao.insert(person);
        aDao.insert(authtoken);
        db.closeConnection(true);

    }

    @AfterEach
    public void tearDown() throws DataAccessException {

        pDao = new PersonDao(db.getConnection());
        aDao = new AuthTokenDao(db.getConnection());
        pDao.clear();
        aDao.clear();
        db.closeConnection(true);

    }

    @Test
    public void processPass() throws DataAccessException {

        request = new PersonByIdRequest("FooBar", "token");
        service = new PersonByIdService();
        result = service.process(request);

        assertTrue(result.isSuccess());

        assertEquals(this.person.getPersonID(), result.getPersonID());

        assertEquals(this.person.getAssociatedUsername(), result.getAssociatedUsername());

        assertEquals(this.person.getFirstName(), result.getFirstName());

        assertEquals(this.person.getLastName(), result.getLastName());

        assertEquals(this.person.getGender(), result.getGender());

        assertEquals(this.person.getFatherID(), result.getFatherID());

        assertEquals(this.person.getMotherID(), result.getMotherID());

        assertEquals(this.person.getSpouseID(), result.getSpouseID());
    }

    @Test
    public void processFail() throws DataAccessException { //user not in tree

        request = new PersonByIdRequest("BadId", "token");
        service = new PersonByIdService();
        result = service.process(request);

        assertFalse(result.isSuccess());

        assertNull(result.getPersonID());

        assertNull(result.getAssociatedUsername());

        assertNull(result.getFatherID());

        assertNull(result.getLastName());

        assertNull(result.getGender());

        assertNull(result.getFatherID());

        assertNull(result.getMotherID());

        assertNull(result.getSpouseID());
    }



}