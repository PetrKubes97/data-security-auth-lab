package server_side.responses;


public record LoginFailure(String reason) implements LoginResponse {
}