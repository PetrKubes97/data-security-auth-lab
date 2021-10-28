package server_side.responses;

import java.io.Serializable;

public interface CommandResponse<T> extends Serializable {
    T getResponse();
}
