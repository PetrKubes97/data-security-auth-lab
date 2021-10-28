package server_side;

import server_side.data.FileDatabase;
import server_side.data.UserRecord;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static server_side.crypto.Crypto.createPasswordHash;


public class PrintServiceImpl extends UnicastRemoteObject implements PrintService {

    private ArrayList<Printer> printers;
    private boolean printersServerRunning = true;
    private final FileDatabase database;

    public PrintServiceImpl() throws RemoteException, FileNotFoundException {
        super();
        createPrinters();
        database = new FileDatabase("users.txt");
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

    private Boolean limitUserGuesses(String username) throws IOException {
        final UserRecord user = database.loadUserByUsername(username);

        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime minusFiveMin = currentDateTime.minusMinutes(5);

        //Less than 3 tries and less than 5 minutes from last try
        if (user.passwordGuesses() < 3 && user.lastLoginAttemptAt().isAfter(minusFiveMin)) {
            database.updateLoginInfo(user, user.passwordGuesses() + 1, currentDateTime);
            return true;
        }
        //More than 5 minutes from last try
        else if (user.lastLoginAttemptAt().isBefore(minusFiveMin)) {
            database.updateLoginInfo(user, 0, currentDateTime);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public LoginResult login(String username, String password) throws IOException {
        final UserRecord user = database.loadUserByUsername(username);

        if (limitUserGuesses(username)) {
            final String saltedHashedPassword = createPasswordHash(user.salt(), password);
            if (user.hashedPassword().equals(saltedHashedPassword)) {
                return LoginResult.SUCCESS;
            }
        } else {
            return LoginResult.TOO_MANY_ATTEMPTS;
        }

        return LoginResult.FAILURE;
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
