import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ConsoleClient {

    private static final int SERVER_PORT = 5000;
    

    public static void main(String[] args) {

        Socket socket = null;
        BufferedReader in_fromServer = null;
        PrintWriter out_toServer = null;
        Scanner console = new Scanner(System.in);
        

        try {
            System.out.print("Enter Server IP: ");
            String SERVER_ADDRESS = console.nextLine().trim();
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            in_fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out_toServer = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to Reservation Server.");

            // Listener thread reads server responses
            Thread listener = new Thread(new ServerListener(in_fromServer));
            listener.start();

            while (true) {
                String choice = console.nextLine().trim();

                switch (choice) {
                    case "1": // Register
                        System.out.print("Enter username: ");
                        String regUser = console.nextLine().trim();
                        System.out.print("Enter password: ");
                        String regPass = console.nextLine().trim();
                        out_toServer.println("1");
                        out_toServer.println(regUser);
                        out_toServer.println(regPass);
                        break;

                    case "2": // Login
                        System.out.print("Enter username: ");
                        String logUser = console.nextLine().trim();
                        System.out.print("Enter password: ");
                        String logPass = console.nextLine().trim();
                        out_toServer.println("2");
                        out_toServer.println(logUser);
                        out_toServer.println(logPass);
                        break;

                    case "3": // Reserve
                        System.out.print("Enter slot (e.g., Room1-Monday-10AM): ");
                        String slot = console.nextLine().trim();
                        out_toServer.println("3");
                        out_toServer.println(slot);
                        break;

                    case "4": // Cancel
                        out_toServer.println("4");
                        Thread.sleep(500); // wait for server to print reservation list
                        System.out.print("Enter reservation number to cancel: ");
                        String cancelChoice = console.nextLine().trim();
                        out_toServer.println(cancelChoice);
                        break;

                    case "5": // View Profile
                        out_toServer.println("5");
                        break;

                    case "6": // Exit
                        out_toServer.println("6");
                        System.out.println("You have exited the system.");
                        socket.close();
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try { if (socket != null) socket.close(); } catch (IOException e) { e.printStackTrace(); }
            if (console != null) console.close();
        }
    }
}

class ServerListener implements Runnable {
    private BufferedReader in_fromServer;

    public ServerListener(BufferedReader in_fromServer) {
        this.in_fromServer = in_fromServer;
    }

    @Override
    public void run() {
        try {
            String serverMsg;
            while ((serverMsg = in_fromServer.readLine()) != null) {
                System.out.println("[Server]: " + serverMsg);
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server.");
        }
    }
}
