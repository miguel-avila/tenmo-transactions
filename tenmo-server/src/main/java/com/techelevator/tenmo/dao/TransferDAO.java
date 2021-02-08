package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {

	List<Transfer> findAll(int userId);
	
	Transfer getTransferById(int id);
	
	boolean create(int accountFrom, int accountTo, double amount);
	
	boolean increaseBalance(int accountTo, double amount);
	
	boolean decreaseBalance(int accountFrom, double amount);
	
	public Transfer transfer(int userId, Transfer transfer) throws Exception;
	
}
