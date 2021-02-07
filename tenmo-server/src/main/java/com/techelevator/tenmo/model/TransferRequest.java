package com.techelevator.tenmo.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

//TransferInput
public class TransferRequest {
	
	@Min( value = 1, message = "The field 'toUserId' is required.")
	private int fromUserId;
	@Min( value = 1, message = "The field 'toUserId' is required.")
	private int toUserId;
	@Positive( message = "Amount cannot be negative or zero.")
	private double amount;
	
	public TransferRequest() {
		
		
	}
	
	public TransferRequest(int fromUserId, int toUserId, double amount) {
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.amount = amount;
	}

	public int getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(int fromUserId) {
		this.fromUserId = fromUserId;
	}

	public int getToUserId() {
		return toUserId;
	}

	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
