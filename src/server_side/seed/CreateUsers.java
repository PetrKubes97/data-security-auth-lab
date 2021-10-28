package server_side.seed;

import server_side.data.FileDatabase;
import server_side.data.UserRecord;

import java.io.IOException;
import java.time.LocalDateTime;

import static server_side.crypto.Crypto.createPasswordHash;

public class CreateUsers {
    public static void main(String[] args) throws IOException {
        final FileDatabase db = new FileDatabase("users.txt");
        db.deleteAll();

        final String salt1 = "thisisasalt";
        final UserRecord user1 = new UserRecord(
                "user",
                LocalDateTime.now().minusDays(1),
                0,
                createPasswordHash( salt1, "pass"),
                salt1,
                null
        );
        db.insertUser(user1);

        final String salt2 = "anothersalt";
        final UserRecord user2 = new UserRecord(
                "test",
                LocalDateTime.now().minusDays(1),
                0,
                createPasswordHash( salt2, "test"),
                salt2,
                null
        );
        db.insertUser(user2);
    }
}
