package DAO;

import java.util.*;
import DB_init.*;
import DataModel.*;

public class OrderRecordDAO {
    private ArrayList<OrderRecord> table_orderRecord;

    public OrderRecordDAO() {
        table_orderRecord = Database.getInstance().getTable_orderRecord();
    }

    public ArrayList<OrderRecord> getTable_orderRecord() {
        return table_orderRecord;
    }

    public boolean addOrderRecord(OrderRecord orderRecord) {
        table_orderRecord.add(orderRecord);
        return true;
    }

    public ArrayList<OrderRecord> getOrdersByUserId(int userId) {
        ArrayList<OrderRecord> userOrders = new ArrayList<>();
        for (OrderRecord order : table_orderRecord) {
            if (order.getUserId() == userId) {
                userOrders.add(order);
            }
        }
        return userOrders;
    }

    public OrderRecord getOrderById(int orderId) {
        for (OrderRecord order : table_orderRecord) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        return null;
    }

   
    public boolean updateOrderRecord(OrderRecord updatedOrder) {
        for (int i = 0; i < table_orderRecord.size(); i++) {
            if (table_orderRecord.get(i).getOrderId() == updatedOrder.getOrderId()) {
                table_orderRecord.set(i, updatedOrder);
                return true;
            }
        }
        return false;
    }

    public boolean deleteOrderRecord(int orderId) {
        Iterator<OrderRecord> iterator = table_orderRecord.iterator();
        while (iterator.hasNext()) {
            OrderRecord order = iterator.next();
            if (order.getOrderId() == orderId) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}
