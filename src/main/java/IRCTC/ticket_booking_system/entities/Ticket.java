package IRCTC.ticket_booking_system.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)

public class Ticket {
    private String ticketId;
    private String userId;
    private String source;
    private String destination;
    private String dateToTravel;
    private Train train;
    private int seatRow;
    private int seatCol;

    public Ticket(String ticketId, String userId, String source, String destination, String dateToTravel, Train train, int seatRow, int seatCol) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.source = source;
        this.destination = destination;
        this.dateToTravel = dateToTravel;
        this.train = train;
        this.seatRow = seatRow;
        this.seatCol = seatCol;
    }

    public Ticket() {
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDateToTravel() {
        return dateToTravel;
    }

    public void setDateToTravel(String dateToTravel) {
        this.dateToTravel = dateToTravel;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public int getSeatRow() {
        return seatRow;
    }

    public void setSeatRow(int seatRow) {
        this.seatRow = seatRow;
    }

    public int getSeatCol() {
        return seatCol;
    }

    public void setSeatCol(int seatCol) {
        this.seatCol = seatCol;
    }

    public String getTicketInfo(){
        return "\n---------------------------------------------------\n" +
                "Ticket ID   : " + ticketId + "\n" +
                "User ID     : " + userId + "\n" +
                "From        : " + source + "\n" +
                "To          : " + destination + "\n" +
                "Travel Date : " + dateToTravel + "\n" +
                "Seat No.    : Row " + seatRow + ", Col " + seatCol + "\n" +
                "Train Name  : " + train.getTrainNo() + " (ID: " + train.getTrainId() + ")\n" +
                "---------------------------------------------------\n";
    }
}

