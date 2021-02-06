package com.techelevator.tenmo.services;

import java.util.List;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;


import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.User;
import com.techelevator.view.ConsoleService;
import com.techelevator.tenmo.models.TransferRequest;

public class TenmoService {
	
	private String BASE_URL;
	private final RestTemplate restTemplate = new RestTemplate();
	
	
	
	public TenmoService(String url) {
		this.BASE_URL = url;
	}

	//Gets Account Balance for a user
	public Account getBalance(AuthenticatedUser currentUser) {
		String token = currentUser.getToken(); 
		HttpEntity entity = AuthenticationService.makeAuthEntity(token);
		String url = BASE_URL + "user/me/balance";
		return restTemplate.exchange(url, HttpMethod.GET, entity, Account.class).getBody();
	}
	public User[] getAllUsers() {
		/*int id = currentUser.getUser().getId();*/
		//String token = currentUser.getToken(); 
		//HttpEntity entity = AuthenticationService.makeAuthEntity(token);
		String url = BASE_URL + "/allusers";
		User[] allUsers = null;
		try {
			allUsers = restTemplate.getForObject(url, User[].class);
		} catch (RestClientResponseException ex) {
		} catch (ResourceAccessException ex) {
		}
		return allUsers;
	}
	
	//I may be wrong here but In our app, I feel like we need this method to continue
	public User getUserbyId() {
		return null;
	}
	public TransferRequest sendMoney(AuthenticatedUser currentUser, TransferRequest toUser) {
		String token = currentUser.getToken(); 
		HttpEntity entity = AuthenticationService.makeAuthEntity(token);
		String url = BASE_URL + "/transfers/sendmoney";
		TransferRequest toUserId = toUser;
		if (toUserId == null) {
			return null;
		}
		 try {
		      toUserId = restTemplate.postForObject(url, entity, TransferRequest.class);
		    } catch (RestClientResponseException ex) {
		      
		    } catch (ResourceAccessException ex) {
		      
		    }
		    return toUserId;
	}

}
