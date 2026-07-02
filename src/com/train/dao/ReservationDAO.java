package com.train.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import com.train.config.DatabaseConfig;
import com.train.model.Passenger;
import com.train.model.Train;

public class ReservationDAO {
    
    public void addTrain(Train train){
        String sql = "INSERT INTO trains (train_id, train_name, source, destination, total_seats, available_seats, ticket_cost) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) 
        {
            stmt.setInt(1, train.trainId());
            stmt.setString(2, train.trainName());
            stmt.setString(3, train.source());
            stmt.setString(4, train.destination());
            stmt.setInt(5, train.total_seats());
            stmt.setInt(6, train.available_seats());
            stmt.setDouble(7, train.ticket_cost());
            
            stmt.executeUpdate();
            System.out.println("Train added successfully.");
        }
        catch (SQLException e) {
            if (e.getErrorCode() == 1062) { 
                System.out.println("Warning: Train with ID " + train.trainId() + " already exists. Skipping insertion.");
            } else {
                e.printStackTrace();
            }
        }
    }
    public int viewRemainingTicket(int trainId) {
        String sql = "SELECT available_seats FROM trains WHERE train_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("available_seats");
            } else {
                System.out.println("Train not found.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int addPassenger(Passenger passenger) {
        String sql = "INSERT INTO passengers(name, age) VALUES(?,?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) 
        {
            stmt.setString(1, passenger.name());
            stmt.setInt(2, passenger.age());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    public void viewTrains() {
        String trainSql = "SELECT * FROM trains";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(trainSql)) {

            System.out.println("--------------------------------------------------------------------------------");
            System.out.printf("%-10s %-20s %-15s %-15s %-12s %-15s %-10s%n",
                    "Train ID", "Train Name", "Source", "Destination",
                    "Total", "Available", "Cost");
            System.out.println("--------------------------------------------------------------------------------");

            while (rs.next()) {

                int trainId = rs.getInt("train_id");
                String trainName = rs.getString("train_name");
                String source = rs.getString("source");
                String destination = rs.getString("destination");
                int totalSeats = rs.getInt("total_seats");
                int availableSeats = rs.getInt("available_seats");
                double ticketCost = rs.getDouble("ticket_cost");

                System.out.printf("%-10d %-20s %-15s %-15s %-12d %-15d ₹%.2f%n",
                        trainId, trainName, source, destination,
                        totalSeats, availableSeats, ticketCost);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean cancelTrain(int trainId) {
        String sql = "DELETE FROM trains WHERE train_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, trainId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error removing train. Cascade constraint check failed.");
            return false;
        }
    }

    public void bookTicket(int trainId, int passengerId) {
        String selectSql = "SELECT available_seats FROM trains WHERE train_id = ?";
        String insertSql = "INSERT INTO bookings(train_id, passenger_id, booking_date) VALUES(?,?,?)";
        String updateSql = "UPDATE trains SET available_seats = available_seats - 1 WHERE train_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) 
        {
            selectStmt.setInt(1, trainId);
            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) { 
                    int seats = rs.getInt(1);
                    if (seats <= 0) {
                        System.out.println("Sorry!!!! You are too late, seats are not available.");
                        return;
                    }
                } else {
                    System.out.println("Booking failed: Train ID " + trainId + " does not exist.");
                    return;
                }
            }

            insertStmt.setInt(1, trainId);
            insertStmt.setInt(2, passengerId);
            insertStmt.setDate(3, Date.valueOf(LocalDate.now()));
            insertStmt.executeUpdate();
            System.out.println("Ticket is booked successfully!");

            updateStmt.setInt(1, trainId);
            updateStmt.executeUpdate();
            
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showReservations() {
        String sql = """
                SELECT b.booking_id, p.name, t.train_name, t.source, t.destination, b.booking_date
                FROM bookings b
                JOIN passengers p ON b.passenger_id = p.passenger_id
                JOIN trains t ON b.train_id = t.train_id""";
                
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) 
        {
            System.out.println("Current Reservations:");
            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                System.out.printf("BookingId: %d \nPassenger: %s \n" +
                        "Train: %s (%s to %s) \n" + "Date: %s%n\n",
                        rs.getInt("booking_id"), rs.getString("name"), rs.getString("train_name"),
                        rs.getString("source"), rs.getString("destination"), rs.getDate("booking_date").toString());
            }
            if (!hasRecords) {
                System.out.println("No reservations found.");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancelTicket(int bookingId) {
        String findTrainSql = "SELECT train_id FROM bookings WHERE booking_id = ?";
        String deleteBookingSql = "DELETE FROM bookings WHERE booking_id = ?";
        String incrementSeatSql = "UPDATE trains SET available_seats = available_seats + 1 WHERE train_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Begin Safe Transaction

            int trainId = -1;

            // 1. Locate the correct train ID from the booking record
            try (PreparedStatement findStmt = conn.prepareStatement(findTrainSql)) {
                findStmt.setInt(1, bookingId);
                try (ResultSet rs = findStmt.executeQuery()) {
                    if (rs.next()) {
                        trainId = rs.getInt("train_id");
                    } else {
                        System.out.println("Cancellation failed: Booking ID " + bookingId + " does not exist.");
                        return;
                    }
                }
            }

            // 2. Clear booking from database
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteBookingSql)) {
                deleteStmt.setInt(1, bookingId);
                deleteStmt.executeUpdate();
            }

            // 3. Return the seat capacity to the trains table
            try (PreparedStatement seatStmt = conn.prepareStatement(incrementSeatSql)) {
                seatStmt.setInt(1, trainId);
                seatStmt.executeUpdate();
            }

            conn.commit(); // Save all changes atomically
            System.out.println("Ticket with Booking ID " + bookingId + " has been successfully canceled.");

        } catch (SQLException e) {
            System.out.println("Transaction failed. Attempting database state rollback...");
            if (conn != null) {
                try {
                    conn.rollback(); // Undo everything if any query failed
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); 
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}