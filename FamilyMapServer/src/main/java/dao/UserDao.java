package dao;

import model.Event;
import model.User;
import request.ClearRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//TODO: sql indeces start at 1.

public class UserDao {
    /**
     * Data Access Class for users table.
     */

    private final Connection conn;

    public UserDao(Connection conn) {
        this.conn = conn;
    }

    public void insert(User user) throws DataAccessException{
        /**
         * adds user to users table
         */

        String sql = "INSERT INTO users (username, password, email, firstName, lastName, " +
                "gender, personID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getFirstName());
            stmt.setString(5, user.getLastName());
            stmt.setString(6, user.getGender());
            stmt.setString(7, user.getPersonID());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public User find(String userID) throws DataAccessException {
        /**
         * Returns specified user from users table corresponding to the username.
         */
        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM users WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"),
                        rs.getString("email"), rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("personID"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding event");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;
    }

    public User findAuth(String authtoken) {
        /**
         * Returns the user corresponding to authtoken.
         */
        return null;
    }


    public void clear() throws DataAccessException {
        /**
         * Deletes all data from the users table.
         */

        String sql = "DELETE FROM users;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }

        catch (SQLException e) {

            throw new DataAccessException("Error encountered while clearing table");

        }
    }

    public boolean validateUsername(String username) throws DataAccessException {

        ResultSet rs = null;
        String sql = "SELECT * FROM users WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                if(!rs.getString("username").equals(username)) {
                    return false;
                }
                else { return true;}
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding username.");

        }
        return false;
    }

    public boolean validatePassword(String username, String password) throws DataAccessException {

        ResultSet rs = null;
        String sql = "SELECT * FROM users WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            if (rs.next()) {
                if(!rs.getString("password").equals(password)) {
                    return false;
                }
                else { return true;}
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding username.");

        }
        return false;
    }
}
