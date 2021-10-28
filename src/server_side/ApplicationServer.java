package server_side;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ApplicationServer {

    //public static ArrayList<ServerSide.Printer> Printers;
    public static void main(String[] args) throws RemoteException, FileNotFoundException {
        Registry registry = LocateRegistry.createRegistry(5099);
        registry.rebind("printer", new PrintServiceImpl());
    }
}
