import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class PrintServiceImpl extends UnicastRemoteObject implements PrintService {
	
	public static ArrayList<Printer> Printers;
	
	@Override
    public void createPrinters() {
		Printers = new ArrayList<Printer>(10);
        for(int i = 0; i<5; i++)
        {
        	Printers.add(new Printer("printer" + Integer.toString(i), 50));
        }
        
        for(int i = 0; i<5; i++)
        {
        	Printers.get(i).addFile("File1");
        	Printers.get(i).addFile("File2");
        	Printers.get(i).addFile("File3");
        	Printers.get(i).addFile("File4");
        	Printers.get(i).addFile("File5");
        }
        
        for(int i = 0; i<5; i++)
        {
        	System.out.println(" ");
        	System.out.println(Printers.get(i).printerName);
        	
        	for(int a = 0; a < 5; a++)
        	{
        		System.out.println(Printers.get(i).fileNames.get(a));
        	}
        
        	
        }
        
    }
	
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
    	 System.out.println(filename + printer);
    	 
    	 
         

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
