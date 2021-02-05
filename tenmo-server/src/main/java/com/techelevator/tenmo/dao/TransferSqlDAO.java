package com.techelevator.tenmo.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Transfer;

@Component
public class TransferSqlDAO implements TransferDAO {

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
		// TODO Auto-generated method stub
		return null;
	}

	//Step 4.2 Creates a transfer including User IDs and the amount of TE Bucks.
	@Override
	public boolean create(int account_from, int account_to, double amount) {
		boolean transferCreated = false;
		String insertTransfer = "INSERT INTO transfers (transfer_type_id, "
				+ "transfer_status_id, account_from, account_to, amount) VALUES (2, 2, ?, ?, ?)";
		transferCreated = jdbcTemplate.update(insertTransfer, account_from, account_to, amount) == 1;
		
		return transferCreated;
	}

	@Override
	public boolean increaseBalance(int account_to, double amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean decreaseBalance(int account_from, double amount) {
		// TODO Auto-generated method stub
		return false;
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
