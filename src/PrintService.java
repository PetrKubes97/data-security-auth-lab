import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrintService extends Remote {
	
	void createPrinters() throws RemoteException;
	
	
    String echo(String input) throws RemoteException;

    String login(String username, String password) throws RemoteException;
    
    // prints file filename on the specified printer
    void print(String filename, String printer) throws RemoteException;

    // lists the print queue for a given printer on the user's display in lines of the form <job number>   <file name>
    void queue(String printer) throws RemoteException;

    // moves job to the top of the queue
    void topQueue(String printer, int job) throws RemoteException;

    // starts the print server
    void start() throws RemoteException;

    // stops the print server
    void stop() throws RemoteException;

    // stops the print server, clears the print queue and starts the print server again
    void restart() throws RemoteException;

    // prints status of printer on the user's display
    void status(String printer) throws RemoteException;

    // prints the value of the parameter on the user's display
    void readConfig(String parameter) throws RemoteException;

    // sets the parameter to value
    void setConfig(String parameter, String value) throws RemoteException;


}
