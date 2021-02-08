package com.techelevator.tenmo.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "You need more money.")
public class InsufficientFundsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public InsufficientFundsException() {
		super("You need more money.");
	}
}
