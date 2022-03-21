package service;

import dao.DataAccessException;
import dao.UserDao;
import model.AuthToken;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import dao.Database;
import dao.AuthTokenDao;
import model.User;
import result.RegisterResult;

import java.util.UUID;

public class LoginService {
    /**
     * Logs in the user and returns an authtoken.
     */

    public LoginResult process(LoginRequest request) throws DataAccessException {

        Database db = new Database();
        //String personID = UUID.randomUUID().toString();
        AuthToken auth = null;
        User user = null;


        db.openConnection();

        try {

            UserDao uDao = new UserDao(db.getConnection());
            user = uDao.find(request.getUsername());

            if (user == null) {
                db.closeConnection(false);
                return new LoginResult(false, "Error: invalid Username");
            }

            if (!request.getPassword().equals(user.getPassword())) {
                db.closeConnection(false);
                return new LoginResult(false, "Error: invalid Username");
            }

            String authtoken = UUID.randomUUID().toString();
            auth = new AuthToken(request.getUsername(), authtoken);
            //aDao.insert(auth);
            db.closeConnection(true);
            db.openConnection();
            AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            aDao.insert(auth);
            db.closeConnection(true);


        }
        catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new LoginResult(false, "Error: " + e);
        }



        return new LoginResult(auth.getAuthToken(), auth.getUserID(), user.getPersonID());
    }
}
