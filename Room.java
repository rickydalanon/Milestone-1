package boutiquecapsulehotelsystem;

import java.sql.*;
import java.util.Scanner;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class Room {

    public static void browseRooms() {
        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {}

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM rooms ORDER BY room_id ASC")) {

 
            System.out.println("\n+---------+-------------------+-----------------------+");
            System.out.println("| Room ID | Category          | Price                 |");
            System.out.println("+---------+-------------------+-----------------------+");

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                
                int id = rs.getInt("room_id");
                String cat = rs.getString("category");
                int price = rs.getInt("price");

             
                String priceString = "\u20b1 " + price;

                System.out.printf("| %-7d | %-17s | %-21s |\n", id, cat, priceString);
            }

            if (!hasData) {
                System.out.println("| No rooms available.                     |");
            }
            System.out.println("+---------+-------------------+-----------------------+");

            System.out.println("\nEnter Room ID to view details | 0 to go back");
            Scanner sc = new Scanner(System.in);
            if (sc.hasNextInt()) {
                int inputId = sc.nextInt();
                if (inputId != 0) viewRoomDetailsByID(inputId);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewRoomDetailsByID(int id) {
        try (Connection conn = DBConnection.connect();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM rooms WHERE room_id=?")) {
            
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- ROOM " + id + " DETAILS ---");
                System.out.println("Category:    " + rs.getString("category"));
                System.out.println("Price:       \u20b1 " + rs.getInt("price") + " per night");
                System.out.println("Description: " + rs.getString("description"));
                
                System.out.println("\nPress Enter to return...");
                new Scanner(System.in).nextLine(); 
                new Scanner(System.in).nextLine(); 
            } else {
                System.out.println("Room not found.");
            }
        } catch (Exception e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    public static void searchRooms() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter keyword: ");
        String key = sc.nextLine();

        try (Connection conn = DBConnection.connect();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM rooms WHERE category LIKE ? OR description LIKE ?")) {
            
            pst.setString(1, "%" + key + "%");
            pst.setString(2, "%" + key + "%");
            ResultSet rs = pst.executeQuery();

            System.out.println("\n--- SEARCH RESULTS ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("[%d] %s - \u20b1 %d\n", 
                        rs.getInt("room_id"), rs.getString("category"), rs.getInt("price"));
            }

            if (!found) {
                System.out.println("No matching rooms found for: " + key);
            } else {
                System.out.print("\nEnter Room ID to view details or 0 to go back: ");
                if (sc.hasNextInt()) {
                    int id = sc.nextInt();
                    if (id != 0) viewRoomDetailsByID(id);
                }
            }
        } catch (Exception e) {
            System.out.println("Search error: " + e.getMessage());
        }
    }
}