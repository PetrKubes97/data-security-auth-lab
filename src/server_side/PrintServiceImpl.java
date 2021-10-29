package server_side;

import server_side.data.FileDatabase;
import server_side.responses.CommandFailure;
import server_side.responses.CommandResponse;
import server_side.responses.CommandSuccess;
import server_side.responses.LoginResponse;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class PrintServiceImpl extends UnicastRemoteObject implements PrintService {

    private ArrayList<Printer> printers;
    private boolean printersServerRunning = true;
    private final PrinterAuthenticator authenticator;

    public PrintServiceImpl() throws RemoteException {
        super();
        createPrinters();
        FileDatabase database = new FileDatabase("users.txt");
        Logger logger = new Logger("log.txt");
        authenticator = new PrinterAuthenticator(database, logger);
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
    public LoginResponse login(String username, String password) throws IOException {
        return authenticator.login(username, password);
    }

    // prints file filename on the specified printer
    @Override
    public CommandResponse<Void> print(String filename, String printer, String accessToken) {
        return authenticator.authenticated(accessToken,"print",  () -> {
            if (isSeverTurnedOff()) {
                return new CommandFailure<>("Server is turned off");
            }
            int printerId = findPrinter(printer);

            if (printerId != -1) {
                printers.get(printerId).fileNames.add(filename);
            }

            return new CommandSuccess<>(null);
        });
    }

    // lists the print queue for a given printer on the user's display in lines of the form <job number>   <file name>
    @Override
    public CommandResponse<String> queue(String printer, String accessToken) {
        return authenticator.authenticated(accessToken, "queue", () -> {
            if (isSeverTurnedOff()) {
                return new CommandFailure<>("Server is turned off");
            }

            int printerId = findPrinter(printer);
            if (printerId != -1) {
                return new CommandSuccess<>(printers.get(printerId).listQueue());
            }
            return new CommandFailure<>("Printer does not exist");
        });
    }

    @Override
    public CommandResponse<Void> topQueue(String printer, int job, String accessToken) {
        return authenticator.authenticated(accessToken, "topQueue", () -> {
            if (isSeverTurnedOff()) {
                return new CommandFailure<>("Server is turned off");
            }

            int printerId = findPrinter(printer);
            if (printerId != -1) {
                printers.get(printerId).moveFirstInQueue(job);
            }

            return new CommandSuccess<>(null);
        });
    }

    @Override
    public CommandResponse<Void> start(String accessToken) throws RemoteException {
        return authenticator.authenticated(accessToken, "start", () -> {
            printersServerRunning = true;
            return new CommandSuccess<>(null);
        });
    }

    @Override
    public CommandResponse<Void> stop(String accessToken) throws RemoteException {
        return authenticator.authenticated(accessToken, "stop", () -> {
            printersServerRunning = false;
            return new CommandSuccess<>(null);
        });
    }

    @Override
    public CommandResponse<Void> restart(String accessToken) throws RemoteException {
        return authenticator.authenticated(accessToken, "restart", () ->  new CommandSuccess<>(null));
    }

    @Override
    public CommandResponse<String> status(String printer, String accessToken) throws RemoteException {
        return authenticator.authenticated(accessToken, "status", () ->  new CommandSuccess<>(null));
    }

    @Override
    public CommandResponse<String> readConfig(String parameter, String accessToken) throws RemoteException {
        return authenticator.authenticated(accessToken, "readConfig", () -> new CommandSuccess<>(null));
    }

    @Override
    public CommandResponse<String> setConfig(String parameter, String value, String accessToken) throws RemoteException {
        return authenticator.authenticated(accessToken, "setConfig", () -> new CommandSuccess<>(null));
    }

    private int findPrinter(String printer) {
        for (int i = 0; i < printers.size(); i++) {
            if (printers.get(i).printerName.equals(printer)) {
                return i;
            }
        }
        return -1;
    }


    private boolean isSeverTurnedOff() {
        if (!printersServerRunning) {
            System.out.println("Print server is not running");
        }
        return !printersServerRunning;
    }
}
