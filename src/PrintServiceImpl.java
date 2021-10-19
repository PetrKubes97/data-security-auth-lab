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
        
       /* for(int i = 0; i<5; i++)
        {
        	System.out.println(" ");
        	System.out.println(Printers.get(i).printerName);
        	
        	for(int a = 0; a < 5; a++)
        	{
        		System.out.println(Printers.get(i).fileNames.get(a));
        	}
        }*/
        
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

    // prints file filename on the specified printer
    @Override
    public void print(String filename, String printer) {
    	int printerId = findPrinter(printer);
    	
    	if(printerId != -1)
    	{
    		//Todo Print
    	}

    }
    
    // lists the print queue for a given printer on the user's display in lines of the form <job number>   <file name>
    @Override
    public void queue(String printer) {
    	int printerId = findPrinter(printer);
    	
    	if(printerId != -1)
    	{
    		Printers.get(printerId).listQueue();
    	}
    }

    @Override
    public void topQueue(String printer, int job) {
    	int printerId = findPrinter(printer);
    	
    	if(printerId != -1)
    	{
    		Printers.get(printerId).moveFirstInQueue(job);
    	}
    }
    
    public int findPrinter(String printer)
    {
    	for(int i = 0; i < Printers.size(); i++)
    	{
    		if(Printers.get(i).printerName == printer)
    		{
    			return i;
    		}
    	}
    	return -1;
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
    
    // prints status of printer on the user's display
    @Override
    public void status(String printer) {

    }
    // prints the value of the parameter on the user's display
    @Override
    public void readConfig(String parameter) {

    }
    // sets the parameter to value
    @Override
    public void setConfig(String parameter, String value) {

    }
	
}
