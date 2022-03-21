package passoff;

import dao.*;
import model.AuthToken;
import model.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;
import service.RegisterService;

public class RegisterServiceTest {

    Database db = new Database();

    RegisterRequest request;
    RegisterResult result;
    RegisterService service;
    UserDao uDao;
    EventDao eDao;
    PersonDao pDao;
    AuthTokenDao aDao;


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

        request = new RegisterRequest(
                "foobar",
                "123",
                "foo@bar.com",
                "Foo",
                "Bar",
                "m"
        );

        service = new RegisterService();
        result = service.process(request);

        Assertions.assertTrue(result.isSuccess());



    }
    @Test
    public void processFail() throws DataAccessException {

        request = new RegisterRequest(
                "foobar",
                "123",
                "foo@bar.com",
                "Foo",
                "Bar",
                "m"
        );

        service = new RegisterService();
        service.process(request);
        result = service.process(request);


        Assertions.assertFalse(result.isSuccess());

    }
}
