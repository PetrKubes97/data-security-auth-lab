package server_side.middlewares.authorization;

import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.database.pojo.Role;
import server_side.logger.Logger;
import server_side.responses.CommandFailure;

import java.sql.SQLException;
import java.util.Set;

public class RoleAuthorizator implements Authorizator {

    private final Database database;
    private final Logger logger;

    public RoleAuthorizator(Database database, Logger logger) {
        this.database = database;
        this.logger = logger;
    }

    @Override
    public <T> AuthorizationResult<T> authorize(String username, AccessRight requiredAccessRight) {
        try {
            final Role userRole = database.getRoleByUsername(username);
            final Set<AccessRight> accessRights = userRole.getAllAccessRights();

            if (!accessRights.contains(requiredAccessRight)) {
                logger.logToFile(username, "Attempted to use forbidden action: " + requiredAccessRight);
                return new AuthorizationResult<>(
                        new CommandFailure<>("Action " + requiredAccessRight + " is not permitted for role: " + userRole.name)
                );
            }

            return new AuthorizationResult<>(null);
        } catch (SQLException e) {
            e.printStackTrace();
            return new AuthorizationResult<>(new CommandFailure<>("Authorization failed"));
        }
    }
}
