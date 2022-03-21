package passoff;

import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTest {


    private Database db;
    private UserService service;
    private UserDao uDao;
    private User user = new User(
            "foo",
            "bar",
            "foo@bar.com",
            "Foo",
            "Bar",
            "b",
            "foobar");


    @BeforeEach
    public void setup() throws DataAccessException {

        db = new Database();
        db.openConnection();
        uDao = new UserDao(db.getConnection());
        uDao.insert(user);
        db.closeConnection(true);

    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.openConnection();
        uDao = new UserDao(db.getConnection());
        uDao.clear();
        db.closeConnection(true);


    }

    @Test
    public void validateUsernamePass() throws DataAccessException {

        service = new UserService();

        boolean result = service.validateUsername("foo");

        assertTrue(result);

    }

    @Test
    public void validateUsernameFail() throws DataAccessException {

        service = new UserService();

        boolean result = service.validateUsername("moo");

        assertFalse(result);

    }

    @Test
    public void validatePasswordPass() throws DataAccessException {

        service = new UserService();

        boolean result = service.validatePassword("foo", "bar");

        assertTrue(result);

    }

    @Test
    public void validatePasswordFail() throws DataAccessException {

        service = new UserService();

        boolean result = service.validatePassword("foo", "far");

        assertFalse(result);

    }
}