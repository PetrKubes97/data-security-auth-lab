package server_side.scripts;

import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.database.pojo.UserRecord;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static server_side.crypto.Crypto.createPasswordHash;
import static server_side.crypto.Crypto.getNextSalt;

public class AclSeed {

    public static void createAclSeed(Database db) throws SQLException {

        createUserWithAccessRights(
                "alice",
                "pass",
                AccessRight.values(), // All rights
                db
        );

        createUserWithAccessRights(
                "bob",
                "pass",
                new AccessRight[]{
                        AccessRight.START, AccessRight.STOP, AccessRight.RESTART,
                        AccessRight.STATUS, AccessRight.READ_CONFIG, AccessRight.SET_CONFIG
                }, // All rights
                db
        );

        createUserWithAccessRights(
                "cecilia",
                "pass",
                new AccessRight[]{
                        AccessRight.PRINT, AccessRight.QUEUE, AccessRight.TOP_QUEUE,
                        AccessRight.RESTART
                }, // All rights
                db
        );

        AccessRight[] basicRights = new AccessRight[]{AccessRight.PRINT, AccessRight.QUEUE};
        createUserWithAccessRights(
                "david",
                "pass",
                basicRights, // All rights
                db
        );
        createUserWithAccessRights(
                "erica",
                "pass",
                basicRights, // All rights
                db
        );
        createUserWithAccessRights(
                "fred",
                "pass",
                basicRights, // All rights
                db
        );
        createUserWithAccessRights(
                "george",
                "pass",
                basicRights, // All rights
                db
        );
    }

    private static void createUserWithAccessRights(
            String username,
            String password,
            AccessRight[] accessRights,
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

        for (AccessRight accessRight : accessRights) {
            db.insertAccessRight(username, accessRight);
        }
    }
}
