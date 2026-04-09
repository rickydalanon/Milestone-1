package boutiquecapsulehotelsystem;

import java.util.Scanner;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class MainMenu {
    public static void main(String[] args) {

        try {
            System.setProperty("file.encoding", "UTF-8");
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding error: " + e.getMessage());
        }

        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n+-------------------------------------------------------------+");
            System.out.println("|         BOUTIQUE CAPSULE HOTEL SYSTEM                       |");
            System.out.println("+-------------------------------------------------------------+");
            System.out.println("| 1. Browse Rooms               5. Check-Out                  |");
            System.out.println("| 2. Search Rooms               6. Cancel Reservation         |");
            System.out.println("| 3. Book Room                  7. Change Reservation Date    |");
            System.out.println("| 4. View Reservations          8. Exit                       |");
            System.out.println("+-------------------------------------------------------------+");

            System.out.print("Enter option: ");
            
            if (!sc.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number (1-8).");
                sc.next(); 
                continue;
            }
            
            int choice = sc.nextInt();

            switch (choice) {
                case 1: 
                    Room.browseRooms(); 
                    break;
                case 2: 
                    Room.searchRooms(); 
                    break;
                case 3: 
                    Reservation.bookRoom(); 
                    break;
                case 4: 
                    Reservation.viewReservations(); 
                    break;
                case 5: 
                    Reservation.checkOut(); 
                    break;
                case 6: 
                    Reservation.cancelReservation(); 
                    break;
                case 7: 
                    Reservation.changeReservation(); 
                    break;
                case 8: 
                    System.out.println("Exiting system... Goodbye!");
                    System.exit(0);
                default: 
                    System.out.println("Invalid choice. Please select 1-8.");
            }
        }
    }
}