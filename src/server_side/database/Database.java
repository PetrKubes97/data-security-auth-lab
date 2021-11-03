package server_side.database;

import org.sqlite.SQLiteConfig;
import server_side.database.pojo.AccessRight;
import server_side.database.pojo.UserRecord;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private Connection connection;
    private static final String DB_URL = "jdbc:sqlite:database.db";
    private static final String DRIVER = "org.sqlite.JDBC";

    public static void main(String[] args) {
        Database test = new Database();
    }

    public Database() {
        try {
            Class.forName(DRIVER);
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            connection = DriverManager.getConnection(DB_URL, config.toProperties());
            createTables();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }


    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();
        String createSubjectSql = "CREATE TABLE IF NOT EXISTS USERS (" +
                "NAME TEXT PRIMARY KEY NOT NULL," +
                "LAST_LOGIN_AT TEXT NOT NULL," +
                "PASSWORD_GUESSES INTEGER NOT NULL ," +
                "HASHED_PASSWORD TEXT NOT NULL ," +
                "SALT TEXT NOT NULL ," +
                "ACCESS_TOKEN TEXT NOT NULL " +
                ")";
        String createAccessRightsSql = "CREATE TABLE IF NOT EXISTS ACCESS_RIGHTS " +
                "(SUBJECT CHAR(50) NOT NULL," +
                "ACCESS_RIGHT CHAR(50) NOT NULL," +
                "FOREIGN KEY(SUBJECT) REFERENCES USERS(NAME)" +
                ")";
        stmt.executeUpdate(createSubjectSql);
        stmt.executeUpdate(createAccessRightsSql);
        stmt.close();
    }

    private void insertAccessRight(String username, AccessRight accessRight) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "INSERT INTO ACCESS_RIGHTS (SUBJECT, ACCESS_RIGHT) " +
                "VALUES ('" + username + "', '" + accessRight.toString() + "');";
        stmt.executeUpdate(sql);
    }

    public UserRecord loadUserByUsername(String username) throws SQLException {
        return loadUserBy("NAME", username);
    }

    public UserRecord loadUserByAccessToken(String accessToken) throws SQLException {
        return loadUserBy("ACCESS_TOKEN", accessToken);
    }

    private UserRecord loadUserBy(String column, String value) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM USERS WHERE " + column + "='" + value + "';");
        UserRecord result = null;

        if (rs.first()) {
            result = UserRecord.fromRS(rs);
        }
        rs.close();
        statement.close();

        return result;
    }

    public List<UserRecord> loadAllUsers() throws SQLException {
        ArrayList<UserRecord> users = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM USERS;");

        while (rs.next()) {
            users.add(UserRecord.fromRS(rs));
        }
        rs.close();
        statement.close();
        return users;
    }


    public void insertUser(UserRecord user) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "INSERT INTO USERS (NAME, LAST_LOGIN_AT, PASSWORD_GUESSES, HASHED_PASSWORD, SALT, ACCESS_TOKEN) " +
                "VALUES (" +
                "'" + user.name() + "', " +
                "'" + user.lastLoginAtStr() + "', " +
                "" + user.passwordGuesses() + ", " +
                "'" + user.hashedPassword() + "', " +
                "'" + user.salt() + "', " +
                "'" + user.accessToken() + "'" +
                ");";
        stmt.executeUpdate(sql);
    }

    public void deleteUserByName(String username) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "DELETE from USERS where NAME='" + username + "';";
        statement.executeUpdate(sql);
    }

    public void deleteAll() throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "DELETE from USERS;";
        statement.executeUpdate(sql);
    }

    public void updateUser(UserRecord updatedUserRecord) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "UPDATE USERS set " +
                "LAST_LOGIN_AT='" + updatedUserRecord.lastLoginAtStr() + "'," +
                "PASSWORD_GUESSES='" + updatedUserRecord.passwordGuesses() + "'," +
                "HASHED_PASSWORD='" + updatedUserRecord.hashedPassword() + "'," +
                "SALT='" + updatedUserRecord.salt() + "'," +
                "ACCESS_TOKEN='" + updatedUserRecord.accessToken() + "'," +
                "WHERE NAME='" + updatedUserRecord.name() + "';";
        statement.executeUpdate(sql);
    }


    public void loginUpdate(UserRecord userRecord, Integer newAttempts, LocalDateTime newAttemptTime) throws SQLException {
        loginUpdate(userRecord, newAttempts, newAttemptTime, userRecord.accessToken());
    }

    public void loginUpdate(UserRecord userRecord, Integer newAttempts, LocalDateTime newAttemptTime, String accessToken) throws SQLException {
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
