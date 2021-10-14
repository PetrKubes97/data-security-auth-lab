import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrintService extends Remote {
    String echo(String input) throws RemoteException;

    String login(String username, String password);

    // prints file filename on the specified printer
    void print(String filename, String printer);

    // lists the print queue for a given printer on the user's display in lines of the form <job number>   <file name>
    void queue(String printer);

    // moves job to the top of the queue
    void topQueue(String printer, int job);

    // starts the print server
    void start();

    // stops the print server
    void stop();

    // stops the print server, clears the print queue and starts the print server again
    void restart();

    // prints status of printer on the user's display
    void status(String printer);

    // prints the value of the parameter on the user's display
    void readConfig(String parameter);

    // sets the parameter to value
    void setConfig(String parameter, String value);
}
