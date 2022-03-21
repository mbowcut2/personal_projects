package passoff;

import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private Database db;
    private User user;
    private UserDao uDao;

    @BeforeEach
    public void setUp() throws DataAccessException {

        db = new Database();

        user = new User("foobar", "raboof", "foo@bar.com", "Foo", "Bar", "f", "fb123");

        Connection conn = db.getConnection();

        db.clearTables();

        uDao = new UserDao(conn);
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        db.closeConnection(false);
    }

    @Test
    public void insertPass() throws DataAccessException {

        uDao.insert(user);

        User compareTest = uDao.find(user.getUsername());

        assertNotNull(compareTest);

        assertEquals(user, compareTest);

    }

    @Test
    public void insertFail() throws DataAccessException {

        uDao.insert(user);

        assertThrows(DataAccessException.class, ()-> uDao.insert(user));
    }

    @Test
    public void findPass() throws DataAccessException { //TODO: How can I make this different?

        uDao.insert(user);

        User findTest = user;

        findTest.setUsername("test");

        uDao.insert(findTest);

        User compareTest = uDao.find("test");

        assertNotNull(compareTest);


    }

    @Test
    public void findFail() throws DataAccessException {
        uDao.insert(user);

        assertNull(uDao.find("test"));

    }

    @Test
    public void clearPass() throws DataAccessException { //TODO: implement clear method

        uDao.insert(user);

        User compareUser = uDao.find(user.getUsername());

        assertNotNull(compareUser);

        uDao.clear();

        assertNull(uDao.find(user.getUsername()));

    }


}
