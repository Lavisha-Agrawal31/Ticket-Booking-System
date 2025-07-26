package IRCTC.ticket_booking_system;

import IRCTC.ticket_booking_system.entities.Train;
import IRCTC.ticket_booking_system.entities.User;
import IRCTC.ticket_booking_system.services.TrainService;
import IRCTC.ticket_booking_system.services.UserBookingService;
import IRCTC.ticket_booking_system.util.UserServiceUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.*;

@SpringBootApplication
public class TicketBookingSystemApplication {
	public static void main(String[] args){
		SpringApplication.run(TicketBookingSystemApplication.class, args);
		System.out.println("Running Ticket Booking System");
		Scanner sc = new Scanner(System.in);

		int option = 0;
		UserBookingService userBookingService;

		User loggedInUser = null;

		try {
			userBookingService = new UserBookingService();
		} catch(IOException ex) {
			System.out.println("There is something wrong: " + ex.getMessage());
			ex.printStackTrace();
			return;
		}


		while(option != 7){
			System.out.println("\nchoose option");
			System.out.println("1. Sign up");
			System.out.println("2. Login");
			System.out.println("3. Fetch Bookings");
			System.out.println("4. Search trains");
			System.out.println("5. Book a seat");
			System.out.println("6. Cancel my booking");
			System.out.println("7. Exit the app");

			option = sc.nextInt();

			switch (option){
				case 1:
					System.out.println("Enter the username to signup");
					String nameToSignUp = sc.next();
					System.out.println("Enter the password to signup");
					String passwordToSignUp = sc.next();
					User userToSignUp = new User(nameToSignUp , passwordToSignUp , UserServiceUtil.hashPassword(passwordToSignUp) , new ArrayList<>() , UUID.randomUUID().toString());
					userBookingService.signUp(userToSignUp);
					break;

				case 2 :
					System.out.println("Enter the username to login");
					String nameToLogin = sc.next();
					System.out.println("Enter the password to login");
					String passwordToLogin = sc.next();

					loggedInUser = new User(nameToLogin , passwordToLogin , UserServiceUtil.hashPassword(passwordToLogin) , new ArrayList<>() , UUID.randomUUID().toString());

					try {
						UserBookingService tempService = new UserBookingService(loggedInUser);
						if (tempService.loginUser()) {
							userBookingService = tempService;
							System.out.println("Login successful.");
						} else {
							loggedInUser = null;
							System.out.println("Login failed. Invalid credentials.");
						}
					} catch (IOException ex){
						System.out.println("Error during login.");
						ex.printStackTrace();
					}
					break;

				case 3 :
					System.out.println("Fetching your Bookings");
					if (loggedInUser == null) {
						System.out.println("No user is logged in.");
					} else {
						userBookingService.fetchBookings();
					}
					break;

				case 4:
					if (loggedInUser == null) {
						System.out.println("Please log in to book a seat.");
						break;
					}
					System.out.println("Type your source station");
					String source = sc.next();
					System.out.println("Type your destination station");
					String dest = sc.next();

					TrainService tService;
					try {
						tService = new TrainService();
					} catch (IOException e) {
						System.out.println("Error loading train data.");
						break;
					}

					List<Train> AvialableTrains = tService.searchTrains(source, dest);

					if (AvialableTrains.isEmpty()) {
						System.out.println("No trains available from " + source + " to " + dest);
						break;
					}

					System.out.println("\nAvailable Trains:");
					for (int i = 0; i < AvialableTrains.size(); i++) {
						Train t = AvialableTrains.get(i);
						System.out.println((i + 1) + ". Train ID: " + t.getTrainId() + " | Name: " + t.getTrainNo());
					}
					break;
				case 5:
					if (loggedInUser == null) {
						System.out.println("Please log in to book a seat.");
						break;
					}
					System.out.println("Type your source station");
					String src = sc.next();
					System.out.println("Type your destination station");
					String destination = sc.next();
					System.out.print("Enter date of travel (yyyy-MM-dd): ");
					String travelDate = sc.next();

					TrainService trainService;
					try {
						trainService = new TrainService();
					} catch (IOException e) {
						System.out.println("Error loading train data.");
						break;
					}

					List<Train> matchingTrains = trainService.searchTrains(src, destination);

					if (matchingTrains.isEmpty()) {
						System.out.println("No trains available from " + src + " to " + destination);
						break;
					}

					System.out.println("\nAvailable Trains:");
					for (int i = 0; i < matchingTrains.size(); i++) {
						Train t = matchingTrains.get(i);
						System.out.println((i + 1) + ". Train ID: " + t.getTrainId() + " | Name: " + t.getTrainNo());
					}

					System.out.print("\nEnter the number of the train you want to book: ");
					int trainChoice = sc.nextInt();
					sc.nextLine();

					if (trainChoice < 1 || trainChoice > matchingTrains.size()) {
						System.out.println("Invalid choice.");
						break;
					}

					Train selectedTrain = matchingTrains.get(trainChoice - 1);

					// Fetch fresh seat data
					List<List<Integer>> seats = userBookingService.fetchSeats(selectedTrain);

					if (seats == null) {
						System.out.println("Could not load seat layout.");
						break;
					}

					System.out.println("\nSeat Layout (0 = Available, 1 = Booked):");
					for (int i = 0; i < seats.size(); i++) {
						System.out.print("Row " + i + ": ");
						for (Integer seat : seats.get(i)) {
							System.out.print(seat + " ");
						}
						System.out.println();
					}

					// Book automatically
					System.out.println("Auto-assigning a seat...");
					List<Integer> bookedSeat = userBookingService.bookTrainSeat(selectedTrain , src ,destination , travelDate);

					if (bookedSeat != null) {
						System.out.println("Seat successfully booked at Row " + bookedSeat.get(0) + ", Column " + bookedSeat.get(1));
					} else {
						System.out.println("No seats available to book.");
					}

					break;

				default:
					System.out.println("Exiting the app...");
					break;
			}
		}
	}
}
