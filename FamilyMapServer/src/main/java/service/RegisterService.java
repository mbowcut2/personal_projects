package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;
import dao.UserDao;
import model.AuthToken;
import request.RegisterRequest;
import result.RegisterResult;
import model.User;

import java.util.UUID;

public class RegisterService {

    /**
     * This class fulfills the register request -- i.e. it creates a new user, logs in the user, generates a family tree, and returns an authtoken.
     */


    public RegisterResult process(RegisterRequest request) throws DataAccessException {

        Database db = new Database();
        String personID = UUID.randomUUID().toString();
        AuthToken auth = null;

        User user = new User(
                request.getUsername(),
                request.getPassword(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getGender(),
                personID
        );

        db.openConnection();

        try {
            AuthTokenDao aDao = new AuthTokenDao(db.getConnection());
            UserDao uDao = new UserDao(db.getConnection());
            if (uDao.find(request.getUsername()) != null) {
                db.closeConnection(false);
                return new RegisterResult(false, "Error: Username already taken by another user.");
            }
            uDao.insert(user);

            String authtoken = UUID.randomUUID().toString();
            auth = new AuthToken(request.getUsername(), authtoken);
            aDao.insert(auth);

            db.closeConnection(true);

        }
        catch (DataAccessException e) {
            e.printStackTrace();
            db.closeConnection(false);
            return new RegisterResult(false, "Error: " + e);
        }



        return new RegisterResult(auth.getAuthToken(), auth.getUserID(), personID);
    }
}
