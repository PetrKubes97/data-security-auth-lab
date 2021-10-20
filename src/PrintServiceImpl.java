import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class PrintServiceImpl extends UnicastRemoteObject implements PrintService {
	
	public  ArrayList<Printer> Printers;
	
	private boolean PrinterRunning = true;
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
    	if(isServerRunning() == false) {return;}
    	int printerId = findPrinter(printer);
    	
    	if(printerId != -1)
    	{
    		Printers.get(printerId).fileNames.add(filename);
    	}

    }
    
    // lists the print queue for a given printer on the user's display in lines of the form <job number>   <file name>
    @Override
    public void queue(String printer) {
    	if(isServerRunning() == false) {return;}
    	
    	int printerId = findPrinter(printer);
    	
    	if(printerId != -1)
    	{
    		Printers.get(printerId).listQueue();
    	}
    }

    @Override
    public void topQueue(String printer, int job) {
    	if(isServerRunning() == false) {return;}
    		
    	
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
    	PrinterRunning = true;
    	System.out.println("Print server started");
    }

    @Override
    public void stop() {
    	PrinterRunning = false;
    	System.out.println("Print server stopped");
    }

    @Override
    public void restart() {
    	PrinterRunning = true;
    	for(int i = 0; i < Printers.size(); i++)
    	{
    		Printers.get(i).restartPrinter();
    	}
    }
    
    public boolean isServerRunning()
    {
    	if(PrinterRunning == false)
    	{
    		System.out.println("Print server is not running");
    	}
    	return PrinterRunning;
    }
    // prints status of printer on the user's display
    @Override
    public void status(String printer) {
    	if(isServerRunning() == false) {return;}

    }
    // prints the value of the parameter on the user's display
    @Override
    public void readConfig(String parameter) {
    	if(isServerRunning() == false) {return;}

    }
    // sets the parameter to value
    @Override
    public void setConfig(String parameter, String value) {
    	if(isServerRunning() == false) {return;}

    }
	
}
