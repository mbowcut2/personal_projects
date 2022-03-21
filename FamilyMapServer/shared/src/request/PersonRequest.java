package request;

import model.Person;

public class PersonRequest {



    /**
     * URL Path: /person
     * Description: Returns ALL family members of the current user. The current user is determined from the provided auth token.
     * HTTP Method: GET
     * Auth Token Required: Yes
     * Request Body: None
     * Errors: Invalid auth token, Internal server error
     * Success Response Body: The response body returns a JSON object with a “data” attribute that contains an array of Person objects.  Each Person object has the same format as described in previous section on the /person/[personID] API.
     */

    private String authtoken;

    public PersonRequest(String authtoken) {
        this.authtoken = authtoken;
    }

    public String getAuthtoken() {
        return authtoken;
    }
}
