package server_side;

import server_side.responses.CommandResponse;
import server_side.responses.LoginResponse;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PrintService extends Remote {

    LoginResponse login(String username, String password) throws RemoteException, IOException;

    // prints file filename on the specified printer
    CommandResponse<Void> print(String filename, String printer, String accessToken) throws RemoteException;

    // lists the print queue for a given printer on the user's display in lines of the form <job number>   <file name>
    CommandResponse<String> queue(String printer, String accessToken) throws RemoteException;

    // moves job to the top of the queue
    CommandResponse<Void> topQueue(String printer, int job, String accessToken) throws RemoteException;

    // starts the print server
    CommandResponse<Void> start(String accessToken) throws RemoteException;

    // stops the print server
    CommandResponse<Void> stop(String accessToken) throws RemoteException;

    // stops the print server, clears the print queue and starts the print server again
    CommandResponse<Void> restart(String accessToken) throws RemoteException;

    // prints status of printer on the user's display
    CommandResponse<String> status(String printer, String accessToken) throws RemoteException;

    // prints the value of the parameter on the user's display
    CommandResponse<String> readConfig(String parameter, String accessToken) throws RemoteException;

    // sets the parameter to value
    CommandResponse<String> setConfig(String parameter, String value, String accessToken) throws RemoteException;


}
