package boutiquecapsulehotelsystem;

import java.sql.*;
import java.util.Scanner;

public class Reservation {

    public static void bookRoom() {
        Scanner sc = new Scanner(System.in);
        try (Connection conn = DBConnection.connect()) {
            System.out.println("\n--- BOOK ROOM ---");
            
            System.out.print("Enter Room ID: ");
            int roomId = sc.nextInt(); 
            sc.nextLine(); 

            System.out.print("Guest Name: ");
            String name = sc.nextLine();
            
            System.out.print("Contact No: ");
            String contact = sc.nextLine();
            
            System.out.print("Check-in Date (YYYY-MM-DD): ");
            String checkIn = sc.nextLine();
            
            System.out.print("Check-out Date (YYYY-MM-DD): ");
            String checkOut = sc.nextLine();

            System.out.print("\nConfirm Booking? (1-Yes / 2-Cancel): ");
            if (!sc.hasNextInt()) { sc.next(); return; }
            int confirmChoice = sc.nextInt();

            if (confirmChoice == 1) {
                String sql = "INSERT INTO reservations (room_id, guest_name, contact_no, check_in, check_out) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, roomId);
                pst.setString(2, name);
                pst.setString(3, contact);
                pst.setString(4, checkIn);
                pst.setString(5, checkOut);

                pst.executeUpdate();
                System.out.println("\nSUCCESS: Booking confirmed for " + name + "!");
            } else {
                System.out.println("\nBooking cancelled.");
            }
            
        } catch (SQLException e) {
            System.out.println("\nERROR: Could not book room. Ensure Room ID exists.");
        }
    }

    public static void viewReservations() {
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM reservations")) {

            System.out.println("\nYOUR RESERVATIONS:");
            System.out.println("+--------+---------+--------------------+------------+------------+");
            System.out.println("| Res ID | Room ID | Guest Name         | Check-in   | Check-out  |");
            System.out.println("+--------+---------+--------------------+------------+------------+");
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.printf("| %-6d | %-7d | %-18s | %-10s | %-10s |\n",
                    rs.getInt("res_id"), rs.getInt("room_id"), rs.getString("guest_name"),
                    rs.getString("check_in"), rs.getString("check_out"));
            }
            
            if (!hasData) {
                System.out.println("|               No active reservations found in system.           |");
            }
            System.out.println("+--------+---------+--------------------+------------+------------+");
            
            System.out.println("\nPress Enter to return...");
            new Scanner(System.in).nextLine();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void checkOut() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Reservation ID to Check-Out: ");
        if (!sc.hasNextInt()) { sc.next(); return; }
        int resId = sc.nextInt();

        String sql = "SELECT r.guest_name, r.check_in, r.check_out, rm.room_id, rm.category, rm.price, " +
                     "DATEDIFF(r.check_out, r.check_in) as nights " +
                     "FROM reservations r JOIN rooms rm ON r.room_id = rm.room_id WHERE r.res_id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, resId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int nights = rs.getInt("nights");
                if (nights <= 0) nights = 1; 
                int total = nights * rs.getInt("price");
                
                System.out.println("\n--- FINAL BILLING ---");
                System.out.println("Guest:       " + rs.getString("guest_name"));
                System.out.println("Room ID:     " + rs.getInt("room_id"));
                System.out.println("Category:    " + rs.getString("category"));
                System.out.println("Check-In:    " + rs.getString("check_in"));
                System.out.println("Check-Out:   " + rs.getString("check_out"));
                System.out.println("Stay:        " + nights + " night(s)");
                System.out.println("Total Price: \u20b1 " + total);

                System.out.print("\nConfirm Check-Out? (1-Yes / 2-No): ");
                if (sc.nextInt() == 1) {
                    PreparedStatement del = conn.prepareStatement("DELETE FROM reservations WHERE res_id = ?");
                    del.setInt(1, resId);
                    del.executeUpdate();
                    System.out.println("Payment successful! Reservation cleared.");
                }
            } else {
                System.out.println("Reservation ID not found.");
            }
        } catch (Exception e) { System.out.println("Error: " + e.getMessage()); }
    }

    public static void cancelReservation() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Reservation ID to Cancel: ");
        if (!sc.hasNextInt()) { sc.next(); return; }
        int id = sc.nextInt();

        try (Connection conn = DBConnection.connect();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM reservations WHERE res_id = ?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- CANCEL RESERVATION ---");
                System.out.println("Room ID:     " + rs.getInt("room_id"));
                System.out.println("Guest Name:  " + rs.getString("guest_name"));
                System.out.println("Check-in:    " + rs.getString("check_in"));
                System.out.println("Check-out:   " + rs.getString("check_out"));

                System.out.print("\nConfirm Cancel? (1-Yes / 2-Back): ");
                if (sc.nextInt() == 1) {
                    PreparedStatement del = conn.prepareStatement("DELETE FROM reservations WHERE res_id = ?");
                    del.setInt(1, id);
                    del.executeUpdate();
                    System.out.println("Reservation cancelled successfully.");
                }
            } else {
                System.out.println("Reservation ID not found.");
            }
        } catch (Exception e) { System.out.println(e); }
    }

    public static void changeReservation() {
        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter Reservation ID: ");
        if (!sc.hasNextInt()) { sc.next(); return; }
        int id = sc.nextInt(); sc.nextLine(); 

        try (Connection conn = DBConnection.connect();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM reservations WHERE res_id = ?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- CURRENT DETAILS ---");
                System.out.println("Room ID:     " + rs.getInt("room_id"));
                System.out.println("Check-in:    " + rs.getString("check_in"));
                System.out.println("Check-out:   " + rs.getString("check_out"));

                System.out.print("\nNew Check-in Date (YYYY-MM-DD):  ");
                String in = sc.nextLine();
                System.out.print("New Check-out Date (YYYY-MM-DD): ");
                String out = sc.nextLine();

                System.out.print("\nConfirm Change? (1-Yes / 2-Cancel): ");
                if (sc.nextInt() == 1) {
                    PreparedStatement up = conn.prepareStatement("UPDATE reservations SET check_in=?, check_out=? WHERE res_id=?");
                    up.setString(1, in);
                    up.setString(2, out);
                    up.setInt(3, id);
                    up.executeUpdate();
                    System.out.println("Dates updated successfully!");
                }
            } else {
                System.out.println("Reservation ID not found.");
            }
        } catch (Exception e) { System.out.println(e); }
    }
}