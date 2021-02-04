package com.techelevator.tenmo.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;

@RestController
public class TenmoController {

	private UserDAO userDAO;
	
	public TenmoController(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	//Gets Account Balance for a user
	@RequestMapping(path = "/user/{id}/balance", method = RequestMethod.GET)
	public Account getBalance(@PathVariable int id) {
		return userDAO.findBalanceByUserId(id);
	}
	
}
