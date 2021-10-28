package server_side.data;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileDatabase {

    private File file;

    public FileDatabase(String fileLocation) {
        file = new File(fileLocation);
    }

    public UserRecord loadUserByUsername(String username) throws FileNotFoundException {
        final List<UserRecord> allUsers = loadAllUsers();
        for (UserRecord user : allUsers) {
            if (user.name().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public UserRecord loadUserByAccessToken(String accessToken) throws FileNotFoundException {
        final List<UserRecord> allUsers = loadAllUsers();
        for (UserRecord user : allUsers) {
            if (user.accessToken() != null && user.accessToken().equals(accessToken)) {
                return user;
            }
        }
        return null;
    }

    public List<UserRecord> loadAllUsers() throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("\n");

        final ArrayList<UserRecord> users = new ArrayList<>();

        while (scanner.hasNext()) {
            final String line = scanner.next();
            users.add(UserRecord.fromTextLine(line));
        }

        return users;
    }

    public void insertUser(UserRecord user) throws IOException {
        FileWriter fileWriter = new FileWriter(file, true);
        BufferedWriter out = new BufferedWriter(fileWriter);
        out.write(user.toTextLine());
        out.close();
    }

    // https://stackoverflow.com/a/1377322
    public void deleteUserByName(String username) throws IOException {
        File inputFile = file;
        File tempFile = new File("temp.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;
        while ((currentLine = reader.readLine()) != null) {
            UserRecord userRecord = UserRecord.fromTextLine(currentLine);
            // Copies all lines not matching the username
            if (userRecord.name().equals(username)) continue;
            writer.write(userRecord.toTextLine());
        }
        writer.close();
        reader.close();

        boolean successful = tempFile.renameTo(inputFile);
        // Might not be necessary?
        file = new File(inputFile.getName());
    }

    public void deleteAll() throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fileWriter);
        out.write("");
        out.close();
    }

    public void updateUser(UserRecord updatedUserRecord) throws IOException {
        deleteUserByName(updatedUserRecord.name());
        insertUser(updatedUserRecord);
    }


    public void loginUpdate(UserRecord userRecord, Integer newAttempts, LocalDateTime newAttemptTime) throws IOException {
        loginUpdate(userRecord, newAttempts, newAttemptTime, userRecord.accessToken());
    }

    public void loginUpdate(UserRecord userRecord, Integer newAttempts, LocalDateTime newAttemptTime, String accessToken) throws IOException {
        // This is unfortunate, I was thinking that java records are like kotlin data classes, but apparently they are very dumb
        final UserRecord updatedUser = new UserRecord(
                userRecord.name(),
                newAttemptTime,
                newAttempts,
                userRecord.hashedPassword(),
                userRecord.salt(),
                accessToken
        );
        updateUser(updatedUser);
    }
}
