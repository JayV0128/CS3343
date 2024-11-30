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

    public boolean addQA(CsQuestion question) {
        if(Database.getInstance().getTable_question().add(question)){
            return true;
        }else{
            return false;
        }
    }
}
