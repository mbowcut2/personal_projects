package model;

public class AuthToken {
    /**
     * The model for auth tokens stored in auth_tokens table.
     */
    private String userID;
    private String authtoken;

    public AuthToken(String userID, String token) {
        /**
         * @param userID unique identifier. Primary key.
         * @param authtoken unique authtoken.
         */
        this.userID = userID;
        this.authtoken = token;
    }

    public String getUserID() {
        return userID;
    }

    public String getAuthToken() {
        return authtoken;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setToken(String token) {
        this.authtoken = token;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (o instanceof AuthToken) {
            AuthToken oAuth = (AuthToken) o;
            return oAuth.getAuthToken().equals(getAuthToken()) &&
                    oAuth.getUserID().equals(getUserID());
        } else {
            return false;
        }
    }
}
