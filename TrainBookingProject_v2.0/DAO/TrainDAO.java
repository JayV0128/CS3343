package DAO;

import java.util.*;
import DB_init.*;
import DataModel.*;

public class TrainDAO { 
    public TrainDAO() {
    }
    
    public ArrayList<Train> getTable_train() {
        return Database.getInstance().getTable_train();
    }
    
    public Train getTrain_fromTrainTable(String trainNumber) {
        Train targetTrain = null;
        for (Train train : Database.getInstance().getTable_train()) {
            if (train.getTrainNumber().equals(trainNumber)) {
                targetTrain = train;
            }
        }
        
        return targetTrain;
    }
    
    public boolean addTrain_fromTrainTable(Train train) {
        Database.getInstance().getTable_train().add(train);
        return true;
    }
    
    public boolean updateTrain_fromTrainTable(Train train) {
        boolean result = false;
        Train foundTrain = getTrain_fromTrainTable(train.getTrainNumber());
        
        if(foundTrain != null) {
            foundTrain.setTrainNumber(train.getTrainNumber());
            foundTrain.setDeparture(train.getDeparture());
            foundTrain.setArrival(train.getArrival());
            foundTrain.setDate(train.getDate());
            foundTrain.setTime(train.getTime());
            foundTrain.setAvailableSeats(train.getAvailableSeats());
            foundTrain.setPrice(train.getPrice());
            result = true;
        }
        
        return result;
    }
    
    public boolean deleteTrain_fromTrainTable(String trainNumber) {
        boolean result = false;
        Train foundTrain = getTrain_fromTrainTable(trainNumber);
        
        if(foundTrain != null) {
            Database.getInstance().getTable_train().remove(foundTrain);
            result = true;
        }
        return result;
    }
    
}
