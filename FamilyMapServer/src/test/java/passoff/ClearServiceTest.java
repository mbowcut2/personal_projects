package passoff;

import dao.*;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.ClearRequest;
import result.ClearResult;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearServiceTest {


    private Database db = new Database();
    private ClearService service = new ClearService();
    private ClearRequest request = new ClearRequest();
    private ClearResult result;
    private AuthTokenDao aDao;
    private EventDao eDao;
    private PersonDao pDao;
    private UserDao uDao;



    @BeforeEach
    public void setup() throws DataAccessException {

        db.openConnection();
        aDao = new AuthTokenDao(db.getConnection());
        eDao = new EventDao(db.getConnection());
        pDao = new PersonDao(db.getConnection());
        uDao = new UserDao(db.getConnection());

    }
/*
    @AfterEach
    public void tearDown() throws DataAccessException {






    }
 */

    @Test
    public void processPass() throws DataAccessException {

        aDao.insert(new AuthToken("foo", "bar"));
        eDao.insert(new Event(
                "foobar",
                "foobar",
                "foobar", 123f,
                123f, "Fooland",
                "Bartown",
                "Explosion",
                3000
        ));
        pDao.insert(new Person(
                "FooBar",
                "foobar",
                "Foo",
                "Bar",
                "m",
                "Daddy",
                "Mommy",
                "Babe"
        ));
        uDao.insert(new User(
                "foo",
                "bar",
                "foo@bar.com",
                "Foo",
                "Bar",
                "m",
                "FooBar"
        ));

        db.closeConnection(true);

        result = service.process(request);

        assertTrue(result.isSuccess());

        db.openConnection();

        aDao = new AuthTokenDao(db.getConnection());
        eDao = new EventDao(db.getConnection());
        pDao = new PersonDao(db.getConnection());
        uDao = new UserDao(db.getConnection());

        Assertions.assertNull(aDao.find("bar"));
        Assertions.assertNull(aDao.find("foo"));
        Assertions.assertNull(aDao.find("foo"));
        Assertions.assertNull(aDao.find("foo"));

        db.closeConnection(true);


    }
/*
    @Test
    public void processFail() throws DataAccessException {


    }
    */
}
