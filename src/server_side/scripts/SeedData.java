package server_side.scripts;

import server_side.database.Database;
import java.sql.SQLException;


public class SeedData {

    public static void main(String[] args) throws SQLException {
        final Database db = new Database();
        resetWithSeedData(db);
    }

    public static void resetWithSeedData(Database db) throws SQLException {
        db.deleteAll();
//        AclSeed.createAclSeed(db);
        RoleSeed.createRoleSeed(db);
    }


}
