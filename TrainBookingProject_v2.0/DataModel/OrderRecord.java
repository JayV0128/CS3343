package DataModel;

import java.util.*;

public class OrderRecord {
    private String orderId;
    private String userId;
    private String trainId;
    private Date orderDate;
    private double amount;
    // private ArrayList<Passenger> passengerList;
    private ArrayList<Ticket> ticketList;
    private int rating;

    // Default constructor
    public OrderRecord() {}

    // Parameterized constructor
    public OrderRecord(String orderId, String userId, String trainId, Date orderDate, double amount, ArrayList<Ticket> ticketList) {
        this.orderId = orderId;
        this.userId = userId;
        this.trainId = trainId;
        this.orderDate = orderDate;
        this.amount = amount;
        // this.passengerList = passengerList;
        this.ticketList = ticketList;
        this.rating = 0;
    }
    
    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

//    public void setOrderId(String orderId) {
//        this.orderId = orderId;
//    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

//    public void setOrderDate(Date orderDate) {
//        this.orderDate = orderDate;
//    }

    public double getAmount() {
        return amount;
    }

//    public void setAmount(double amount) {
//        this.amount = amount;
//    }


    public ArrayList<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(ArrayList<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
    
	public int getRating() {
		return rating;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("User ID: %s\nTrain ID: %s\nOrder Date: %s\nTotal Amount: $%.2f\n", 
            userId, trainId, orderDate.toString(), amount));
        sb.append("Passengers:\n");
        for (Ticket t : ticketList) {
            sb.append(t.toString()).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        // sb.append("Tickets:\n");
        // for (Ticket t : ticketList) {
        //     sb.append(t.toString()).append("\n");
        // }
        return sb.toString();
    }
}