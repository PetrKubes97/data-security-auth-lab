package server_side.data;
import java.time.LocalDateTime;

public record UserRecord(String name, String passwordGuessTimestamp, String passwordGuesses, String password, String salt) {
	
}
