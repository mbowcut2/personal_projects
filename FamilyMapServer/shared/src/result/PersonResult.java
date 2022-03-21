package result;

import model.Person;

public class PersonResult extends Result{

    /**
     * The response body returns a JSON object with a “data” attribute that contains an array of Person objects.
     */

    private Person [] data;

    public PersonResult(Person [] data) {
        /**
         *@param success indicates whether the request was successfully executed.
         *The Constructor should initialize the object as either successful or unsuccessful
         */

        super(true, null);

        this.data = data;
    }

    public PersonResult(boolean success, String message) {
        super(success, message);
        this.data = null;
    }

    public Person[] getData() {
        return data;
    }
}
