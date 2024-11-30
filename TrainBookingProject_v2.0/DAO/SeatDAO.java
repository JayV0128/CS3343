package DAO;
import java.util.*;

import DB_init.*;
import DataModel.*;

public class SeatDAO {
    public SeatDAO() {
    }

    public ArrayList<seatPlan> getTable_seattingPlan(){
        return Database.getInstance().getTable_seattingPlan();
    }

    public seatPlan getSeatPlan_ByTrainID(String trainNumber) {
        seatPlan targetSeatPlan = null;
        for (seatPlan seatPlan : Database.getInstance().getTable_seattingPlan()) {
            if (seatPlan.getTrainID().equals(trainNumber)) {
                targetSeatPlan = seatPlan;
            }
        }
        
        return targetSeatPlan;
    }


    public String getOneAvailableSeat(seatPlan seatPlan) {
        
        for(int i=0; i<seatPlan.getSeatList().size(); i++) {
            String seat = seatPlan.getSeatList().get(i);
            if (!seat.equals("X")) {

                int latestSeatCount = seatPlan.getSeatCount();
                latestSeatCount--;
                seatPlan.setSeatCount(latestSeatCount);
                seatPlan.updateSeat(i, "X");
                return seat;
            }
        }

        return null;
    }

    public ArrayList<String> getMultipleAvailableSeat(seatPlan seatPlan, int passengerCount) {
        ArrayList<String> target_seatList = seatPlan.getSeatList();
        ArrayList<String> assignedSeats = new ArrayList<>();
        int seatsPerRow = 6;
    
        int remainingPassengers = passengerCount;

        // if there are more passengers than seats per row > 6:
        if (passengerCount > seatsPerRow) {
            for (int i = 0; i < target_seatList.size() && remainingPassengers > 0; i++) {
                
                if (!target_seatList.get(i).equals("X")) {
                    assignedSeats.add(target_seatList.get(i));
                    target_seatList.set(i, "X");
                    remainingPassengers--;
                }
            }

            seatPlan.setSeatCount(seatPlan.getSeatCount() - passengerCount);
            return assignedSeats;
        }
    
    
        // Strategy 1: Try to assign full rows first
        if (remainingPassengers > seatsPerRow) {
            for (int rowStart = 0; rowStart < target_seatList.size(); rowStart += seatsPerRow) {
                boolean fullRowAvailable = true;
                ArrayList<Integer> rowIndices = new ArrayList<>();
                
                for (int i = rowStart; i < Math.min(rowStart + seatsPerRow, target_seatList.size()); i++) {
                    if (target_seatList.get(i).equals("X")) {
                        fullRowAvailable = false;
                        break;
                    }
                    rowIndices.add(i);
                }
                
                if (fullRowAvailable) {
                    for (int index : rowIndices) {
                        assignedSeats.add(target_seatList.get(index));
                        seatPlan.updateSeat(index, "X");
                    }
                    remainingPassengers -= seatsPerRow;
                    if (remainingPassengers <= 0) break;
                }
            }
        }
    
        // Strategy 2: Try to find consecutive seats in rows
        if (remainingPassengers > 0) {
            for (int rowStart = 0; rowStart < target_seatList.size(); rowStart += seatsPerRow) {
                int consecutiveSeats = 0;
                int startIndex = -1;
                
                for (int i = rowStart; i < Math.min(rowStart + seatsPerRow, target_seatList.size()); i++) {
                    if (!target_seatList.get(i).equals("X")) {
                        if (startIndex == -1) startIndex = i;
                        consecutiveSeats++;
                        
                        if (consecutiveSeats == remainingPassengers) {
                            for (int j = 0; j < consecutiveSeats; j++) {
                                assignedSeats.add(target_seatList.get(startIndex + j));
                                seatPlan.updateSeat(startIndex + j, "X");
                            }
                            remainingPassengers = 0;
                            break;
                        }
                    } else {
                        consecutiveSeats = 0;
                        startIndex = -1;
                    }
                }
                if (remainingPassengers == 0) break;
            }
        }
    
        // Strategy 3: Assign any available seats
        if (remainingPassengers > 0) {
            for (int i = 0; i < target_seatList.size() && remainingPassengers > 0; i++) {
                if (!target_seatList.get(i).equals("X")) {
                    assignedSeats.add(target_seatList.get(i));
                    seatPlan.updateSeat(i, "X");
                    remainingPassengers--;
                }
            }
        }
    
        // Final check if we got all needed seats
        if (assignedSeats.size() == passengerCount) {
            seatPlan.setSeatCount(seatPlan.getSeatCount() - passengerCount);
            return assignedSeats;
        }
    
        return null; // Return null if unable to fulfill the request
    }

}
