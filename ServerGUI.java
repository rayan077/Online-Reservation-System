
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 *
 * @author rayan
 */
public class ServerGUI {
    private static final int PORT = 5000;
    static Map<String, String> users = new HashMap<>();
    static Map<String, String> reservations = new HashMap<>();

    public static void main(String[] args) {
        ServerSocket WelcomeSocket = null;

        try {
            WelcomeSocket = new ServerSocket(PORT);
            System.out.println("Reservation Server started on port " + PORT);

            while (true) {
                Socket clientSocket = WelcomeSocket.accept();
                System.out.println("New client connected.");
                Thread clientThread = new Thread(new ClientWorkGui(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (WelcomeSocket != null) {
                try { WelcomeSocket.close(); }
                catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    public static synchronized boolean reserveSlot(String slot, String user) {
        if (!reservations.containsKey(slot)) {
            reservations.put(slot, user);
            return true;
        }
        return false;
    }

    public static synchronized void cancelReservation(String slot) {
        reservations.remove(slot);
    }
}

class ClientWorkGui implements Runnable {

    private Socket clientSocket;
    private BufferedReader in_fromClient;
    private PrintWriter out_toClient;
    private String currentUser;

    public ClientWorkGui(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        in_fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out_toClient = new PrintWriter(clientSocket.getOutputStream(), true);
    }

 @Override
    public void run() {
        try {
            out_toClient.println("Welcome to Online Reservation System!");
            while (true) {
                String choice = in_fromClient.readLine();
                if (choice == null) break;
                switch (choice) {
                    case "1": Register(); break;
                    case "2": login(); break;
                    case "3": makeReservation(); break;
                    case "4": cancelReservation(); break;
                    case "5": viewProfile(); break;
                    case "6":
                        out_toClient.println("Goodbye! You have exited the system.");
                        System.out.println("Client " + clientSocket.getInetAddress() + " disconnected by choosing Exit.");
                        clientSocket.close();
                        return;
                    default: out_toClient.println("Invalid choice.");
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected unexpectedly.");
        }
    }

    private void Register() throws IOException {
        String username = in_fromClient.readLine();
        String password = in_fromClient.readLine();

        synchronized (Server.users) {
            if (Server.users.containsKey(username)) {     
               out_toClient.println("User Already exist!.");
                }
             else {
                Server.users.put(username, password);
                currentUser = username;
                out_toClient.println("New user registered and connected successfully! Welcome, " + username);
                System.out.println("new user registered "+username);
            }
        }
    }

    private void login() throws IOException {
        String username = in_fromClient.readLine();
        String password = in_fromClient.readLine();

        synchronized (Server.users) {
            if (Server.users.containsKey(username) && Server.users.get(username).equals(password)) {
                currentUser = username;
                out_toClient.println("Login successful! Welcome back, " + username);
                System.out.println("user login "+username);
            } else {
                if (Server.users.containsKey(username)==false) {
                    out_toClient.println("Incorrect username.");
                } else {
                out_toClient.println("Incorrect password.");
                }
            }
        }
    }

    private void makeReservation() throws IOException {
        if (currentUser == null) {
            out_toClient.println("Login first!");
            return;
        }
        String slot = in_fromClient.readLine();
        slot = slot.toUpperCase();
        
        if (slot.equals("NULL") || slot.isBlank()){
            out_toClient.println("Cannot reserve null");
            return;
        }
        out_toClient.println(slot);
        
        if (Server.reserveSlot(slot, currentUser)) {
            out_toClient.println("Reservation confirmed for " + slot);
            System.out.println("user "+currentUser+" reserved "+slot);
        } else {
            out_toClient.println("Slot unavailable.");
        }
    }
    private void cancelReservation() throws IOException {
        if (currentUser == null) {
            out_toClient.println("Login first!");
            return;
        }

        List<String> userReservations = new ArrayList<>();
        synchronized (Server.reservations) {
            Server.reservations.forEach((slot, user) -> {
                if (user.equals(currentUser))
                    userReservations.add(slot);
            });
        }

        if (userReservations.isEmpty()) {
            out_toClient.println("You have no reservations to cancel.");
            return;
        }

        out_toClient.println("Your reservations:");
        for (int i = 0; i < userReservations.size(); i++) {
            out_toClient.println((i+1) + ". " + userReservations.get(i));
        }

        String choice = in_fromClient.readLine();
        choice = choice.strip();
        try {
            int index = Integer.parseInt(choice) - 1;
            if (index >= 0 && index < userReservations.size()) {
                String slot = userReservations.get(index);
                Server.cancelReservation(slot);
                out_toClient.println("Reservation cancelled: " + slot);
                System.out.println("user "+currentUser+" cancelled "+slot);
            } else {
                out_toClient.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            out_toClient.println("Invalid input.");
        }
    }

    private void viewProfile() {
        if (currentUser == null) {
            out_toClient.println("Login first!");
            return;
        }
        out_toClient.println("User: " + currentUser);
        synchronized (Server.reservations) {
            Server.reservations.forEach((slot, user) -> {
                if (user.equals(currentUser))
                    out_toClient.println(slot);
            });
        }
    }
}
