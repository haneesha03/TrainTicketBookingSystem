package com.train.main;

import com.train.dao.ReservationDAO;
import com.train.model.Passenger;
import com.train.model.Train;
import java.util.Scanner;

public class MainApplication {
	public static void main(String[] args) throws Exception {
		ReservationDAO dao = new ReservationDAO();
		Scanner sc = new Scanner(System.in);
		
		// Auto-initialize base train configuration on application launch
		while (true) {
			System.out.println("\n RAILWAY MANAGEMENT SYSTEM ");
			System.out.println(" a .Passenger Portal");
			System.out.println(" b .Admin Portal");
			
			char role=sc.next().charAt(0);
			sc.nextLine();
			switch(role) {
			case 'a':
				while(true) {
					System.out.println("1. passenger signup");
					System.out.println("2. Book Ticket");
					System.out.println("3. Cancel a Ticket via Booking ID");
					System.out.println("4. View trains");
					System.out.println("5. View Remaining Tickets by train id");
					System.out.println("6. Exit portal");
					int choice = sc.nextInt();
					switch(choice) {
					case 1:
						System.out.println("enter id:");
						int id=sc.nextInt();
						sc.nextLine();
						System.out.println("enter name:");
						String name=sc.nextLine();
						sc.nextLine();
						System.out.println("enter age");
						int age=sc.nextInt();
						Passenger passenger = new Passenger(id,name, age);
						dao.addPassenger(passenger);
						break;
						
					case 2:
					    System.out.println("--- Book Ticket ---");

					    System.out.print("Enter Train ID: ");
					    int train_no = sc.nextInt();
					    sc.nextLine(); 

					    System.out.print("Enter PassengerId ");
					    int p_id = sc.nextInt();

					    if (p_id!= -1) {
					        dao.bookTicket(train_no, p_id);
					    } else {
					        System.out.println("Passenger registration failed. Ticket not booked.");
					    }
					    break;
						
						
					case 3:
						System.out.print("Enter the Booking ID you want to cancel: ");
						int idToCancel = sc.nextInt();
						
						System.out.print("Are you sure you want to cancel booking #" + idToCancel + "? (yes/no): ");
						String confirmation = sc.next();
						
						if (confirmation.equalsIgnoreCase("yes")) {
							dao.cancelTicket(idToCancel);
						} else {
							System.out.println("Cancellation aborted. Ticket positions remain unchanged.");
						}
						break;
					case 4:
						System.out.println("Available trains");
						dao.viewTrains();
						break;
						
					case 5:
						System.out.println("enter train id");
						int train_id=sc.nextInt();
						System.out.println("remaining tickets in "+train_id);
						System.out.println(dao.viewRemainingTicket(train_id));
						break;
					case 6:
						System.out.println("Exiting System. Thank you!");
						sc.close();
						System.exit(0);
						
					default:
						System.out.println("Invalid choice! Please select a number between 1 and 4.");
		
					}
				}	
			case 'b':
				while(true) {
					System.out.println("1. Add train");
					System.out.println("2. View All Active Reservations");
					System.out.println("3.delete train by train id if no ticket is booked");
					System.out.println("4. Exit Application");
					int choice = sc.nextInt();
					
					switch (choice) {
					
						
						case 1:
						    System.out.println("--- Add Train ---");

						    System.out.print("Train ID: ");
						    int t_id = sc.nextInt();
						    sc.nextLine(); 

						    System.out.print("Train Name: ");
						    String train_name = sc.nextLine();

						    System.out.print("Train Source: ");
						    String source = sc.nextLine();

						    System.out.print("Train Destination: ");
						    String destination = sc.nextLine();

						    System.out.print("Total Seats: ");
						    int total_Seats = sc.nextInt();

						    System.out.print("Available Seats: ");
						    int available_seats = sc.nextInt();

						    System.out.print("Ticket Cost: ");
						    double ticket_cost = sc.nextDouble();

						    Train expressTrain = new Train(
						            t_id,
						            train_name,
						            source,
						            destination,
						            total_Seats,
						            available_seats,
						            ticket_cost
						    );

						    dao.addTrain(expressTrain);
						    break;
							
						case 2:
							System.out.println("\n--- Current Active Reservations ---");
							dao.showReservations();
							break;
							
						case 3:
							int tr_id=sc.nextInt();
							if(dao.cancelTrain(tr_id)) {
								System.out.println("train canceled");
							}
							else {
								System.out.println("can't cancel train");
							}
							break;
						case 4:
							System.out.println("Exiting System. Thank you!");
							sc.close();
							System.exit(0);
	
						default:
							System.out.println("Invalid choice! Please select a number between 1 and 4.");
					}
					
				}
				
			}
			
			
			
			System.out.println("6. Add train");
			System.out.println("7. View All Active Reservations");
			System.out.println("8.delete train by train id if no ticket is booked");
			
			
			
		}
	}
}