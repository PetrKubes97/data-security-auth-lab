package server_side.scripts.seed;

import server_side.database.Database;
import server_side.database.pojo.AccessRight;

import java.sql.SQLException;

import static server_side.scripts.seed.SeedHelper.createUserWithAccessRights;

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


}
