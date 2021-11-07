package server_side.scripts;

import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.database.pojo.Role;

import java.sql.SQLException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class RoleSeed {

    public static void createRoleSeed(Database db) throws SQLException {
        final Role technician = new Role(
                "technician",
                new HashSet<>(),
                EnumSet.of(
                        AccessRight.START,
                        AccessRight.STOP,
                        AccessRight.RESTART,
                        AccessRight.STATUS,
                        AccessRight.READ_CONFIG,
                        AccessRight.SET_CONFIG
                )
        );

        final Role basicUser = new Role(
                "user",
                new HashSet<>(),
                EnumSet.of(
                        AccessRight.PRINT,
                        AccessRight.QUEUE
                )
        );

        final Role powerUser = new Role(
                "power_user",
                Set.of(basicUser),
                EnumSet.of(
                        AccessRight.TOP_QUEUE
                )
        );

        final Role admin = new Role(
                "admin",
                Set.of(powerUser, technician),
                Set.of()
        );

        // Needs to inserted in correct order to respect foreign keys
        db.insertRole(technician);
        db.insertRole(basicUser);
        db.insertRole(powerUser);
        db.insertRole(admin);

        // Alice - all
        SeedHelper.createUserWithRole(
                "alice",
                "pass",
                admin,
                db
        );

        // Bob - technician
        SeedHelper.createUserWithRole(
                "bob",
                "pass",
                technician,
                db
        );

        // cecilia power user: quee, top wueue, restart
        SeedHelper.createUserWithRole(
                "cecilia",
                "pass",
                powerUser,
                db
        );

        // david, erica, fred, goerge: print queue
        final String[] users = new String[]{"david", "erica", "fred", "george"};
        for (String user : users) {
            SeedHelper.createUserWithRole(
                    user,
                    "pass",
                    basicUser,
                    db
            );
        }
    }
}
