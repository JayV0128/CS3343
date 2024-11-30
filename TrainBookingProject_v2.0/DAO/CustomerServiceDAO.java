package DAO;

import java.util.*;
import DB_init.*;
import DataModel.*;

public class CustomerServiceDAO {
    public CustomerServiceDAO() {
    }

    public ArrayList<CsQuestion> getTable_question() {
        return Database.getInstance().getTable_question();
    }

<<<<<<< HEAD
    public boolean addQA(CsQuestion question) {
        if(Database.getInstance().getTable_question().add(question)){
            return true;
        }else{
            return false;
        }
=======
    public boolean addQA(ArrayList<String> keywords, String answer) {
            CsQuestion question = new CsQuestion(keywords, answer);
            return question_table.add(question);
>>>>>>> branch 'main' of https://github.com/JayV0128/TrainBookingProject.git
    }
}
