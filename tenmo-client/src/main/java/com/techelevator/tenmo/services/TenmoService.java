package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;


import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.TransferRequest;

public class TenmoService {
	private String BASE_URL;
	private final RestTemplate restTemplate = new RestTemplate();
	public static String AUTH_TOKEN = "";
	private final String INVALID_TRANSFER_MSG = "Invalid Transfer";
	
	public TenmoService(String url) {
		BASE_URL = url;
	}

	//Gets Account Balance for a user
	public Account getBalance(AuthenticatedUser currentUser) {
		String token = currentUser.getToken(); 
		HttpEntity entity = AuthenticationService.makeAuthEntity(token);
		String url = BASE_URL + "user/me/balance";
		return restTemplate.exchange(url, HttpMethod.GET, entity, Account.class).getBody();
	}

	public User[] getAllUsers(AuthenticatedUser currentUser) {
		String token = currentUser.getToken(); 
		HttpEntity entity = AuthenticationService.makeAuthEntity(token);
		String url = BASE_URL + "/allusers";
		return restTemplate.exchange(url, HttpMethod.GET, entity, User[].class).getBody();
	}
	
	//Step 5
	public Transfer[] getTransferHistory(AuthenticatedUser currentUser) {
		String token = currentUser.getToken(); 
		HttpEntity entity = AuthenticationService.makeAuthEntity(token);
		String url = BASE_URL + "/transfers";
		return restTemplate.exchange(url, HttpMethod.GET, entity, Transfer[].class).getBody();
	}
	
	//Step 6
	public Transfer getTransferById(int id, AuthenticatedUser currentUser) {
		String token = currentUser.getToken(); 
		HttpEntity entity = AuthenticationService.makeAuthEntity(token);
		String url = BASE_URL + "/transfers/" + id;
		return restTemplate.exchange(url, HttpMethod.GET, entity, Transfer.class).getBody();
	}
	
	public boolean sendMoney(AuthenticatedUser currentUser, int toUserId, double amount)
			throws TenmoServiceException {
		TransferRequest request = new TransferRequest();
		request.setFromUserId(currentUser.getUser().getId());
		request.setToUserId(toUserId);
		request.setAmount(amount);
		
		if (toUserId <= 0) {
			throw new TenmoServiceException(INVALID_TRANSFER_MSG);
		}
		
		try {
			AUTH_TOKEN = currentUser.getToken();
			HttpEntity entity = makeTransferEntity(request);
			String url = BASE_URL + "/transfers/sendmoney";
			return restTemplate.exchange(url, HttpMethod.POST, entity, Boolean.class).getBody();
		} catch (RestClientResponseException ex) {
			throw new TenmoServiceException("Unable to initiate your transfer, please try again later.");
		}
	}
		
	 private HttpEntity<TransferRequest> makeTransferEntity(TransferRequest transfer) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);
	    headers.setBearerAuth(AUTH_TOKEN);
	    HttpEntity<TransferRequest> entity = new HttpEntity<>(transfer, headers);
	    return entity;
	 }

}
