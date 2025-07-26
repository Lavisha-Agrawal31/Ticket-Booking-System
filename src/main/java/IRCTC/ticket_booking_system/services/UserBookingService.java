package IRCTC.ticket_booking_system.services;

import IRCTC.ticket_booking_system.entities.Ticket;
import IRCTC.ticket_booking_system.entities.Train;
import IRCTC.ticket_booking_system.entities.User;
import IRCTC.ticket_booking_system.util.UserServiceUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {
    private User user;
    private List<User> userList;

    private static final String USERS_PATH = "localDb/users.json";

    private ObjectMapper objectMapper = new ObjectMapper();

    public UserBookingService(User user1) throws IOException {
        this.user = user1;
        loadUsers();
    }

    public UserBookingService() throws IOException{
        loadUsers();
    }

    public List<User> loadUsers() throws IOException{
        File users = new File(USERS_PATH);
        return userList = objectMapper.readValue(users, new TypeReference<List<User>>() {});
    }

    public boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtil.checkPassword(user.getPassword() , user1.getHashedPassword());
        }).findFirst();
        if (foundUser.isPresent()) {
            this.user = foundUser.get();
            return true;
        }
        return false;
    }

    public boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch (IOException ex){
            return Boolean.FALSE;
        }
    }

    public void saveUserListToFile() throws IOException{
        File userFile = new File(USERS_PATH);
        objectMapper.writeValue(userFile , userList);
    }

    public void fetchBookings() {
        if (user == null || user.getName() == null || user.getName().isEmpty()) {
            System.out.println("No user is logged in.");
            return;
        }

        Optional<User> optionalUser = userList.stream()
                .filter(u -> u.getName().equalsIgnoreCase(user.getName()))
                .findFirst();

        if (optionalUser.isPresent()) {
            User loggedInUser = optionalUser.get();
            System.out.println("Booked Tickets for: " + loggedInUser.getName());
            loggedInUser.printTickets();
        } else {
            System.out.println("User not found in the system.");
        }
    }


    public List<List<Integer>> fetchSeats(Train train) {
        try {
            TrainService trainService = new TrainService();
            Train freshTrain = trainService.getTrainById(train.getTrainId());
            return freshTrain != null ? freshTrain.getSeats() : null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Integer> bookTrainSeat(Train train, String source, String destination, String travelDate) {
        try {
            TrainService trainService = new TrainService();
            Train freshTrain = trainService.getTrainById(train.getTrainId());
            if (freshTrain == null) return null;

            List<List<Integer>> seats = freshTrain.getSeats();
            if (seats == null) return null;

            for (int i = 0; i < seats.size(); i++) {
                for (int j = 0; j < seats.get(i).size(); j++) {
                    if (seats.get(i).get(j) == 0) {
                        // Book seat
                        seats.get(i).set(j, 1);
                        freshTrain.setSeats(seats);
                        trainService.updateTrain(freshTrain);

                        // Create ticket
                        Ticket ticket = new Ticket();
                        ticket.setTicketId(java.util.UUID.randomUUID().toString());
                        ticket.setUserId(user.getUserId());
                        ticket.setSource(source);
                        ticket.setDestination(destination);
                        ticket.setDateToTravel(travelDate);
                        ticket.setTrain(freshTrain);
                        ticket.setSeatRow(i);
                        ticket.setSeatCol(j);

                        // Find and update user
                        Optional<User> matchedUser = userList.stream()
                                .filter(u -> u.getUserId().equals(user.getUserId()))
                                .findFirst();

                        if (matchedUser.isPresent()) {
                            User u = matchedUser.get();

                            // Initialize ticket list if null
                            if (u.getTicketsBooked() == null) {
                                u.setTicketsBooked(new ArrayList<>());
                            }

                            // Add the ticket to user's ticket list
                            u.getTicketsBooked().add(ticket);

                            // Update in user list
                            for (int idx = 0; idx < userList.size(); idx++) {
                                if (userList.get(idx).getUserId().equals(u.getUserId())) {
                                    userList.set(idx, u);
                                    break;
                                }
                            }

                            // Also update the local user object
                            this.user = u;

                            // Save user list to file
                            saveUserListToFile();
                        }

                        return List.of(i, j);
                    }
                }
            }

            System.out.println("No available seats.");
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
