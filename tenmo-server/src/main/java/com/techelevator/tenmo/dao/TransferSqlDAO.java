package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Transfer;

@Component
public class TransferSqlDAO implements TransferDAO {

	private static final int TRANSFER_TYPE_SEND = 2;
	private static final int TRANSFER_STATUS_APPROVED = 2;
	
	private JdbcTemplate jdbcTemplate;
	
	public TransferSqlDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	//Step 5. Finds transfers user have sent or received
	@Override
	public List<Transfer> findAll(int userId) {
		List<Transfer> transfers = new ArrayList<>();
		String sql = "SELECT * FROM transfers";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
		while(results.next()) {
			Transfer transfer = mapRowToTransfer(results);
			transfers.add(transfer);
		}
		return transfers;
	}

	//Step 6. Retrieves the details of a transfer based upon the transfer ID
	@Override
	public Transfer getTransferById(int id) {
		Transfer transfer = null;
		String sql = "SELECT * FROM transfers WHERE transfer_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
		if(result.next()) {
			transfer = mapRowToTransfer(result);
		}
		return transfer;
	}

	//Step 4.2 Creates a transfer including User IDs and the amount of TE Bucks.
	@Override
	public boolean create(int account_from, int account_to, double amount) {
		boolean transferCreated = false;
		String insertTransfer = "INSERT INTO transfers (transfer_type_id, "
				+ "transfer_status_id, account_from, account_to, amount) VALUES "
				+ "(" + TRANSFER_TYPE_SEND + ", " + TRANSFER_STATUS_APPROVED + ", ?, ?, ?)";
		transferCreated = jdbcTemplate.update(insertTransfer, account_from, account_to, amount) == 1;
		
		return transferCreated;
	}

	//Step 4.3 Increases receiver's account balance by the amount of the transfer
	@Override
	public boolean increaseBalance(int account_to, double amount) {
		boolean balanceIncreased = false;
		String addMoney = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
		balanceIncreased = jdbcTemplate.update(addMoney, amount, account_to) == 1;
		
		return balanceIncreased;
	}

	//Step 4.4 Decreases sender's account balance by the amount of the transfer
	@Override
	public boolean decreaseBalance(int account_from, double amount) {
		boolean balanceDecreased = false;
		String checkBalance = "SELECT balance FROM accounts WHERE account_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(checkBalance, account_from, double.class);
		double balance = 0.0;
		if(result.next()) {
			balance = result.getDouble("balance");
		}
		String substractMoney = "UPDATE accounts SET balance = ? WHERE account_id = ?";
		if(balance >= amount) {
			balance -= amount;
			balanceDecreased = jdbcTemplate.update(substractMoney, balance, account_from) == 1;
		}
		
		return balanceDecreased;
	}

	//helper method
	//creates Transfer object from SQL row
	private Transfer mapRowToTransfer(SqlRowSet rs) {
		Transfer transfer = new Transfer();
		transfer.setTransfer_id(rs.getLong("transfer_id"));
		transfer.setTransfer_type_id(rs.getInt("transfer_type_id"));
		transfer.setTransfer_status_id(rs.getInt("transfer_status_id"));
		transfer.setAccount_from(rs.getInt("account_from"));
		transfer.setAccount_to(rs.getInt("account_to"));
		transfer.setAmount(rs.getDouble("amount"));
		return transfer;
	}
	
}
