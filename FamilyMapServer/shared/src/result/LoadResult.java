package result;

public class LoadResult extends Result{

    /**
     * Response that indicates whether the database was successfully loaded.
     */

    public LoadResult(boolean success, String message) {
        /**
         *@param success indicates whether the request was successfully executed.
         *The Constructor should initialize the object as either successful or unsuccessful
         */

        super(success, message);
    }
}
