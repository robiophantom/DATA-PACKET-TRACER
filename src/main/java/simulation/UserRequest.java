package simulation;

public class UserRequest {
    private final RouterNode sourceRouter;
    private final String userType;
    private final String domain;

    public UserRequest(RouterNode sourceRouter, String userType, String domain) {
        this.sourceRouter = sourceRouter;
        this.userType = userType;
        this.domain = domain;
    }

    public RouterNode getSourceRouter() {
        return sourceRouter;
    }

    public String getUserType() {
        return userType;
    }

    public String getDomain() {
        return domain;
    }
}