package com.techelevator.tenmo.controller;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferRequest;

import com.techelevator.tenmo.model.IllegalTransferRequest;
@RestController
@PreAuthorize("isAuthenticated()")
public class TenmoController {

	private UserDAO userDAO;
	private TransferDAO transferDAO;

	public TenmoController(UserDAO userDAO, TransferDAO transferDAO) {
		this.userDAO = userDAO;
		this.transferDAO = transferDAO;
	}

	@RequestMapping(path = "/user/me/balance", method = RequestMethod.GET)
	public Account getBalanceForMe(Principal principal) {
		String username = principal.getName();
		int userId = this.userDAO.findIdByUsername(username);
		return userDAO.getAccountByUserId(userId);
	}

	@RequestMapping(path = "/transfers", method = RequestMethod.GET)
	public List<Transfer> getTransferHistory(Principal principal) {
		String username = principal.getName();
		int userId = this.userDAO.findIdByUsername(username);
		return transferDAO.findAll(userId);
	}

	// OPTIONAL pendingtransfers
	@RequestMapping(path = "/transfers/pendingtransfers", method = RequestMethod.GET)
	public List<Transfer> getPendingTransfers(Principal principal) {
		String username = principal.getName();
		int userId = this.userDAO.findIdByUsername(username);
		return null;
	}
	// sendbucks--use user-id to "put" aka update balance transfer from and
	// to--helper methods
	
	@RequestMapping(path = "/transfers/sendmoney", method = RequestMethod.POST)
	public boolean sendMoney(@Valid @RequestBody TransferRequest request, Principal principal) throws IllegalTransferRequest {
		String username = principal.getName();
		int userId = this.userDAO.findIdByUsername(username);
		if (request.getFromUserId() != userId) {
			//TODO throw custom exception that throws 403 forbidden
			throw new IllegalTransferRequest();
		}
		int accountFrom = userDAO.getAccountByUserId(request.getFromUserId()).getAccountId();
		int accountTo = userDAO.getAccountByUserId(request.getToUserId()).getAccountId();
		double amount = request.getAmount();
		//increase/decrease balance
		boolean balanceIncreased = false;
		boolean transferCreated = false;
		boolean balanceDecreased = this.transferDAO.decreaseBalance(accountFrom, amount);
		if(balanceDecreased) {
			balanceIncreased = this.transferDAO.increaseBalance(accountTo, amount);
			transferCreated = this.transferDAO.create(accountFrom, accountTo, amount);
		}
		if(transferCreated && balanceDecreased && balanceIncreased) {
			return true;
		}
		return false;
	}

	// requestTransfer -- get transfer by transfer-id to -- helper methods
	@RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
	public Transfer getTransferWithId(@PathVariable int id, Principal principal) throws IllegalTransferRequest {
		String username = principal.getName();
		int userId = this.userDAO.findIdByUsername(username);
		Transfer transfer = transferDAO.getTransferById(id);
		if (transfer.getUserFromId() != userId && transfer.getUserToId() != userId) {
			//TODO throw custom exception that throws 403 forbidden
			throw new IllegalTransferRequest();
		} else {
			return transfer;
		}
	}

}
