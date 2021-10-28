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

    public PrinterAuthenticator(FileDatabase database) {
        this.database = database;
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

                return new LoginSuccess(accessToken);
            }
        } else {
            return new LoginFailure("Too many attempts, try again later");
        }

        return new LoginFailure("Username or password incorrect");
    }

    public <T> CommandResponse<T> authenticated(String accessToken, CommandResponseFunction<T> function) {
        try {
            UserRecord user = database.loadUserByAccessToken(accessToken);
            if (user != null) {
                System.out.println("Authenticated user run a command");
                return function.call();
            } else {
                System.out.println("Unsuccessfull attempt to run command");
                return new CommandFailure<>("Access token expired");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new CommandFailure<>("Authentication problem");
        }
    }
}
