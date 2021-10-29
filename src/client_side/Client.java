package client_side;

import server_side.PrintService;
import server_side.responses.*;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {

    private static PrintService server;
    private static Scanner scanner;
    private static String accessToken;
    private static boolean quit = false;

    public static void main(String[] args) throws IOException, NotBoundException {
        server = (PrintService) Naming.lookup("rmi://127.0.0.1:5099/printer");

        scanner = new Scanner(System.in);

        while (!quit) {
            if (accessToken == null) {
                accessToken = loginPrompt();
            } else {
                commandPrompt(accessToken);
            }
        }
    }

    public static String loginPrompt() throws IOException {
        System.out.print("Enter username:");
        final String username = scanner.nextLine();
        System.out.print("Enter password:");
        final String password = scanner.nextLine();

        final LoginResponse result = server.login(username, password);
        if (result instanceof LoginSuccess) {
            return ((LoginSuccess) result).accessToken();
        } else if (result instanceof LoginFailure) {
            System.out.println(((LoginFailure) result).reason());
        } else {
            System.out.println("Unrecognized login response");
        }
        return null;
    }

    public static void commandPrompt(String accessToken) throws RemoteException {
        System.out.print("Enter command: ");
        String command = scanner.nextLine();
        processCommand(command.toLowerCase(), accessToken);
    }

    public static void processCommand(String command, String accessToken) throws RemoteException {
        switch (command) {
            case "print": {
                final String filename = genericPrompt("Enter filename:");
                final String printer = printerNamePrompt();
                checkServerResponse(() -> server.print(filename, printer, accessToken));
                return;
            }
            case "queue": {
                final String printer = printerNamePrompt();
                checkServerResponse(() -> server.queue(printer, accessToken));
                return;
            }
            case "top queue":
            case "topqueue": {
                final String printer = printerNamePrompt();
                final String jobId = genericPrompt("Enter job id:");
                checkServerResponse(() -> server.topQueue(printer, Integer.parseInt(jobId), accessToken));
                return;
            }
            case "start":
                checkServerResponse(() -> server.start(accessToken));
                return;
            case "stop":
                checkServerResponse(() -> server.stop(accessToken));
                return;
            case "restart":
                checkServerResponse(() -> server.restart(accessToken));
                return;
            case "status":
                final String printer = printerNamePrompt();
                checkServerResponse(() -> server.status(printer, accessToken));
                return;
            case "read config":
            case "readconfig":
                //server.readConfig(scanner);
                return;
            case "set config":
            case "setconfig":
                //server.setConfig(scanner, config);
                return;
            case "q":
                quit = true;
                return;
        }

        System.out.println("Command not found");
    }


    public interface CommandResponseFunction<T> {
        CommandResponse<T> call() throws RemoteException;
    }

    private static <T> void checkServerResponse(CommandResponseFunction<T> function) throws RemoteException {
        final CommandResponse<T> response = function.call();
        if (response instanceof CommandFailure) {
            System.out.println("Server error: " + ((CommandFailure<T>) response).getError());
            if (((CommandFailure<T>) response).getLogout()) {
                accessToken = null;
            }
        } else if (response != null) {
            System.out.println(response.getResponse());
        }
    }

    private static String genericPrompt(String text) {
        System.out.print(text);
        return scanner.nextLine();
    }

    private static String printerNamePrompt() {
        return genericPrompt("Enter printer name:");
    }
}
