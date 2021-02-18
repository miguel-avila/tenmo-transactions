package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;
//import java.sql.Connection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

//import com.techelevator.tenmo.model.InsufficientFundsException;
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
		String sql = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, "
				+ "t.account_from, t.account_to, t.amount, uf.username AS user_from, "
				+ "ut.username AS user_to, uf.user_id AS user_from_id, ut.user_id AS user_to_id, "
				+ "tt.transfer_type_desc AS transfer_type, ts.transfer_status_desc AS transfer_status "
				+ "FROM transfers AS t JOIN accounts AS af ON t.account_from = af.account_id "
				+ "JOIN users AS uf ON af.user_id = uf.user_id "
				+ "JOIN accounts AS ato ON t.account_to = ato.account_id "
				+ "JOIN users AS ut ON ato.user_id = ut.user_id "
				+ "JOIN transfer_types AS tt ON t.transfer_type_id = tt.transfer_type_id "
				+ "JOIN transfer_statuses AS ts ON t.transfer_status_id = ts.transfer_status_id "
				+ "WHERE uf.user_id = ? or ut.user_id = ?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
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
		String sql = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, "
				+ "t.account_from, t.account_to, t.amount, uf.username AS user_from, "
				+ "ut.username AS user_to, uf.user_id AS user_from_id, ut.user_id AS user_to_id, "
				+ "tt.transfer_type_desc AS transfer_type, ts.transfer_status_desc AS transfer_status "
				+ "FROM transfers AS t JOIN accounts AS af ON t.account_from = af.account_id "
				+ "JOIN users AS uf ON af.user_id = uf.user_id "
				+ "JOIN accounts AS ato ON t.account_to = ato.account_id "
				+ "JOIN users AS ut ON ato.user_id = ut.user_id "
				+ "JOIN transfer_types AS tt ON t.transfer_type_id = tt.transfer_type_id "
				+ "JOIN transfer_statuses AS ts ON t.transfer_status_id = ts.transfer_status_id "
				+ "WHERE t.transfer_id = ?";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
		if(result.next()) {
			transfer = mapRowToTransfer(result);
		}
		return transfer;
	}

	//Step 4.2 Creates a transfer including User IDs and the amount of TE Bucks.
	@Override
	public boolean create(int accountFrom, int accountTo, double amount) {
		boolean transferCreated = false;
		String insertTransfer = "INSERT INTO transfers (transfer_type_id, "
				+ "transfer_status_id, account_from, account_to, amount) VALUES "
				+ "(?, ?, ?, ?, ?)";
		transferCreated = jdbcTemplate.update(insertTransfer, 
				TRANSFER_TYPE_SEND, TRANSFER_STATUS_APPROVED, accountFrom, accountTo, amount) == 1;
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
		transfer.setUserFromId(rs.getInt("user_from_id"));
		transfer.setUserToId(rs.getInt("user_to_id"));
		transfer.setUserFromName(rs.getString("user_from"));
		transfer.setUserToName(rs.getString("user_to"));
		transfer.setTransferType(rs.getString("transfer_type"));
		transfer.setTransferStatus(rs.getString("transfer_status"));
		return transfer;
	}
}
