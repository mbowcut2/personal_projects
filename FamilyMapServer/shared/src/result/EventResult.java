package result;

import model.Event;

public class EventResult extends Result{

    /**
     * Response that  returns a JSON object with a “data” attribute that contains an array of Event objects.
     */

    private Event [] data;

    public EventResult(boolean success, String message) {
        /**
         *@param success indicates whether the request was successfully executed.
         *The Constructor should initialize the object as either successful or unsuccessful
         */

        super(success, message);

        this.data = null;
    }

    public EventResult(Event[] events) {
        super(true, null);
        this.data = events;
    }

    public Event[] getData() {
        return data;
    }
}
