package com.techelevator.tenmo.model;

public class Account {
	private int userId;
	private int accountId;
	private double balance;
	
	public Account() { };
	
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
