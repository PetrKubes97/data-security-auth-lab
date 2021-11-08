package server_side.database;

import org.sqlite.SQLiteConfig;
import server_side.database.pojo.AccessRight;
import server_side.database.pojo.Role;
import server_side.database.pojo.UserRecord;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        // ROLES
        String createRolesSql = "CREATE TABLE IF NOT EXISTS ROLES " +
                "(NAME CHAR(50) PRIMARY KEY NOT NULL);";
        String createSubRolesSql = "CREATE TABLE IF NOT EXISTS SUB_ROLES " +
                "(TOP_ROLE CHAR(50) NOT NULL," +
                "BOTTOM_ROLE CHAR(50) NOT NULL," +
                "FOREIGN KEY(TOP_ROLE) REFERENCES ROLES(NAME), " +
                "FOREIGN KEY(BOTTOM_ROLE) REFERENCES ROLES(NAME)" +
                ");";
        String roleAccessRightsSql = "CREATE TABLE IF NOT EXISTS ROLE_ACCESS_RIGHTS " +
                "(ROLE CHAR(50) NOT NULL, " +
                "ACCESS_RIGHT CHAR(50) NOT NULL," +
                "FOREIGN KEY(ROLE) REFERENCES ROLES(NAME)" +
                ");";
        String userRolesSql = "CREATE TABLE IF NOT EXISTS USER_ROLE " +
                "(USER CHAR(50) NOT NULL, " +
                "ROLE CHAR(50) NOT NULL, " +
                "FOREIGN KEY(USER) REFERENCES USERS(NAME), " +
                "FOREIGN KEY(ROLE) REFERENCES ROLES(NAME)" +
                ");";


        stmt.executeUpdate(createSubjectSql);
        stmt.executeUpdate(createAccessRightsSql);
        // ROLES
        stmt.executeUpdate(createRolesSql);
        stmt.executeUpdate(createSubRolesSql);
        stmt.executeUpdate(roleAccessRightsSql);
        stmt.executeUpdate(userRolesSql);
        stmt.close();
    }

    public void insertRole(Role role) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "INSERT INTO ROLES (NAME) " +
                "VALUES ('" + role.name + "');";
        // Needs to be executed to keep foreign key happy
        stmt.executeUpdate(sql);

        StringBuilder sb = new StringBuilder();

        for (AccessRight accessRight : role.extraAccessRights) {
            String roleAccessSql = "INSERT INTO ROLE_ACCESS_RIGHTS (ROLE, ACCESS_RIGHT) " +
                    "VALUES (" +
                    "'" + role.name + "'," +
                    "'" + accessRight.toString() + "'" +
                    ");";
            sb.append(roleAccessSql);
        }

        for (Role subRole : role.subRoles) {
            String subRoleSql = "INSERT INTO SUB_ROLES (TOP_ROLE, BOTTOM_ROLE) VALUES " +
                    "('" + role.name + "', '" + subRole.name + "');";
            sb.append(subRoleSql);
        }

        stmt.executeUpdate(sb.toString());
        stmt.close();
    }

    private Role getRoleByName(String name) throws SQLException {
        Statement statement = connection.createStatement();
        // Just check if role exists
        ResultSet rs = statement.executeQuery("SELECT * FROM ROLES WHERE NAME='" + name + "';");
        if (!rs.next()) return null;
        rs.close();
        statement.close();

        List<String> subRoleNames = getNamesOfSubRoles(name);
        Set<Role> subRoles = new HashSet<>();
        for (String subRoleName : subRoleNames) {
            subRoles.add(getRoleByName(subRoleName));
        }

        Set<AccessRight> accessRights = getRoleSpecificAccessRights(name);

        return new Role(name, subRoles, accessRights);
    }

    private List<String> getNamesOfSubRoles(String roleName) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM SUB_ROLES WHERE TOP_ROLE='" + roleName + "';");
        ArrayList<String> result = new ArrayList<>();

        while (rs.next()) {
            result.add(rs.getString("BOTTOM_ROLE"));
        }
        rs.close();
        statement.close();
        return result;
    }

    private Set<AccessRight> getRoleSpecificAccessRights(String roleName) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM ROLE_ACCESS_RIGHTS WHERE ROLE='" + roleName + "';");
        Set<AccessRight> result = new HashSet<>();

        while (rs.next()) {
            result.add(
                    AccessRight.valueOf(rs.getString("ACCESS_RIGHT"))
            );
        }
        rs.close();
        statement.close();

        return result;
    }

    public void assignRole(String userName, Role role) throws SQLException {
        Statement stmt = connection.createStatement();
        String sql = "INSERT INTO USER_ROLE (USER, ROLE) " +
                "VALUES ('" + userName + "', '" + role.name + "');";
        stmt.executeUpdate(sql);
    }

    public Role getRoleByUsername(String username) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM USER_ROLE WHERE USER='" + username + "';");
        String roleName = null;

        if (rs.next()) {
            roleName = rs.getString("ROLE");
        }
        rs.close();
        statement.close();

        if (roleName == null) {
            throw new IllegalStateException("Role not found");
        }

        return getRoleByName(roleName);
    }

    public void insertAccessRight(String username, AccessRight accessRight) throws SQLException {
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

        if (rs.next()) {
            result = UserRecord.fromRS(rs);
        }
        rs.close();
        statement.close();

        return result;
    }

    public List<AccessRight> loadAccessRightsByUsername(String username) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM ACCESS_RIGHTS WHERE SUBJECT='" + username + "';");
        ArrayList<AccessRight> result = new ArrayList<>();

        if (rs.next()) {
            result.add(
                    AccessRight.valueOf(rs.getString("ACCESS_RIGHT"))
            );
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
        String sql = "DELETE FROM ACCESS_RIGHTS;" +
                "DELETE FROM ROLE_ACCESS_RIGHTS;" +
                "DELETE FROM SUB_ROLES;" +
                "DELETE FROM USER_ROLE;" +
                "DELETE FROM ROLES;" +
                "DELETE FROM USERS;";
        statement.executeUpdate(sql);
    }

    public void updateUser(UserRecord updatedUserRecord) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "UPDATE USERS set " +
                "LAST_LOGIN_AT='" + updatedUserRecord.lastLoginAtStr() + "', " +
                "PASSWORD_GUESSES='" + updatedUserRecord.passwordGuesses() + "', " +
                "HASHED_PASSWORD='" + updatedUserRecord.hashedPassword() + "', " +
                "SALT='" + updatedUserRecord.salt() + "'," +
                "ACCESS_TOKEN='" + updatedUserRecord.accessToken() + "' " +
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


