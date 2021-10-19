import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    public static void main(String[] args) throws NotBoundException, MalformedURLException, RemoteException {
        try {
            PrintService server = (PrintService) Naming.lookup("rmi://127.0.0.1:5099/printer");
            System.out.println("----- " + server.echo("asdf"));
            server.print("filename", "printer");
            server.createPrinters();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}
