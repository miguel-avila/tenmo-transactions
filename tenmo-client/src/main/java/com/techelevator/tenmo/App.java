package com.techelevator.tenmo;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.TenmoService;
import com.techelevator.tenmo.services.TenmoServiceException;
import com.techelevator.view.ConsoleService;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private TenmoService service;
//    private Scanner in;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.service = new TenmoService(API_BASE_URL);
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		try {
			mainMenu();
		} catch (TenmoServiceException e) {
			System.out.println(e.getMessage());
		}
	}

	private void mainMenu() throws TenmoServiceException {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		double balance = service.getBalance(currentUser).getBalance();
		System.out.println("Your current account balance is: $" + balance);
	}

	private void viewTransferHistory() {
		int transferId = printTransfers();		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
	}

	private void sendBucks() throws TenmoServiceException {
		int userToId = promptForUserId();
		double amountToSend = console.getUserInputDouble("Enter amount");
		boolean moneySent = service.sendMoney(currentUser, userToId, amountToSend);
		if (moneySent) {
			System.out.println("You just sent $" + amountToSend);
		} else {
			System.out.println("Something went wrong");
		}
	}		

	private void requestBucks() {
		// TODO Auto-generated method stub
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	 
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
	
	private int printTransfers() {
		System.out.println("-----------------------------------------");
		System.out.println("Transfers");
		System.out.println("ID\t\tFrom/To\t\tAmount");
		System.out.println("-----------------------------------------");
		
		Transfer[] transfers = service.getTransferHistory(currentUser);
		int currentUserId = currentUser.getUser().getId();
		for (int i = 0; i < transfers.length; i++) {
			String strAmount = String.format("%.2f", transfers[i].getAmount());
			if(transfers[i].getUserFromId() == currentUserId) {
				System.out.printf(transfers[i].getTransferId() + "\tTo: %15s \t$%8s \n", 
						transfers[i].getUserToName(), strAmount);
			} else {
				System.out.printf(transfers[i].getTransferId() + "\tFrom: %13s \t$%8s \n", 
						transfers[i].getUserFromName(), strAmount);
			}
		}
		System.out.println("-----------------------------------------\n");
		String promptForId = "Please enter transfer ID to view details (0 to cancel)";
		return console.getUserInputInteger(promptForId);
	}
	
	private int promptForUserId() {
		System.out.println("-------------------------------------");
		System.out.println("Users");
		System.out.println("ID\tName");
		System.out.println("-------------------------------------");
		
		User[] allUsers = service.getAllUsers(currentUser);
		int currentUserId = currentUser.getUser().getId();
		for (int i = 0; i < allUsers.length; i++) {
			if(allUsers[i].getId() != currentUserId) {
				System.out.println(allUsers[i].getId() + "\t" + allUsers[i].getUsername());
			}
		}
		System.out.println("------------------------------------\n");
		String promptForId = "Enter ID of user you are sending to (0 to cancel)";
		return console.getUserInputInteger(promptForId);
	}
}
