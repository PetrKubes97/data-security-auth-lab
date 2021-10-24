package client_side;

import server_side.PrintService;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {

    private static PrintService server;
    private static Scanner scanner;

    public static void main(String[] args) {
        try {
            server = (PrintService) Naming.lookup("rmi://127.0.0.1:5099/printer");
            System.out.println("----- " + server.echo("asdf"));
            loginPrompt();
            commandPrompt();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void loginPrompt() throws RemoteException {
        String username = "", password = "";
        scanner = new Scanner(System.in);

        do {
            System.out.print("Enter username:");
            username = scanner.nextLine();

            System.out.print("Enter password:");
            password = scanner.nextLine();
        } while (server.login(username, password) != PrintService.LoginResult.SUCCESS);

    }

    public static void commandPrompt() throws RemoteException {
        String command = "";
        do {
            System.out.print("Enter command: ");
            command = scanner.nextLine();
            processCommand(command.toLowerCase());
        } while (!command.equals("q"));// && command != "Q" && command != "quit");

    }

    public static void processCommand(String command) throws RemoteException {
        switch (command) {
            case "print": {
                final String filename = genericPrompt("Enter filename:");
                final String printer = printerNamePrompt();
                server.print(filename, printer);
                return;
            }
            case "queue": {
                final String printer = printerNamePrompt();
                queue(printer);
                return;
            }
            case "top queue":
            case "topqueue": {
                final String printer = printerNamePrompt();
                final String jobId = genericPrompt("Enter job id:");
                server.topQueue(printer, Integer.parseInt(jobId));
                return;
            }
            case "start":
                server.start();
                return;
            case "stop":
                server.stop();
                return;
            case "restart":
                server.restart();
                return;
            case "status":
                final String printer = printerNamePrompt();
                System.out.println(server.status(printer));
                return;
            case "read config":
            case "readconfig":
                //server.readConfig(scanner);
                return;
            case "set config":
            case "setconfig":
                //server.setConfig(scanner, config);
                return;
        }

        System.out.println("Command not found");
    }
    private static void queue(String printer) throws RemoteException {
    	System.out.println(server.queue(printer));
    }

    private static String genericPrompt(String text) {
        System.out.print(text);
        return scanner.nextLine();
    }

    private static String printerNamePrompt() {
        return genericPrompt("Enter printer name:");
    }
}
