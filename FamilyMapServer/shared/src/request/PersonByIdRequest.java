package request;


public class PersonByIdRequest {

    /**
     * URL Path: /person/[personID]
     * Example: /person/7255e93e
     * Description: Returns the single Person object with the specified ID.
     * HTTP Method: GET
     * Auth Token Required: Yes
     * Request Body: None
     * Errors: Invalid auth token, Invalid personID parameter, Requested person does not belong to this user, Internal server error
     */

    String personID;
    String authtoken;

    public PersonByIdRequest(String personID, String authtoken) {
        this.personID = personID;
        this.authtoken = authtoken;
    }

    public String getPersonID() {
        return personID;
    }

    public String getAuthtoken() {
        return authtoken;
    }
}
