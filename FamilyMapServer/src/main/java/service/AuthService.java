package service;

import dao.AuthTokenDao;
import dao.DataAccessException;
import dao.Database;

public class AuthService {

    public boolean validate(String authToken) throws DataAccessException {

        Database db = new Database();
        db.openConnection();

        boolean result = new AuthTokenDao(db.getConnection()).validate(authToken);

        db.closeConnection(true);

        return result;
    }
}
