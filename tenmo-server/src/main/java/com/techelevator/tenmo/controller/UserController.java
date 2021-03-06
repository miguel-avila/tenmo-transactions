package com.techelevator.tenmo.controller;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.User;
//import com.techelevator.tenmo.model.UserNotFound;

@RestController
public class UserController {
	
	private UserDAO userDAO;
	
	public UserController(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@RequestMapping(path = "/allusers", method = RequestMethod.GET)
	public List<User> listUsers(){
		return userDAO.findAll();
	}

}
