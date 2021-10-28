package server_side.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record UserRecord(
        String name,
        LocalDateTime lastLoginAttemptAt,
        Integer passwordGuesses,
        String hashedPassword,
        String salt,
        String accessToken
) {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    public String toTextLine() {
        return name + ',' +
                lastLoginAttemptAt.format(formatter) + ',' +
                passwordGuesses.toString() + ',' +
                hashedPassword + ',' +
                salt + ',' +
                accessToken + '\n';
    }

    public static UserRecord fromTextLine(String textLine) {
        final String[] line = textLine.split(",");

        final String username = line[0].trim();
        final String lastLoginAttemptAt = line[1].trim();
        LocalDateTime parsedLastLoginAttemptAt = LocalDateTime.parse(lastLoginAttemptAt, formatter);

        final String attempts = line[2].trim();
        final Integer parsedAttempts = Integer.parseInt(attempts);
        final String password = line[3].trim();
        final String salt = line[4].trim();
        final String accessToken = line[5].trim();


        return new UserRecord(
                username,
                parsedLastLoginAttemptAt,
                parsedAttempts,
                password,
                salt,
                accessToken);
    }

}
