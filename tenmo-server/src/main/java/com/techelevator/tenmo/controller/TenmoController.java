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
	//potentially replace id with "me"
	//Gets Account Balance for a user
	@RequestMapping(path = "/user/{id}/balance", method = RequestMethod.GET)
	public Account getBalance(@PathVariable int id) {
		return userDAO.findBalanceByUserId(id);
	}
	//transferhistory()
	//pendingtransfers
	//sendbucks--use user-id to "put" aka update balance transfer from and to--helper methods
	//requestTransfer -- use user-id to   -- helper methods
	/*@RequestMapping(path = "/users", method = RequestMethod.GET){
	public List<User> list() {
		return userDAO.();
	}
	*/
	
	}

