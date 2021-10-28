package server_side;

import server_side.data.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;


public class PrintServiceImpl extends UnicastRemoteObject implements PrintService {

    private ArrayList<Printer> printers;

    private boolean printersServerRunning = true;

    public PrintServiceImpl() throws RemoteException, FileNotFoundException {
        super();
        createPrinters();
    }

    private void createPrinters() {
        printers = new ArrayList<>(10);
        for (int i = 0; i < 5; i++) {
            Printer newPrinter = new Printer("printer" + i, 50);
            newPrinter.addFile("File1");
            newPrinter.addFile("File2");
            newPrinter.addFile("File5");
            newPrinter.addFile("File3");
            newPrinter.addFile("File4");
            printers.add(newPrinter);
        }
    }

    @Override
    public String echo(String input) throws RemoteException {
        System.out.println("echo");
        return "From server: " + input;
    }
    
    private String createHashPassword(String password){
    	 SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
    	 byte[] digest = digestSHA3.digest(password.getBytes());
    	 byte[] salt = new byte[50];
    	 salt = getNextSalt();
    	 //To do Save salt, pw and user in txt?
    	 ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    	 
    	 //To do Fix:
    	 try {
			outputStream.write(salt);
			outputStream.write(digest);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	 
    	byte saltedPassword[] =  outputStream.toByteArray();
    	
    	return Hex.toHexString(saltedPassword );
    }
    
    private String getHashPassword(String salt, String password){
    	 SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest512();
    	 byte[] digest = digestSHA3.digest(password.getBytes());	
    	 ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    	 
    	 //To do Fix:
    	 try {
			outputStream.write(salt.getBytes());
			outputStream.write(digest);
		 } catch (IOException e) {
				
			e.printStackTrace();
		 }
    	 
    	byte saltedPassword[] =  outputStream.toByteArray( );
    	return Hex.toHexString(saltedPassword );
    }
    
    public static byte[] getNextSalt() {
    	SecureRandom rand = new SecureRandom();
    	byte salt[] = new byte[50];
    	rand.nextBytes(salt);
    	
        return salt;
      }
    
    //Pw and usernames
    //user,pass
    //test,test
    //user1,pass1
    
    public Boolean limitUserGuesses(String username) throws IOException {
    	final List<UserRecord> users = loadUsersFromFile();
    	LocalDateTime currentDateTime = LocalDateTime.now();
    	final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    	//Todo Fix
    	for (UserRecord user : users) {
    		if(user.name().equals(username))
    		{
    			LocalDateTime userTimeStamp = LocalDateTime.parse(user.passwordGuessTimestamp(), formatter);
    	    	int userAttempts = Integer.valueOf(user.passwordGuesses());
    	    	LocalDateTime minusFiveMin = currentDateTime.minusMinutes(5);
    	       	//Less than 3 tries and less than 5 minutes from last try
    	    	if(userAttempts < 3 && userTimeStamp.isAfter(minusFiveMin)) {
    	    		updateLoginData(user, userTimeStamp, userAttempts + 1, users);
    	    		return true;
    	    	}
    	    	//More than 5 minutes from last try
    	    	else if(userTimeStamp.isBefore(minusFiveMin)) {    	    		
    	    		updateLoginData(user, currentDateTime, 0, users);
    	    		return true;
    	    	}
    	    	else {
    	    		System.out.println("Return False");
    	    		return false;
    	    	}	
    		}
    	}
    	return false;
    }
    
    public void updateLoginData(UserRecord user, LocalDateTime timeStamp, int attempts, List<UserRecord> users) throws IOException  {
    	
    	String findString = user.name() + "," + user.passwordGuessTimestamp() + "," + user.passwordGuesses();
    	LocalDateTime currentDateTime = LocalDateTime.now();
    	final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    	
    	String formattedDateTime = currentDateTime.format(formatter);
    	System.out.println(formattedDateTime);
    	String replaceString = user.name() + "," + formattedDateTime + "," + attempts;
    	Path path = Paths.get("Login.txt");
    	Charset charset = StandardCharsets.UTF_8;

    	String content = new String(Files.readAllBytes(path), charset);
    	content = content.replaceAll(findString, replaceString);
    	Files.write(path, content.getBytes(charset));
    }
    
    @Override
    public LoginResult login(String username, String password) throws IOException{
        try {
            final List<UserRecord> users = loadUsersFromFile();
            String saltedPassword;
            if (limitUserGuesses(username)) {
	            for (UserRecord user : users) {
	            	saltedPassword = getHashPassword(user.salt(), password);
	                if (user.name().equals(username) && user.password().equals(saltedPassword)) {
	                    return LoginResult.SUCCESS;
	                }
	            }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return LoginResult.FAILURE;
    }

    private List<UserRecord> loadUsersFromFile() throws FileNotFoundException {
        File file = new File("Login.txt");
        Scanner scanner2 = new Scanner(file);
        scanner2.useDelimiter("[,\n]");
        ArrayList<UserRecord> users = new ArrayList<>();
        
        while (scanner2.hasNext()) {
            final String username = scanner2.next().trim();
            final String initalizeLoginTime = scanner2.next().trim();
            final String attempts = scanner2.next().trim();
            final String password = scanner2.next().trim();
            final String salt = scanner2.next().trim();
            users.add(new UserRecord(username, initalizeLoginTime, attempts, password, salt));
        }

        return users;
    }

    // prints file filename on the specified printer
    @Override
    public void print(String filename, String printer) {
        if (isSeverTurnedOff()) {
            return;
        }
        int printerId = findPrinter(printer);

        if (printerId != -1) {
            printers.get(printerId).fileNames.add(filename);
        }

    }

    // lists the print queue for a given printer on the user's display in lines of the form <job number>   <file name>
    @Override
    public String queue(String printer) {
        if (isSeverTurnedOff()) {
            return "";
        }

        int printerId = findPrinter(printer);
        if (printerId != -1) {
            return printers.get(printerId).listQueue();
        }
        return "";
    }

    @Override
    public void topQueue(String printer, int job) {
        if (isSeverTurnedOff()) {
            return;
        }
        int printerId = findPrinter(printer);
        if (printerId != -1) {
            printers.get(printerId).moveFirstInQueue(job);
        }
    }

    private int findPrinter(String printer) {
        for (int i = 0; i < printers.size(); i++) {
            if (printers.get(i).printerName.equals(printer)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void start() {
        printersServerRunning = true;
        System.out.println("Print server started");
    }

    @Override
    public void stop() {
        printersServerRunning = false;
        System.out.println("Print server stopped");
    }

    @Override
    public void restart() {
        printersServerRunning = true;
        for (Printer printer : printers) {
            printer.restartPrinter();
        }
    }

    private boolean isSeverTurnedOff() {
        if (!printersServerRunning) {
            System.out.println("Print server is not running");
        }
        return !printersServerRunning;
    }

    // prints status of printer on the user's display
    @Override
    public String status(String printer) {
        if (printersServerRunning) {
            return "Print server: Online";
        }
        return "Print server: Offline";

    }

    // prints the value of the parameter on the user's display
    @Override
    public void readConfig(String parameter) {
        if (isSeverTurnedOff()) {
            return;
        }

    }

    // sets the parameter to value
    @Override
    public void setConfig(String parameter, String value) {
        if (isSeverTurnedOff()) {
            return;
        }

    }

}
