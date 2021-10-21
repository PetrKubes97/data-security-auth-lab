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

    public static void commandPrompt() {
        String command = "";
        do {
            System.out.print("Enter command: ");
            command = scanner.nextLine();
            processCommand(command.toLowerCase());
        } while (!command.equals("q"));// && command != "Q" && command != "quit");

    }

    public static void processCommand(String command) {
        switch (command) {
            case "print": {
                final String filename = genericPrompt("Enter filename:");
                final String printer = printerNamePrompt();
                try {
                    server.print(filename, printer);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            }
            case "queue": {
                final String printer = printerNamePrompt();
                try {
                    server.queue(printer);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return;
            }
            case "top queue":
            case "topqueue": {
                final String printer = printerNamePrompt();
                final String jobId = genericPrompt("Enter job id:");
                try {
                    server.topQueue(printer, Integer.parseInt(jobId));
                } catch (NumberFormatException | RemoteException e) {
                    e.printStackTrace();
                }

                return;
            }
            case "start":
                try {
                    server.start();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            case "stop":
                try {
                    server.stop();
                    return;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            case "restart":
                try {
                    server.restart();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                return;
            case "status":
                final String printer = printerNamePrompt();
                try {
                    server.status(printer);
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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

    private static String genericPrompt(String text) {
        System.out.print(text);
        return scanner.nextLine();
    }

    private static String printerNamePrompt() {
        return genericPrompt("Enter printer name:");
    }
}
