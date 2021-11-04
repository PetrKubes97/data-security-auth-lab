package server_side.middlewares;

import server_side.logger.Logger;
import server_side.logger.LoggerImpl;
import server_side.crypto.Crypto;
import server_side.database.Database;
import server_side.database.pojo.UserRecord;
import server_side.responses.CommandFailure;
import server_side.responses.LoginFailure;
import server_side.responses.LoginResponse;
import server_side.responses.LoginSuccess;

import java.sql.SQLException;
import java.time.LocalDateTime;

import static server_side.crypto.Crypto.createPasswordHash;

public class Authenticator {
    private final Database database;
    private final Logger logger;

    public Authenticator(Database database, Logger logger) {
        this.database = database;
        this.logger = logger;
    }

    private Boolean limitUserGuesses(String username) throws SQLException {
        final UserRecord user = database.loadUserByUsername(username);
        if (user == null) return false;

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime minusFiveMin = currentDateTime.minusMinutes(5);

        //Less than 3 tries and less than 5 minutes from last try
        if (user.passwordGuesses() < 3 && user.lastLoginAttemptAt().isAfter(minusFiveMin)) {
            database.loginUpdate(user, user.passwordGuesses() + 1, currentDateTime);
            return true;
        }
        //More than 5 minutes from last try
        else if (user.lastLoginAttemptAt().isBefore(minusFiveMin)) {
            database.loginUpdate(user, 0, currentDateTime);
            return true;
        } else {
            return false;
        }
    }

    public LoginResponse login(String username, String password) {
        try {
            final UserRecord user = database.loadUserByUsername(username);

            if (limitUserGuesses(username)) {
                final String saltedHashedPassword = createPasswordHash(user.salt(), password);
                if (user.hashedPassword().equals(saltedHashedPassword)) {
                    final String accessToken = Crypto.generateAccessToken();

                    database.loginUpdate(
                            user,
                            0,
                            LocalDateTime.now(),
                            accessToken
                    );
                    logger.logToFile(username, "logged in successfully");
                    return new LoginSuccess(accessToken);
                }
            } else {
                logger.logToFile(username, "failed to login due to excessive attempts");
                return new LoginFailure("Too many attempts, try again later");
            }

            logger.logToFile(username, "failed to login due to incorrect credentials");
            return new LoginFailure("Username or password incorrect");
        } catch (SQLException e) {
            return new LoginFailure("SQL errr " + e);
        }
    }

    public <T> AuthenticationResult<T> authenticated(String accessToken) {
        try {
            UserRecord user = database.loadUserByAccessToken(accessToken);
            if (user != null) {
                if (user.lastLoginAttemptAt().isBefore(LocalDateTime.now().minusMinutes(15))) {
                    logger.logToFile("unknown user", "failed to authenticate with old access token");

                    return new AuthenticationResult<>(
                            new CommandFailure<>("Access token expired", true),
                            null
                    );
                }

                return new AuthenticationResult<>(
                        null,
                        user.name()
                );
            } else {
                logger.logToFile("unknown user", "failed to authenticate with invalid access token");
                return new AuthenticationResult<>(
                        new CommandFailure<>("Access token not valid", true),
                        null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return new AuthenticationResult<>(
                    new CommandFailure<>("Authentication problem", true),
                    null
            );
        }
    }
}
