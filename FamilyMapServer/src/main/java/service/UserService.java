package service;

import dao.DataAccessException;
import dao.UserDao;
import dao.Database;

public class UserService {

    public boolean validateUsername(String username) throws DataAccessException {

        //return new UserDao(new Database().getConnection()).validateUsername(username);
        Database db = new Database();
        boolean result = new UserDao(db.getConnection()).validateUsername(username);
        db.closeConnection(true);
        return result;
    }

    public boolean validatePassword(String username, String password) throws DataAccessException {

        //return new UserDao(new Database().getConnection()).validatePassword(username, password);
        Database db = new Database();
        boolean result = new UserDao(db.getConnection()).validatePassword(username, password);
        db.closeConnection(true);
        return result;
    }
}
