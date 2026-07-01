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
		String sql="Insert INTO trains VALUES(?,?,?,?,?,?,?)";
		try(Connection conn=DatabaseConfig.getConnection();
			PreparedStatement stmt=conn.prepareStatement(sql))
		{
			stmt.setInt(1,train.trainId());
			stmt.setString(2,train.trainName());
			stmt.setString(3, train.source());
			stmt.setString(4, train.destination());
			stmt.setInt(5,train.total_seats());
			stmt.setInt(6,train.available_seats());
			stmt.setDouble(7,train.ticket_cost());
			
			stmt.executeUpdate();
			System.out.println("Train added successfully.");
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public int addPassenger(Passenger passenger) {
		String sql="Insert into passengers(name,age) values(?,?)";
		try(Connection conn=DatabaseConfig.getConnection();
			PreparedStatement stmt=conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS))
		{
			stmt.setString(1,passenger.name());
			stmt.setInt(2, passenger.age());
			stmt.executeUpdate();
			try(ResultSet generatedKeys=stmt.getGeneratedKeys()){
				if(generatedKeys.next()) {
					return generatedKeys.getInt(1);
				}
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public void bookTicket(int trainId,int passengerId) {
		String sql="insert into bookings(train_id,passenger_id,booking_date)values(?,?,?)";
		try(Connection conn=DatabaseConfig.getConnection();
			PreparedStatement stmt=conn.prepareStatement(sql))
		{
			String sq="select available_seats from trains where train_id="+trainId;
			Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(sq);
			rs.next();
			int seats=rs.getInt(1);
			if(seats<=0) {
				System.out.println("Sorry!!!! you are too late seats are not available");
				return;
			}
			stmt.setInt(1, trainId);
			stmt.setInt(2, passengerId);
			stmt.setDate(3, Date.valueOf(LocalDate.now()));
			stmt.executeUpdate();
			System.out.println("ticket is booked successfully");
			String update="update trains set available_seats=available_seats-1 where train_id="+trainId;
			st.executeUpdate(update);
			
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public void showReservations() {
		String sql="""
				select b.booking_id,p.name,t.train_name,t.source,t.destination,b.booking_date
				From bookings b
				join passengers p on b.passenger_id=p.passenger_id
				join trains t on b.train_id=t.train_id""";
		try(Connection conn=DatabaseConfig.getConnection();
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(sql))
		{
			
			System.out.println("current reservations");
			while(rs.next()) {
				System.out.printf("BookingId: %d \npassenger: %s \n"+
						"Train: %s (%s to %s) \n"+"Date:%s%n",
						rs.getInt("booking_id"),rs.getString("name"),rs.getString("train_name"),
						rs.getString("source"),rs.getString("destination"),rs.getDate("booking_date").toString());
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
			
		}
	}
}
