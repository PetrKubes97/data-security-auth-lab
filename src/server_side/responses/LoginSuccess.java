package server_side.responses;

public record LoginSuccess(String accessToken) implements LoginResponse {
}