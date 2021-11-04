package server_side.middlewares;

import server_side.CommandResponseFunction;
import server_side.Logger;
import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.responses.CommandResponse;

public class RequestMiddleware {

    public final Authenticator authenticator;
    private final Authorizator authorizator;

    public RequestMiddleware() {
        Database database = new Database();
        Logger logger = new Logger("log.txt");
        authenticator = new Authenticator(database, logger);
        authorizator = new Authorizator(database, logger);
    }

    public <T> CommandResponse<T> handle(String accessToken, AccessRight requiredAccessRight, CommandResponseFunction<T> function) {
        // Authenticate
        AuthenticationResult<T> authenticationResult = authenticator.authenticated(accessToken);
        if (authenticationResult.commandFailure() != null) return authenticationResult.commandFailure();

        // Authorize
        AuthorizationResult<T> authorizationResult =  authorizator.authorize(
                authenticationResult.userName(),
                requiredAccessRight
        );
        if (authorizationResult.commandFailure() != null) return authorizationResult.commandFailure();

        return function.call();
    }

}
