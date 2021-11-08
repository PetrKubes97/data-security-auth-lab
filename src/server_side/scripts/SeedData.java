package server_side.scripts;

import server_side.database.Database;
import server_side.middlewares.authorization.AuthorizationType;

import java.sql.SQLException;


public class SeedData {

    public static void main(String[] args) throws SQLException {
        final Database db = new Database();
        resetWithSeedData(db, AuthorizationType.ROLE);
    }

    public static void resetWithSeedData(Database db, AuthorizationType authorizationType) throws SQLException {
        db.deleteAll();

        if (authorizationType == AuthorizationType.ACL) {
            AclSeed.createAclSeed(db);
        } else {
            RoleSeed.createRoleSeed(db);
        }
    }
}
