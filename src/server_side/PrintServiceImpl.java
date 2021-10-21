package server_side;

import server_side.data.UserRecord;

import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PrintServiceImpl extends UnicastRemoteObject implements PrintService {

    private ArrayList<Printer> printers;

    private boolean printersServerRunning = true;

    public PrintServiceImpl() throws RemoteException {
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

    @Override
    public LoginResult login(String username, String password) {
        try {
            final List<UserRecord> users = loadUsersFromFile();
            for (UserRecord user : users) {
                if (user.name().equals(username) && user.password().equals(password)) {
                    return LoginResult.SUCCESS;
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
            final String password = scanner2.next().trim();
            users.add(new UserRecord(username, password));
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
    public void queue(String printer) {
        if (isSeverTurnedOff()) {
            return;
        }

        int printerId = findPrinter(printer);
        if (printerId != -1) {
            printers.get(printerId).listQueue();
        }
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
    public void status(String printer) {
        if (isSeverTurnedOff()) {
            return;
        }

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
