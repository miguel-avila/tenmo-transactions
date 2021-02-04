package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Account;

public class TenmoService {
	
	private String BASE_URL;
	private final RestTemplate restTemplate = new RestTemplate();
	
	public TenmoService(String url) {
		this.BASE_URL = url;
	}

	//Gets Account Balance for a user
	public Account getBalance(int id) {
	//TODO need to add authentication?
		
//		HttpEntity entity = 
		return restTemplate.getForObject(BASE_URL + "user/" + id + "/balance", Account.class);
	}
//setter for auth token	
}
