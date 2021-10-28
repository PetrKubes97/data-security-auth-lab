package server_side;

import server_side.crypto.Crypto;
import server_side.data.FileDatabase;
import server_side.data.UserRecord;
import server_side.responses.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;

import static server_side.crypto.Crypto.createPasswordHash;

public class PrinterAuthenticator {
    private final FileDatabase database;
    private final Logger logger;

    public PrinterAuthenticator(FileDatabase database, Logger logger) {
        this.database = database;
        this.logger = logger;
    }

    private Boolean limitUserGuesses(String username) throws IOException {
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

    public LoginResponse login(String username, String password) throws IOException {
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
    }

    public <T> CommandResponse<T> authenticated(String accessToken, String commandName, CommandResponseFunction<T> function) {
        try {
            UserRecord user = database.loadUserByAccessToken(accessToken);
            if (user != null) {
                if (user.lastLoginAttemptAt().isBefore(LocalDateTime.now().minusMinutes(15))) {
                    logger.logToFile("unknown user", "failed to authenticate with old access token");
                    return new CommandFailure<>("Access token expired");
                }

                logger.logToFile(user.name(), "executed command: " + commandName);
                return function.call();
            } else {
                logger.logToFile("unknown user", "failed to authenticate with invalid access token");
                return new CommandFailure<>("Access token not valid");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new CommandFailure<>("Authentication problem");
        }
    }
}
