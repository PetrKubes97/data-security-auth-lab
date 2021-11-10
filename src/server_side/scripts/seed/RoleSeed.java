package server_side.scripts.seed;

import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.database.pojo.Role;
import server_side.database.pojo.RoleName;

import java.sql.SQLException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public class RoleSeed {

    public static void createRoleSeed(Database db) throws SQLException {
        final Role technician = new Role(
                RoleName.TECHNICIAN,
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
                RoleName.USER,
                new HashSet<>(),
                EnumSet.of(
                        AccessRight.PRINT,
                        AccessRight.QUEUE
                )
        );

        final Role powerUser = new Role(
                RoleName.POWER_USER,
                Set.of(basicUser),
                EnumSet.of(
                        AccessRight.TOP_QUEUE
                )
        );

        final Role admin = new Role(
                RoleName.ADMIN,
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
                admin.name,
                db
        );

        // Bob - technician
        SeedHelper.createUserWithRole(
                "bob",
                "pass",
                technician.name,
                db
        );

        // cecilia power user: quee, top wueue, restart
        SeedHelper.createUserWithRole(
                "cecilia",
                "pass",
                powerUser.name,
                db
        );

        // david, erica, fred, goerge: print queue
        final String[] users = new String[]{"david", "erica", "fred", "george"};
        for (String user : users) {
            SeedHelper.createUserWithRole(
                    user,
                    "pass",
                    basicUser.name,
                    db
            );
        }
    }
}
