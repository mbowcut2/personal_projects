package result;


import model.User;

public class RegisterResult extends Result{

    private String authtoken;
    private String username;
    private String personID;


    public RegisterResult(boolean success, String message) {

        /**
         *@param success indicates whether the request was successfully executed.
         *The Constructor should initialize the object as either successful or unsuccessful
         */

        super(success, message);

    }

    public RegisterResult(String authtoken, String username, String personID) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPersonID() {
        return personID;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

}
