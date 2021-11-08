package server_side.middlewares.authorization;

import server_side.responses.CommandFailure;

public record AuthorizationResult<T>(CommandFailure<T> commandFailure) {
}
