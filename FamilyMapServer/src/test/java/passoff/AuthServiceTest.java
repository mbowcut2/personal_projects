package passoff;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import model.AuthToken;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.AuthService;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {


    private Database db;
    private AuthService service;
    private AuthTokenDao aDao;
    private AuthToken authtoken;


    @BeforeEach
    public void setup() throws DataAccessException {

        db = new Database();
        db.openConnection();
        aDao = new AuthTokenDao(db.getConnection());
        aDao.clear();

    }

    @AfterEach
    public void tearDown() throws DataAccessException {

        aDao = new AuthTokenDao(db.getConnection());
        aDao.clear();
        db.closeConnection(true);


    }

    @Test
    public void validatePass() throws DataAccessException {

        authtoken = new AuthToken("foo", "bar");

        aDao.insert(authtoken);

        db.closeConnection(true);

        service = new AuthService();

        boolean result = service.validate("bar");

        assertTrue(result);

    }

    @Test
    public void validateFail() throws DataAccessException {

        authtoken = new AuthToken("foo", "bar");

        aDao.insert(authtoken);

        db.closeConnection(true);

        service = new AuthService();

        boolean result = service.validate("foo");

        assertFalse(result);

    }


}
