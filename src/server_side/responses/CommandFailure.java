package server_side.responses;

public class CommandFailure<T> implements CommandResponse<T>{
    final String error;

    public CommandFailure(String error) {
        this.error = error;
    }

    @Override
    public T getResponse() {
        return null;
    }
}
