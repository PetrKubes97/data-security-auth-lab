package server_side;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ApplicationServer {

    public static void main(String[] args) throws RemoteException, FileNotFoundException {
        // https://stackoverflow.com/a/28800991
        System.setProperty("java.rmi.server.hostname","127.0.0.1");
        Registry registry = LocateRegistry.createRegistry(5099);
        registry.rebind("printer", new PrintServiceImpl());
    }
}
