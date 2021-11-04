package server_side;

import server_side.database.pojo.AccessRight;
import server_side.middlewares.RequestMiddleware;
import server_side.responses.CommandFailure;
import server_side.responses.CommandResponse;
import server_side.responses.CommandSuccess;
import server_side.responses.LoginResponse;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;


public class PrintServiceImpl extends UnicastRemoteObject implements PrintService {

    private ArrayList<Printer> printers;
    private boolean printersServerRunning = true;
    private final RequestMiddleware middleware;

    public PrintServiceImpl() throws RemoteException {
        super();
        createPrinters();
        middleware = new RequestMiddleware();
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
    public LoginResponse login(String username, String password) {
        return middleware.authenticator.login(username, password);
    }

    // prints file filename on the specified printer
    @Override
    public CommandResponse<Void> print(String filename, String printer, String accessToken) {
        return middleware.handle(accessToken, AccessRight.PRINT,  () -> {
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
        return middleware.handle(accessToken, AccessRight.QUEUE, () -> {
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
        return middleware.handle(accessToken, AccessRight.TOP_QUEUE, () -> {
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
        return middleware.handle(accessToken, AccessRight.START, () -> {
            printersServerRunning = true;
            return new CommandSuccess<>(null);
        });
    }

    @Override
    public CommandResponse<Void> stop(String accessToken) throws RemoteException {
        return middleware.handle(accessToken, AccessRight.STOP, () -> {
            printersServerRunning = false;
            return new CommandSuccess<>(null);
        });
    }

    @Override
    public CommandResponse<Void> restart(String accessToken) throws RemoteException {
        return middleware.handle(accessToken, AccessRight.RESTART, () ->  new CommandSuccess<>(null));
    }

    @Override
    public CommandResponse<String> status(String printer, String accessToken) throws RemoteException {
        return middleware.handle(accessToken, AccessRight.STATUS, () ->  new CommandSuccess<>(null));
    }

    @Override
    public CommandResponse<String> readConfig(String parameter, String accessToken) throws RemoteException {
        return middleware.handle(accessToken, AccessRight.READ_CONFIG, () -> new CommandSuccess<>(null));
    }

    @Override
    public CommandResponse<String> setConfig(String parameter, String value, String accessToken) throws RemoteException {
        return middleware.handle(accessToken, AccessRight.SET_CONFIG, () -> new CommandSuccess<>(null));
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
