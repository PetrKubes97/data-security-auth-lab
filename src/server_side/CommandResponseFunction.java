package server_side;

import server_side.responses.CommandResponse;

public interface CommandResponseFunction<T> {
    CommandResponse<T> call();
}
