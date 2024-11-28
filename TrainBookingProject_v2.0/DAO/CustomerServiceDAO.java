package DAO;

import java.util.*;
import DB_init.*;
import DataModel.*;

public class CustomerServiceDAO {
    private ArrayList<CsQuestion> question_table;
    
    public CustomerServiceDAO() {
        question_table = Database.getInstance().getTable_question();
    }

    public ArrayList<CsQuestion> getTable_question() {
        return question_table;
    }

    public boolean addQA(CsQuestion question) {
        if(question_table.add(question)){
            return true;
        }else{
            return false;
        }
    }
}
