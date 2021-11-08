package server_side.scripts;

import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.logger.Logger;
import server_side.middlewares.authentication.Authenticator;
import server_side.middlewares.authorization.*;
import server_side.responses.LoginResponse;
import server_side.responses.LoginSuccess;

import java.sql.SQLException;

public class Tests {
    public static void main(String[] args) throws SQLException {
        final Database db = new Database();
        final Logger logger = (username, actionName) -> System.out.println(username + ": " + actionName);
        final Authenticator authenticator = new Authenticator(db, logger);


        // ---------------- ACL Tests ------------------
        SeedData.resetWithSeedData(db, AuthorizationType.ACL);
        {
            final Authorizator authorizator = new AclAuthorizator(db, logger);
            LoginResponse loginResponse =
                    authenticator.login("david", "pass");
            check(loginResponse instanceof LoginSuccess);

            AuthorizationResult<Object> authorizationResult = authorizator
                    .authorize("david", AccessRight.PRINT);

            check(authorizationResult.commandFailure() == null);

            AuthorizationResult<Object> authorizationResult2 = authorizator
                    .authorize("david", AccessRight.RESTART);

            check(authorizationResult2.commandFailure() != null);
        }

        // ---------------- Role Tests ------------------
        {
            final Authorizator authorizator = new RoleAuthorizator(db, logger);
            SeedData.resetWithSeedData(db, AuthorizationType.ROLE);

            LoginResponse loginResponse =
                    authenticator.login("bob", "pass");
            check(loginResponse instanceof LoginSuccess);

            AuthorizationResult<Object> authorizationResult = authorizator
                    .authorize("bob", AccessRight.RESTART);

            check(authorizationResult.commandFailure() == null);

            AuthorizationResult<Object> authorizationResult2 = authorizator
                    .authorize("bob", AccessRight.PRINT);

            check(authorizationResult2.commandFailure() != null);
        }
    }

    private static void check(boolean test) {
        if (!test)
            System.err.println("Test failed");
        else
            System.out.println("Test passed");
    }
}
