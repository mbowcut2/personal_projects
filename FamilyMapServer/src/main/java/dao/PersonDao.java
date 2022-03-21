package dao;

import model.Event;
import model.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonDao {
    /**
     * Data Access Class for persons table.
     */

    private Person [] persons = new Person [0];

    private final Connection conn;

    public PersonDao(Connection conn) {
        this.conn = conn;
    }

    public void insert(Person person) throws DataAccessException {
        /**
         * adds user_id and token to persons table
         */

        String sql = "INSERT INTO persons (personID, associatedUsername, firstName, lastName, gender, " +
                "fatherID, motherID, spouseID) VALUES(?,?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, person.getPersonID());
            stmt.setString(2, person.getAssociatedUsername());
            stmt.setString(3, person.getFirstName());
            stmt.setString(4, person.getLastName());
            stmt.setString(5, person.getGender());
            stmt.setString(6, person.getFatherID()); //TODO: make optional
            stmt.setString(7, person.getMotherID()); //TODO: make optional
            stmt.setString(8, person.getSpouseID());//TODO: make optional

            stmt.executeUpdate();


        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }


    public void clear() throws DataAccessException{
        /**
         * Deletes all data from the persons table.
         */

        String sql = "DELETE FROM persons;";

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

        String sql = "DELETE FROM persons WHERE associatedUsername = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }

        catch (SQLException e) {

            throw new DataAccessException("Error encountered while clearing table");

        }

    }

    public Person find(String personID) throws DataAccessException {
        /**
         * Takes in a personID and returns the corresponding Person object.
         */
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM persons WHERE personID = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, personID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
                return person;
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

    public Person[] findAll(String username) throws DataAccessException {
        /**
         * takes in the eventID and returns the corresponding Event object.
         */
        Person person;
        ResultSet rs = null;
        String sql = "SELECT * FROM persons WHERE associatedUsername = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            while (rs.next()) {
                person = new Person(rs.getString("personID"), rs.getString("associatedUsername"),
                        rs.getString("firstName"), rs.getString("lastName"), rs.getString("gender"),
                        rs.getString("fatherID"), rs.getString("motherID"), rs.getString("spouseID"));
                addPerson(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding Person");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return this.persons;

    }

    private void addPerson(Person person) {

        Person [] copy = new Person [this.persons.length + 1];
        for(int i = 0; i < this.persons.length; i++) {
            copy[i] = this.persons[i];
        }

        copy[this.persons.length] = person;
        this.persons = copy;
    }

}
