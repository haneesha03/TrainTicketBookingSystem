package com.train.main;
import com.train.dao.ReservationDAO;
import com.train.model.Passenger;
import com.train.model.Train;
public class MainApplication {
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub 
		ReservationDAO dao=new ReservationDAO();
		Train expressTrain=new Train(103,"vande bharath","delhi","Chennai",200,200,500);
		dao.addTrain(expressTrain);
		Passenger passenger=new Passenger(3,"sunny",22);
		int passengerId=dao.addPassenger(passenger);
		if(passengerId!=-1) {
			dao.bookTicket(102,passengerId);
		}
		dao.showReservations();
	}

}
