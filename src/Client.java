import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;


public class Client {
	
    public static void main(String[] args) throws NotBoundException, MalformedURLException, RemoteException {
        try {
            PrintService server = (PrintService) Naming.lookup("rmi://127.0.0.1:5099/printer");
            System.out.println("----- " + server.echo("asdf"));
            String username = "", password ="";
            Scanner input = new Scanner(System.in);
            do
            {
            	System.out.print("Enter username:");
                username = input.nextLine();
                
                System.out.print("Enter password:");
                password = input.nextLine();
            }while(!verifyLogin(username, password));
            
            input.close();
            server.createPrinters();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    
    public static boolean verifyLogin(String username, String password)
    {
    	boolean foundUser = false;
    	try{
    		//File file = new File("Login.txt");
    		Scanner scanner = new Scanner(System.in);

            File file = new File("Login.txt");
            scanner = new Scanner(file);
	    	scanner.useDelimiter("[,\n]");
	    	
	    	while(scanner.hasNext() && !foundUser) {
	    		if(scanner.next().trim().equals(username) && scanner.next().trim().equals(password))
	    		{
	    			scanner.close();
	    			System.out.println("Login successful");
	    			return true;
	    		}
	    	}
	    	scanner.close();
	    	System.out.println("Wrong username or password");
	    	
    	
    	
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		
    		
    	}
    	
    	return false;
    }
    
}
