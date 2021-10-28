package server_side.responses;

public class CommandSuccess<T> implements CommandResponse<T>{
    final private T response;

    public CommandSuccess(T result) {
        this.response = result;
    }

    @Override
    public T getResponse() {
        return response;
    }
}
