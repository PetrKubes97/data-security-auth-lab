import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class PrintServiceImpl extends UnicastRemoteObject implements PrintService {

    public PrintServiceImpl() throws RemoteException {
        super();
    }

    public String echo(String input) throws RemoteException {
        System.out.println("echo");
        return "From server: " + input;
    }

    @Override
    public String login(String username, String password) {
        return null;
    }

    @Override
    public void print(String filename, String printer) {

    }

    @Override
    public void queue(String printer) {

    }

    @Override
    public void topQueue(String printer, int job) {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void restart() {

    }

    @Override
    public void status(String printer) {

    }

    @Override
    public void readConfig(String parameter) {

    }

    @Override
    public void setConfig(String parameter, String value) {

    }
}
