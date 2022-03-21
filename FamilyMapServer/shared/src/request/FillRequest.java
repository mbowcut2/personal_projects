package request;


public class FillRequest {

    /**
     * URL Path: /fill/[username]/{generations}
     * Example: /fill/susan/3
     * Description: Populates the server's database with generated data for the specified username. The required "username" parameter must be a user already registered with the server. If there is any data in the database already associated with the given user name, it is deleted. The optional “generations” parameter lets the caller specify the number of generations of ancestors to be generated, and must be a non-negative integer (the default is 4, which results in 31 new persons each with associated events).
     * HTTP Method: POST
     * Auth Token Required: No
     * Request Body: None
     * Errors: Invalid username or generations parameter, Internal server error
     */

    private String username;
    private int generations;

    public FillRequest(String username, int generations) {
        this.username = username;
        this.generations = generations;
    }


    public String getUsername() {
        return username;
    }

    public int getGenerations() {
        return generations;
    }
}
