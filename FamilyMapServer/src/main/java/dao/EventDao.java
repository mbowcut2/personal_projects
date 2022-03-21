package dao;

//TODO do you have enough methods to fulfill all the services

import model.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EventDao {
    /**
     * Data Access Class for events table.
     */

    private final Connection conn;

    Event [] events = new Event [0];

    public EventDao(Connection conn) {
        this.conn = conn;
    }

    public void insert(Event event) throws DataAccessException {
        /**
         * adds event to events table
         */

        String sql = "INSERT INTO events (EventID, AssociatedUsername, PersonID, Latitude, Longitude, " +
                "Country, City, EventType, Year) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String
            stmt.setString(1, event.getEventID());
            stmt.setString(2, event.getAssociatedUsername());
            stmt.setString(3, event.getPersonID());
            stmt.setFloat(4, event.getLatitude());
            stmt.setFloat(5, event.getLongitude());
            stmt.setString(6, event.getCountry());
            stmt.setString(7, event.getCity());
            stmt.setString(8, event.getEventType());
            stmt.setInt(9, event.getYear());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }


    public void clear() throws DataAccessException {
        /**
         * Deletes all data from the events table.
         */

        String sql = "DELETE FROM events;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }

        catch (SQLException e) {

            throw new DataAccessException("Error encountered while clearing table");

        }
    }

    public void clear(String username) throws DataAccessException {
        /**
         * Clears all persons and events for the given user
         */

        String sql = "DELETE FROM events WHERE associatedUsername = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }

        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while clearing table");

        }

    }

    public Event find(String eventID) throws DataAccessException {
        /**
         * takes in the eventID and returns the corresponding Event object.
         */

        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM events WHERE EventID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, eventID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                return event;
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

    public Event findAuth(String authtoken) {
        /**
         * Returns the Event corresponding to authtoken.
         */
        return null;
    }

    public Event [] findAll(String username) throws DataAccessException {
        /**
         * takes in the eventID and returns the corresponding Event object.
         */
        Event event;
        ResultSet rs = null;
        String sql = "SELECT * FROM events WHERE associatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                event = new Event(rs.getString("EventID"), rs.getString("AssociatedUsername"),
                        rs.getString("PersonID"), rs.getFloat("Latitude"), rs.getFloat("Longitude"),
                        rs.getString("Country"), rs.getString("City"), rs.getString("EventType"),
                        rs.getInt("Year"));
                addEvent(event);
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
        return this.events;

    }

    private void addEvent(Event event) {

        Event [] copy = new Event [this.events.length + 1];
        for(int i = 0; i < this.events.length; i++) {
            copy[i] = this.events[i];
        }

        copy[this.events.length] = event;
        this.events = copy;
    }
}
