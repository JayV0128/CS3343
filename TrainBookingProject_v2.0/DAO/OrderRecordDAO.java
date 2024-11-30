package DAO;

import java.util.*;

import DB_init.*;
import DataModel.*;

public class OrderRecordDAO {
    public OrderRecordDAO() {
    }

    public ArrayList<OrderRecord> getTable_orderRecord() {
        return Database.getInstance().getTable_orderRecord();
    }

    public boolean addOrderRecord(OrderRecord orderRecord) {
        Database.getInstance().getTable_orderRecord().add(orderRecord);
        return true;
    }

    public ArrayList<OrderRecord> getOrdersByUserId(String userId) {
        ArrayList<OrderRecord> userOrders = new ArrayList<>();
        for (OrderRecord order : Database.getInstance().getTable_orderRecord()) {
            if (order.getUserId().equals(userId)) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }

    public OrderRecord getOrderById(String orderId) {
        for (OrderRecord order : Database.getInstance().getTable_orderRecord()) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

   
    public boolean updateOrderRecord(OrderRecord updatedOrder) {
        for (int i = 0; i < Database.getInstance().getTable_orderRecord().size(); i++) {
            if (Database.getInstance().getTable_orderRecord().get(i).getOrderId() == updatedOrder.getOrderId()) {
                Database.getInstance().getTable_orderRecord().set(i, updatedOrder);
                return true;
            }
        }
        return false;
    }

    public boolean deleteOrderRecord(String orderId) {
        Iterator<OrderRecord> iterator = Database.getInstance().getTable_orderRecord().iterator();
        while (iterator.hasNext()) {
            OrderRecord order = iterator.next();
            if (order.getOrderId().equals(orderId)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}