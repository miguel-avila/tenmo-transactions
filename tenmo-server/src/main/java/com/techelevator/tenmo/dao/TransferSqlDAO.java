package com.techelevator.tenmo.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.InsufficientFundsException;
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
	
	@Override
	public Transfer transfer(int userId, Transfer transfer) throws Exception {
		int accountTo = transfer.getAccountTo();
		int accountFrom = userId;
		double amount = transfer.getAmount();
		UserSqlDAO userDAO = new UserSqlDAO(jdbcTemplate);
//		Connection con = this.jdbcTemplate.getDataSource().getConnection();
		try {
//			con.setAutoCommit(false);
			double balance = userDAO.findBalanceByUserId(userId);
			if (balance < amount) {
				// TODO write a custom exception that uses a 400 status exception
				throw new InsufficientFundsException();
			}
			userDAO.updateBalance(accountTo, accountFrom, amount);
			// TODO Insert record and read it back
			String insertTransfer = "INSERT INTO transfers (transfer_type_id,transfer_status_id,account_From, account_To, amount) "
					+ "VALUES ('2','2', ?, ?, ?);";
			jdbcTemplate.update(insertTransfer, accountFrom, accountTo, amount);
			
//			con.commit();
		} catch (Exception ex) {
//			con.rollback();
			throw ex;
		} finally {
//			con.setAutoCommit(true);
		}
		return transfer;
	}

	//Step 4.2 Creates a transfer including User IDs and the amount of TE Bucks.
	@Override
	public boolean create(int accountFrom, int accountTo, double amount) {
		boolean transferCreated = false;
		String insertTransfer = "INSERT INTO transfers (transfer_type_id, "
				+ "transfer_status_id, account_from, account_to, amount) VALUES "
				+ "(" + TRANSFER_TYPE_SEND + ", " + TRANSFER_STATUS_APPROVED + ", ?, ?, ?)";
		transferCreated = jdbcTemplate.update(insertTransfer, accountFrom, accountTo, amount) == 1;
		
		return transferCreated;
	}

	//Step 4.3 Increases receiver's account balance by the amount of the transfer
	@Override
	public boolean increaseBalance(int accountTo, double amount) {
		boolean balanceIncreased = false;
		String addMoney = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
		balanceIncreased = jdbcTemplate.update(addMoney, amount, accountTo) == 1;
		
		return balanceIncreased;
	}

	//Step 4.4 Decreases sender's account balance by the amount of the transfer
	@Override
	public boolean decreaseBalance(int accountFrom, double amount) {
		boolean balanceDecreased = false;
		String checkBalance = "SELECT balance FROM accounts WHERE account_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(checkBalance, accountFrom);
		double balance = 0.0;
		if(result.next()) {
			balance = result.getDouble("balance");
		}
		String substractMoney = "UPDATE accounts SET balance = ? WHERE account_id = ?";
		if(balance >= amount) {
			balance -= amount;
			balanceDecreased = jdbcTemplate.update(substractMoney, balance, accountFrom) == 1;
		}
		
		return balanceDecreased;
	}

	//helper method
	//creates Transfer object from SQL row
	private Transfer mapRowToTransfer(SqlRowSet rs) {
		Transfer transfer = new Transfer();
		transfer.setTransferId(rs.getInt("transfer_id"));
		transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
		transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
		transfer.setAccountFrom(rs.getInt("account_from"));
		transfer.setAccountTo(rs.getInt("account_to"));
		transfer.setAmount(rs.getDouble("amount"));
		return transfer;
	}
	
}
