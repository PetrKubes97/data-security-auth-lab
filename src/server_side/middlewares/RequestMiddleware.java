package server_side.middlewares;

import server_side.CommandResponseFunction;
import server_side.logger.LoggerImpl;
import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.middlewares.authentication.AuthenticationResult;
import server_side.middlewares.authentication.Authenticator;
import server_side.middlewares.authorization.*;
import server_side.responses.CommandResponse;

public class RequestMiddleware {

    public final Authenticator authenticator;
    private final Authorizator authorizator;

    public RequestMiddleware(AuthorizationType authorizationType) {
        Database database = new Database();
        LoggerImpl logger = new LoggerImpl("log.txt");
        authenticator = new Authenticator(database, logger);

        if (authorizationType == AuthorizationType.ACL) {
            authorizator = new AclAuthorizator(database, logger);
        } else {
            authorizator = new RoleAuthorizator(database, logger);
        }

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
