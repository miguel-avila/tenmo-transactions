package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
//import com.techelevator.tenmo.model.UserNotFound;

import java.util.List;

public interface UserDAO {

    List<User> findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);
    
    Account getAccountByUserId(int userId);
    
//    User findUserById(int id) throws UserNotFound;
    
//    double findBalanceByUserId(int id);
    
//    public void updateBalance(int accountTo, int accountFrom, double amount);
    
}
