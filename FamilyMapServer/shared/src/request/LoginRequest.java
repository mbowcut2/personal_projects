package request;

public class LoginRequest {

    /**
     * URL Path: /user/login
     * Description: Logs in the user and returns an auth token.
     * HTTP Method: POST
     * Auth Token Required: No
     */

    private String username;
    private String password;

    public LoginRequest(String username, String password) {

        /**
         * @param username
         * @param password
         */

        this.username = username;
        this.password = password;

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
