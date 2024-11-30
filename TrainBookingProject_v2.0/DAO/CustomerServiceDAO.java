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
    
    public boolean addQA(ArrayList<String> keywords, String answer) {
            CsQuestion question = new CsQuestion(keywords, answer);
            return Database.getInstance().getTable_question().add(question);
    }
}
