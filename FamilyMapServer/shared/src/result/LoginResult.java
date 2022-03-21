package result;

public class LoginResult extends Result {

    /**
     * Response that indicates whether the user was successfully loged in, and includes the created authtoken, personID, and username.
     */

    private String authtoken;
    private String personID;
    private String username;

    public LoginResult(boolean success, String message) {

        /**
         *@param success indicates whether the request was successfully executed.
         *The Constructor should initialize the object as either successful or unsuccessful
         */

        super(success, message);

    }

    public LoginResult(String authtoken, String username, String personID) {
        super(true, null);
        this.authtoken = authtoken;
        this.personID = personID;
        this.username = username;
    }

    public String getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
