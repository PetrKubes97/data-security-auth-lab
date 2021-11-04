package server_side.middlewares;

import server_side.responses.CommandFailure;

public record AuthorizationResult<T>(CommandFailure<T> commandFailure) {
}
