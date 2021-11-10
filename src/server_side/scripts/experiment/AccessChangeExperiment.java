package server_side.scripts.experiment;

import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.database.pojo.RoleName;
import server_side.scripts.seed.AclSeed;
import server_side.scripts.seed.RoleSeed;
import server_side.scripts.seed.SeedHelper;

import java.sql.SQLException;
import java.util.List;

public class AccessChangeExperiment {

    public static void main(String[] args) throws SQLException {
        final Database db = new Database();
        /*
         * Now consider the situation where Bob leaves the company and George takes over the responsibilities as service
         * technician. At the same time, two new employees are hired: Henry, who should be granted the privileges of an
         * ordinary user, and Ida who is a power user and should be given the same privileges as Cecilia.
         */

//        ACLChange(db);
        RoleChange(db);
    }

    private static void RoleChange(Database db) throws SQLException {
        db.deleteAll();
        RoleSeed.createRoleSeed(db);

        // USER_ROLES table record about user's role is automatically deleted
        db.deleteUser("bob");
        db.assignRole("george", RoleName.TECHNICIAN);

        SeedHelper.createUserWithRole("henry", "pass", RoleName.USER, db);
        SeedHelper.createUserWithRole("ida", "pass", RoleName.POWER_USER, db);
    }

    private static void ACLChange(Database db) throws SQLException {
        db.deleteAll();
        AclSeed.createAclSeed(db);

        // Access rights are automatically deleted through DB key
        db.deleteUser("bob");

        // If we want to change rights, it is simpler to first delete all of user's previous ones, so that we don't have
        // to worry about the difference.
        db.removeAccessRightsForUser("george");

        // When inserting a new user, we have to remember what rights each role has. We could save that into a variable
        // in the code.
        SeedHelper.insertAccessRightsForUser(
                "george",
                new AccessRight[]{
                        AccessRight.START, AccessRight.STOP, AccessRight.RESTART,
                        AccessRight.STATUS, AccessRight.READ_CONFIG, AccessRight.SET_CONFIG
                },
                db
        );
        SeedHelper.createUserWithAccessRights(
                "henry",
                "pass",
                new AccessRight[]{
                        AccessRight.PRINT,
                        AccessRight.QUEUE
                },
                db
        );

        final List<AccessRight> cecillasAccessRights = db.loadAccessRightsByUsername("cecilia");
        SeedHelper.createUserWithAccessRights(
                "ida",
                "pass",
                cecillasAccessRights.toArray(new AccessRight[0]),
                db
        );
    }

}
