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

    public boolean addQA(ArrayList<String> keywords, String answer) {
            CsQuestion question = new CsQuestion(keywords, answer);
            return question_table.add(question);
    }
}
