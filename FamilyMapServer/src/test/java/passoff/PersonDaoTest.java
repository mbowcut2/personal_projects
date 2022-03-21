package passoff;


import dao.DataAccessException;
import dao.Database;
import dao.PersonDao;
import model.Person;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class PersonDaoTest {

    private Database db;
    private Person person; //TODO: name
    private PersonDao pDao;

    @BeforeEach
    public void setUp() throws DataAccessException {
        db = new Database();

        person = new Person("testABC123", "foobar", "Foo", "Bar", "f", "dadID", "momID" , "spouseID");

        Connection conn = db.getConnection();

        db.clearTables();

        pDao = new PersonDao(conn);
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
        pDao.insert(person);

        Person compareTest = pDao.find(person.getPersonID());

        assertNotNull(compareTest);

        assertEquals(person, compareTest);
    }

    @Test
    public void insertFail() throws DataAccessException {

        /**
         * Negative test for insert() method.
         */

        pDao.insert(person);

        assertThrows(DataAccessException.class, ()-> pDao.insert(person));
    }

    @Test
    public void findPass() throws DataAccessException {

        pDao.insert(person);

        Person findTest = person;

        findTest.setPersonID("test");

        pDao.insert(findTest);

        Person compareTest = pDao.find("test");

        assertNotNull(compareTest);


    }

    @Test
    public void findFail() throws DataAccessException {

        pDao.insert(person);

        assertNull(pDao.find("test"));

    }

    @Test
    public void clearPass() throws DataAccessException { //TODO: implement clear method

        pDao.insert(person);

        Person comparePerson = pDao.find(person.getPersonID());

        assertNotNull(comparePerson);

        pDao.clear();

        assertNull(pDao.find(person.getPersonID()));
    }


}
