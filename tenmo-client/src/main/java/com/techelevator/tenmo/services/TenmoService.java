package com.techelevator.tenmo.services;

import org.springframework.web.client.RestTemplate;

public class TenmoService {
	
	private String BASE_URL;
	private final RestTemplate restTemplate = new RestTemplate();
	
	public TenmoService(String url) {
		this.BASE_URL = url;
	}

	//Gets Account Balance for a user
	public double getBalance(int id) {
	//TODO need to add authentication
		
		return restTemplate.getForObject(BASE_URL + "user/" + id + "/balance", double.class);
	}
	
}
