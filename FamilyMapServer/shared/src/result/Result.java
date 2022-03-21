package result;

public abstract class Result {

    /**
     * The base class for all Result classes.
     * @param success a boolean indicating whether the request was successful or unsuccessful
     * @param message if unsuccessful: "Error: [Description of the error]"
     */

    private String message;

    public Result(boolean success, String message) {
        this.message = message;
        this.success = success;
    }

    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
