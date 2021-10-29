package server_side.responses;

public class CommandFailure<T> implements CommandResponse<T> {
    final boolean logout;
    final String error;

    public CommandFailure(String error) {
        this.error = error;
        this.logout = false;
    }

    public CommandFailure(String error, boolean logout) {
        this.error = error;
        this.logout = logout;
    }

    @Override
    public T getResponse() {
        return null;
    }

    public String getError() {
        return error;
    }

    public boolean getLogout() {
        return logout;
    }
}
