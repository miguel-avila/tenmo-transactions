package com.techelevator.tenmo.model;

public class Account {

	private int userId;
	private int accountId;
	private double balance;
	
	public Account() { };
	
	public Account(int userId, int accountId, double balance) {
		this.userId = userId;
		this.accountId = accountId;
		this.balance = balance;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public int getAccountId() {
		return accountId;
	}
	
	public double getBalance() {
		return balance;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	
	
}
