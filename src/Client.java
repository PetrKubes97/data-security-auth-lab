import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.io.FileReader;  

public class Client {
	
	private static PrintService server;
	private static Scanner scanner;
    public static void main(String[] args) throws NotBoundException, MalformedURLException, RemoteException {
        try {
            server = (PrintService) Naming.lookup("rmi://127.0.0.1:5099/printer");
            System.out.println("----- " + server.echo("asdf"));
            server.createPrinters();
            loginPrompt();
            commandPrompt();
            
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    public static void loginPrompt()
    {
    	 String username = "", password ="";
         scanner = new Scanner(System.in);
         
         do
         {
         	System.out.print("Enter username:");
             username = scanner.nextLine();
             
             System.out.print("Enter password:");
             password = scanner.nextLine();
         }while(!verifyLogin(username, password));
         
    }
    public static void commandPrompt()
    {
    	String command = "";
        do
        {
        	System.out.print("Enter command: ");
            command = scanner.nextLine();
            processCommand(command.toLowerCase());
        }while (command != "q");// && command != "Q" && command != "quit");
    	
    }
    
    public static void processCommand(String command)
    {
    	String filename = "", printer = "", jobId = "";
    	
    	if(command.equals("print"))
    	{
    		System.out.print("Enter filename:");
            filename = scanner.nextLine();
            System.out.print("Enter printer name:");
            filename = scanner.nextLine();
    		try {
				server.print(filename, printer);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
    		return;
    	}
    	else if(command.equals("queue"))
    	{
            System.out.print("Enter printer name:");
            printer = scanner.nextLine();
    		try {
				server.queue(printer);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		return;
    	}
    	else if(command.equals("top queue") || command.equals("topqueue"))
    	{
    		
            System.out.print("Enter printer name:");
            printer = scanner.nextLine();
    		
    		System.out.print("Enter job id:");
            jobId = scanner.nextLine();
    		try {
				server.topQueue(printer, Integer.valueOf(jobId));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    		return;
    	}
    	else if(command.equals("start"))
    	{
    		try {
				server.start();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return;
    	}
    	else if(command.equals("stop"))
    	{
    		try {
				server.stop();
				return;
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return;
    	}
    	else if(command.equals("restart"))
    	{
    		try {
				server.restart();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return;
    	}
    	else if(command.equals("status"))
    	{
    		try {
				server.status(printer);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return;
    	}
    	else if(command.equals("read config") || command.equals("readconfig"))
    	{
    		//server.readConfig(scanner);
    		return;
    	}
    	else if(command.equals("set config") || command.equals("setconfig"))
    	{
    		//server.setConfig(scanner, config);
    		return;
    	}
    	
    	System.out.println("Command not found");
    }
    
    public static boolean verifyLogin(String username, String password)
    {
    	boolean foundUser = false;
    	try{
    		//Scanner scanner2 = new Scanner(System.in);

            File file = new File("Login.txt");
            Scanner scanner2 = new Scanner(file);
	    	scanner2.useDelimiter("[,\n]");
		   
	    	
	    	while(scanner2.hasNext() && !foundUser) {
	    		if(scanner2.next().trim().equals(username) && scanner2.next().trim().equals(password))
	    		{
	    			
	    			System.out.println("Login successful");
	    			//scanner2.close();
	    			return true;
	    		}
	    	}
	        scanner2 = new Scanner(System.in);
	    	//scanner2.close();
	    	System.out.println("Wrong username or password");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    	return false;
    }
    
}
