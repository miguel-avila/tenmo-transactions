package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;


import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
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
		User[] allUsers = null;
			allUsers = restTemplate.exchange(url, HttpMethod.GET, entity, User[].class).getBody();
		return allUsers;
	}
	
	//I may be wrong here but In our app, I feel like we need this method to continue
	public User getUserbyId(long id) throws TenmoServiceException {
		String url = BASE_URL + "/user/{id}";
		User user = null;
		try {
			user = restTemplate.exchange(url + id, HttpMethod.GET, makeAuthEntity(), User.class).getBody();
		} catch (RestClientResponseException ex) {
		throw new TenmoServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return user;
	}
	
	//TODO fix this somehow
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
	
	/*private TransferRequest makeRequest(String CSV)	{
		String[] parsed = CSV.split(",");
		if (parsed.length < 2 || parsed.length > )2 {
		      return null;
		    }

		    // Add method does not include an id and only has 5 strings
		    if (parsed.length == 3) {
		      // Create a string version of the id and place into an array to be concatenated
		      String[] withId = new String[3];
		      String[] idArray = new String[] { new Random().nextInt(1000) + "" };
		      // place the id into the first position of the data array
		      System.arraycopy(idArray, 0, withId, 0, 1);
		      System.arraycopy(parsed, 0, withId, 1, 5);
		      parsed = withId;
		    }

		    return new TransferRequest(Integer.parseInt(parsed[1].trim()), Double.parseDouble(parsed[3].trim()));
		  }*/
	

	
	 private HttpEntity<TransferRequest> makeTransferEntity(TransferRequest transfer) {
		    HttpHeaders headers = new HttpHeaders();
		    headers.setContentType(MediaType.APPLICATION_JSON);
		    headers.setBearerAuth(AUTH_TOKEN);
		    HttpEntity<TransferRequest> entity = new HttpEntity<>(transfer, headers);
		    return entity;
		  }
	 
		private HttpEntity makeAuthEntity() {
		    HttpHeaders headers = new HttpHeaders();
		    headers.setBearerAuth(AUTH_TOKEN);
		    HttpEntity entity = new HttpEntity<>(headers);
		    return entity;
		  }
	

}
