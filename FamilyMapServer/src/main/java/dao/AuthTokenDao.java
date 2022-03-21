package dao;



import model.AuthToken;
import model.Event;
import model.User;
import request.ClearRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthTokenDao {

    private final Connection conn;


    public AuthTokenDao(Connection conn) {

        this.conn = conn;

    }

    /**
     * Data Access Class for authtokens table.
     */


    public String find(String authtoken) throws DataAccessException {
        String username = "";
        /**
         * returns the userID for a given authToken
         */

        //AuthToken auth;
        ResultSet rs = null;
        String sql = "SELECT * FROM authtokens WHERE token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authtoken);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding username");
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

    public void insert(AuthToken authtoken) throws DataAccessException {
        /**
         * adds user_id and token to authtokens table
         */

        String sql = "INSERT INTO authtokens (username, token) VALUES(?,?)";
        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, authtoken.getUserID());
            stmt.setString(2, authtoken.getAuthToken());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    //TODO: write a method to replace old authtokens -- should query username, and update the authtoken.

    public void update(String username, String authtoken) throws DataAccessException {
        /**
         * Updates an existing username with a new token.
         */

        String sql = "UPDATE authtokens SET token = ? WHERE username = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authtoken);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }

        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while updating table.");
        }

    }

    public void clear() throws DataAccessException {
        /**
         * Deletes all data from the authtokens table.
         */

        String sql = "DELETE FROM authtokens;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }

        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing table");

        }
    }

    public boolean validate(String authToken) throws DataAccessException {

        ResultSet rs = null;
        String sql = "SELECT * FROM authtokens WHERE token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            rs = stmt.executeQuery();
            if (rs.next()) {
                if(rs.getString("token").equals(authToken)) {
                    return true;
                }
                else { return false;}
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding token");

        }
        return false;
    }
}
