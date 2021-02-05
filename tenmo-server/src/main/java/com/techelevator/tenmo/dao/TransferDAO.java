package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {

	List<Transfer> findAll(int userId);
	
	Transfer getTransferById(int id);
	
	boolean create(int account_from, int account_to, double amount);
	
	boolean increaseBalance(int account_to, double amount);
	
	boolean decreaseBalance(int account_from, double amount);
	
}
