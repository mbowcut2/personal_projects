package request;

public class EventByIdRequest {

    /**
     * URL Path: /event/[eventID]
     * Example: /event/251837d7
     * Description: Returns the single Event object with the specified ID.
     * HTTP Method: GET
     * Auth Token Required: Yes
     * Request Body: None
     */

    private String eventID;
    private String authtoken;

    public EventByIdRequest(String eventID, String authtoken) {
        this.eventID = eventID;
        this.authtoken = authtoken;
    }

    public String getEventID() {
        return eventID;
    }

    public String getAuthtoken() {
        return authtoken;
    }
}
