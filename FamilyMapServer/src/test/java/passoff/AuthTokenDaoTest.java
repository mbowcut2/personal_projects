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

import java.sql.Connection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenDaoTest {

    private Database db;
    private AuthToken authtoken; //TODO: name
    private AuthTokenDao aDao;


    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();

        authtoken = new AuthToken("johnDoe", UUID.randomUUID().toString());

        Connection conn = db.getConnection();

        db.clearTables();

        aDao = new AuthTokenDao(conn);
    }


    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }
    @Test
    public void insertPass() throws DataAccessException {

        /**
         * Positive test for insert() method.
         */
        aDao.insert(authtoken);

        String username = aDao.find(authtoken.getAuthToken());

        assertNotNull(username);

        assertEquals(authtoken.getUserID(), username);
    }

    @Test
    public void insertFail() throws DataAccessException {

        /**
         * Negative test for insert() method.
         */

        aDao.insert(authtoken);

        assertThrows(DataAccessException.class, ()-> aDao.insert(authtoken));
    }

    @Test
    public void findPass() throws DataAccessException {

        aDao.insert(authtoken);

        AuthToken findTest = authtoken;

        findTest.setToken("test");

        aDao.insert(findTest);

        String compareTest = aDao.find("test");

        assertNotNull(compareTest);


    }

    @Test
    public void findFail() throws DataAccessException {

        aDao.insert(authtoken);

        assertNull(aDao.find("test"));

    }

    @Test
    public void clearPass() throws DataAccessException { //TODO: implement clear method

        aDao.insert(authtoken);

        String test = aDao.find(authtoken.getAuthToken());

        assertNotNull(test);

        aDao.clear();

        assertNull(aDao.find(authtoken.getAuthToken()));
    }


}
