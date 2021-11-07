package server_side.scripts;

import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.database.pojo.Role;
import server_side.database.pojo.UserRecord;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static server_side.crypto.Crypto.createPasswordHash;
import static server_side.crypto.Crypto.getNextSalt;

public class SeedHelper {

    public static void createUserWithAccessRights(
            String username,
            String password,
            AccessRight[] accessRights,
            Database db
    ) throws SQLException {
        createUser(
                username,
                password,
                db
        );

        for (AccessRight accessRight : accessRights) {
            db.insertAccessRight(username, accessRight);
        }
    }

    public static void createUserWithRole(
            String username,
            String password,
            Role role,
            Database db
    ) throws SQLException {
        createUser(
                username,
                password,
                db
        );

        db.assignRole(username, role);
    }

    private static void createUser(
            String username,
            String password,
            Database db
    ) throws SQLException {
        final String salt = getNextSalt();
        final UserRecord user = new UserRecord(
                username,
                LocalDateTime.now().minusDays(1),
                0,
                createPasswordHash(salt, password),
                salt,
                null
        );

        db.insertUser(user);
    }
}
