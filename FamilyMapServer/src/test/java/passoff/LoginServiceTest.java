package passoff;

import dao.*;
import model.AuthToken;
import model.Event;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.LoginService;
import service.RegisterService;

public class LoginServiceTest {

    Database db = new Database();

    LoginRequest request;
    LoginResult result;
    LoginService service;
    UserDao uDao;
    EventDao eDao;
    PersonDao pDao;
    AuthTokenDao aDao;

    @BeforeEach
    public void setup() throws DataAccessException {

        RegisterRequest r = new RegisterRequest(
                "foobar",
                "123",
                "foo@bar.com",
                "Foo",
                "Bar",
                "m"
        );

        RegisterService s = new RegisterService();
        Assertions.assertTrue(s.process(r).isSuccess());

    }


    @AfterEach
    public void tearDown() throws DataAccessException {

        db.openConnection();

        uDao = new UserDao(db.getConnection());
        eDao = new EventDao(db.getConnection());
        pDao = new PersonDao(db.getConnection());
        aDao = new AuthTokenDao(db.getConnection());

        uDao.clear();
        eDao.clear();
        pDao.clear();
        aDao.clear();

        db.closeConnection(true);

    }

    @Test
    public void processPass() throws DataAccessException {

        request = new LoginRequest("foobar", "123");

        service = new LoginService();
        result = service.process(request);

        Assertions.assertTrue(result.isSuccess());



    }
    @Test
    public void processFail() throws DataAccessException {

        request = new LoginRequest("foobar", "wrongPassword");

        service = new LoginService();
        service.process(request);
        result = service.process(request);


        Assertions.assertFalse(result.isSuccess());

    }
}
