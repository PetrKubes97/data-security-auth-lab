package server_side.middlewares.authorization;

import server_side.logger.Logger;
import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.responses.CommandFailure;

import java.sql.SQLException;
import java.util.List;

public class AclAuthorizator implements Authorizator {
    private final Database database;
    private final Logger logger;

    public AclAuthorizator(Database database, Logger logger) {
        this.database = database;
        this.logger = logger;
    }

    @Override
    public <T> AuthorizationResult<T> authorize(String username, AccessRight requiredAccessRight) {
        try {
            List<AccessRight> accessRights = database.loadAccessRightsByUsername(username);

            if (!accessRights.contains(requiredAccessRight)) {
                logger.logToFile(username, "Attempted to use forbidden action: " + requiredAccessRight);
                return new AuthorizationResult<>(
                        new CommandFailure<>("Action " + requiredAccessRight + " is not permitted")
                );
            }

            return new AuthorizationResult<>(null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new AuthorizationResult<>(new CommandFailure<>("Authorization failed"));
        }
    }
}
