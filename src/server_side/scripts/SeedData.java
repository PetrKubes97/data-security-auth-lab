package server_side.scripts;

import server_side.database.Database;
import server_side.database.pojo.AccessRight;
import server_side.database.pojo.UserRecord;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static server_side.crypto.Crypto.createPasswordHash;
import static server_side.crypto.Crypto.getNextSalt;

public class SeedData {

    public static void main(String[] args) throws SQLException {
        final Database db = new Database();
        resetWithSeedData(db);
    }

    public static void resetWithSeedData(Database db) throws SQLException {
        db.deleteAll();
//        AclSeed.createAclSeed(db);
    }


}
