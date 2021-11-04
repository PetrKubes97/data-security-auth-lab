package server_side.middlewares;

import server_side.responses.CommandFailure;

public record AuthenticationResult<T>(CommandFailure<T> commandFailure, String userName) {
}
