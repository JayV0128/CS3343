package DB_init;

import DataModel.*;

import java.util.*;

public class Database {

    // Singleton
    private static Database instance = null;

    // DB contains all tables right here:
    private ArrayList<User> table_user;
    private ArrayList<Train> table_train;
    private ArrayList<OrderRecord> table_orderRecord;
    private ArrayList<seatPlan> table_seattingPlan;
    private ArrayList<CsQuestion> table_question;

    // ID values reference for each table

    private Database() {
        table_user = new ArrayList<>();
        table_train = new ArrayList<>();
        table_orderRecord = new ArrayList<>();
        table_seattingPlan = new ArrayList<>();
        table_question = new ArrayList<>();
        DB_initialize();
    }

    private void DB_initialize() {
        table_user.add(new User("admin", "userID_1", "admin", "admin"));
        table_user.add(new User("normal", "userID_2", "q", "q"));
        table_user.add(new User("normal", "userID_3", "b", "b"));
        table_user.add(new User("normal", "userID_4", "c", "c"));
        table_user.add(new User("normal", "userID_11", "y", "y"));
        table_user.add(new User("normal", "userID_12", "x", "x"));
        

        table_train.add(new Train("trainID_1", "LA", "Chicago", "2024-10-01", "12:00", 24, 100));
        table_train.add(new Train("trainID_2", "Washington DC", "Miami", "2024-10-02", "14:00", 24, 150));
        table_train.add(new Train("trainID_3", "Washington DC", "Miami", "2024-10-03", "14:00", 21, 150));

        // Sample data for Admin - two overloaded train time slots (same train route)
        table_train.add(new Train("trainID_4", "Houston", "Dallas", "2024-10-06", "10:00", 24, 110));
        table_train.add(new Train("trainID_5", "Houston", "Dallas", "2024-10-06", "11:00", 24, 150));
        table_train.add(new Train("trainID_6", "Houston", "Dallas", "2024-10-06", "17:00", 24, 110));

        table_train.add(new Train("trainID_7", "HK", "TW", "2024-10-06", "10:00", 24, 110));
        table_train.add(new Train("trainID_8", "HK", "TW", "2024-10-06", "11:00", 24, 110));
        table_train.add(new Train("trainID_9", "HK", "TW", "2024-10-06", "12:00", 24, 110));
        table_train.add(new Train("trainID_10", "HK", "TW", "2024-10-06", "13:00", 24, 110));
        table_train.add(new Train("trainID_11", "HK", "TW", "2024-10-06", "14:00", 24, 110));
        table_train.add(new Train("trainID_12", "HK", "TW", "2024-10-06", "15:00", 24, 110));
        table_train.add(new Train("trainID_13", "HK", "TW", "2024-10-06", "16:00", 24, 110));

        // Sample data for Seat Plan
        table_seattingPlan.add(new seatPlan("trainID_1"));
        table_seattingPlan.add(new seatPlan("trainID_2"));
        table_seattingPlan.add(new seatPlan("trainID_6"));

        // test case:index 0 -4 are booked(A1, A2, A3, A4, A5)
        seatPlan sp1 = new seatPlan("trainID_3");
        sp1.updateSeat(0, "X");
        sp1.updateSeat(1, "X");
        sp1.updateSeat(2, "X");
        table_seattingPlan.add(sp1);

        seatPlan sp6 = new seatPlan("trainID_6");
        sp6.updateSeat(0, "X");
        sp6.updateSeat(1, "X");
        sp6.updateSeat(2, "X");
        sp6.updateSeat(3, "X");
        sp6.updateSeat(4, "X");
        sp6.updateSeat(5, "X");
        sp6.updateSeat(6, "X");
        sp6.updateSeat(7, "X");
        sp6.updateSeat(8, "X");
        sp6.updateSeat(9, "X");
        sp6.updateSeat(10, "X");
        sp6.updateSeat(11, "X");
        sp6.updateSeat(12, "X");
        sp6.updateSeat(13, "X");
        sp6.updateSeat(14, "X");
        sp6.updateSeat(15, "X");
        sp6.updateSeat(16, "X");
        sp6.updateSeat(17, "X");
        sp6.updateSeat(18, "X");
        sp6.updateSeat(19, "X");
        sp6.updateSeat(20, "X");
        sp6.updateSeat(21, "X");
        sp6.updateSeat(22, "X");
        table_seattingPlan.add(sp6);

        // test case CS question
        table_question.add(new CsQuestion(new ArrayList<String>(Arrays.asList("book", "ticket")),
                "To book a ticket, you should press 1 in the main menu after login, then follow the instructions to do booking."));
        table_question.add(new CsQuestion(new ArrayList<String>(Arrays.asList("edit", "ticket")),
                "To edit a ticket, you should press 2 in the main menu after login, then select a order to edit."));
        table_question.add(new CsQuestion(new ArrayList<String>(Arrays.asList("check", "ticket")),
                "To check a ticket, you should press 2 in the main menu after login, then you can see all ticket you booked and select the view the details."));
        table_question.add(new CsQuestion(new ArrayList<String>(Arrays.asList("cancel", "ticket")),
                "To cancel a ticket, you should press 2 in the main menu after login, then follow the instructions to cancel."));
        table_question.add(new CsQuestion(new ArrayList<String>(Arrays.asList("customer service")),
                "If you have any problem, please press 3 to contact customer service."));
        table_question
                .add(new CsQuestion(new ArrayList<String>(Arrays.asList("help")),
                        "If you have any problem, please press 3 to contact customer service."));
        table_question
                .add(new CsQuestion(new ArrayList<String>(Arrays.asList("contact method")),
                        "How to find us : email : 123@123.com, phone : 1234567890"));
    }

    public ArrayList<User> getTable_user() {
        return getInstance().table_user;
    }

    public ArrayList<Train> getTable_train() {
        return getInstance().table_train;
    }

    public ArrayList<OrderRecord> getTable_orderRecord() {
        return getInstance().table_orderRecord;
    }

    public ArrayList<seatPlan> getTable_seattingPlan() {
        return getInstance().table_seattingPlan;
    }

    public ArrayList<CsQuestion> getTable_question() {
        return getInstance().table_question;
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void resetDB() {
    	instance = new Database();
    }
}
