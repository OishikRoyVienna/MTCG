package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Battle;
import model.Card;
import model.Trade;

public class PackageDao {

	private final Connection connection;

	public PackageDao(Connection connection) {
		this.connection = connection;
	}

	public void createPackage(List<Card> cards) throws SQLException {
		String query = "insert into Package (Name) values (?)";
		int maxCount = getMaxCount() + 1;
		int updates = 0;
		long key = -1;
		try (PreparedStatement stm = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)) {
			stm.setString(1, "Package "+maxCount );
			updates = stm.executeUpdate();
			
			if(updates > 0) {
				try(ResultSet rs = stm.getGeneratedKeys()){
					if(rs.next()) {
						key = rs.getLong(1);
					}
				}
			}
		}
		for (Card card : cards) {
		    query = "insert into Card (Name,Damage,PackageId,Id) values (?,?,?,?)";
			try (PreparedStatement stm = connection.prepareStatement(query)) {
				stm.setString(1, card.getName());
				stm.setInt(2, card.getDamage());
				stm.setLong(3, key );
				stm.setString(4, card.getId());
				updates = stm.executeUpdate();

			}
		}
	}
	
	public List<model.Package> getAvailablePackages() throws SQLException {
		String query = "select * from Package where Available = true";
		List<model.Package> packageList = new ArrayList<model.Package>();
		try (Statement st = connection.createStatement()) {
			ResultSet rs = st.executeQuery(query);
			while(rs.next()) {
				model.Package pkg = new model.Package();
				pkg.setId(rs.getInt("Id"));
				packageList.add(pkg);
			}
		}
		return packageList;
	}
	
	public int getMaxCount() throws SQLException {
		String query = "select count(*) from Package";
		
		try (Statement st = connection.createStatement()) {
			ResultSet rs = st.executeQuery(query);
			if(rs.next()) {
				return rs.getInt(1);
			}
		}
		return 0;
	}
	
	public void updatePackage(model.Package pkg) throws SQLException {
		String query = "update Package set Available = false where Id = ?";
		try (PreparedStatement stm = connection.prepareStatement(query)) {
			stm.setInt(1, pkg.getId());
			stm.executeUpdate();
		}
	}
	
	public List<Card> getCardDetails(int packageId) throws SQLException{
		String query = "select * from Card where PackageId = ? "; 
		List<Card> cardList = new ArrayList<>();
		try (PreparedStatement st = connection.prepareStatement(query)) {
			st.setInt(1, packageId);
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				Card card = new Card();
				card.setId(rs.getString("Id"));
				card.setName(rs.getString("Name"));
				card.setDamage(rs.getInt("Damage"));
				cardList.add(card);
			}
		} 
		return cardList;
	}
	
	public void insertToUserCard(String username, List<Card> cards) throws SQLException {
		
		for (Card card : cards) {
		    String query = "insert into UserCard (UserId,CardId) values (?,?)";
			try (PreparedStatement stm = connection.prepareStatement(query)) {
				stm.setString(1, username);
				stm.setString(2, card.getId());
				stm.executeUpdate();

			} 
		}
	}
	
	public List<Card> getCards(String username, boolean isDeck) throws SQLException{
		String query;
		if(isDeck) {
			query = "select * from Card where Id in (select CardId from UserCard where UserId = ? and Deck = ? ) "; 
		}else {
			query = "select * from Card where Id in (select CardId from UserCard where UserId = ? ) "; 
		}
		
		List<Card> cardList = new ArrayList<>();
		try (PreparedStatement st = connection.prepareStatement(query)) {
			st.setString(1, username);
			if(isDeck) {
				st.setBoolean(2, isDeck);
			}
			
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				Card card = new Card();
				card.setId(rs.getString("Id"));
				card.setName(rs.getString("Name"));
				card.setDamage(rs.getInt("Damage"));
				cardList.add(card);
			}
		} 
		return cardList;
	}
	
	public void updateUserCardDeck(String username, List<Card> cards, boolean isDeck) throws SQLException {
		connection.setAutoCommit(false);
		int count = 0;
		for (Card card : cards) {
			String query = "update UserCard set Deck = ? where  UserId = ? and CardId = ?";
			try (PreparedStatement stm = connection.prepareStatement(query)) {

				stm.setBoolean(1, isDeck);
				stm.setString(2, username);
				stm.setString(3, card.getId());
				if(stm.executeUpdate() >=1 ) {
					count++;
				}

			}
		}
		
		if(count == cards.size()) {
			connection.commit();
			connection.setAutoCommit(true);
		}else {
			throw new SQLException("Error updating cards");
		}
	}
	
	public void updateUserCardAfterBattle(String winner,String loser, List<Card> cards) throws SQLException {
		for (Card card : cards) {
			String query = "update UserCard set Deck = false, UserId=? where  UserId = ? and CardId = ?";
			try (PreparedStatement stm = connection.prepareStatement(query)) {
				stm.setString(1, winner);
				stm.setString(2, loser);
				stm.setString(3, card.getId());
				stm.executeUpdate();
			}
		}
	}
	
	public void insertBattleDetails(List<Battle> battles) throws SQLException {
		int battleId = getMaxCountBattle()+1;
		for (Battle battle : battles) {
			String query = "insert into Battle(Battle,Round,Player1,Player2,Player1Card,Player2Card,Log,Winner,RoundStatus,BattleStatus) "
					+ "Values (?,?,?,?,?,?,?,?,?,?)";
			try (PreparedStatement stm = connection.prepareStatement(query)) {
				stm.setInt(1, battleId);
				stm.setInt(2, battle.getRound());
				stm.setString(3, battle.getPlayer1());
				stm.setString(4, battle.getPlayer2());
				stm.setString(5, battle.getPlayer1Card());
				stm.setString(6, battle.getPlayer2Card());
				stm.setString(7, battle.getLog());
				stm.setString(8, battle.getWinner());
				stm.setString(9, battle.getRoundStatus());
				stm.setString(10, "COMPLETED");
				stm.executeUpdate();
			}
		}
	}
	
	public int getMaxCountBattle() throws SQLException {
		String query = "select max(Battle) from Battle";
		
		try (Statement st = connection.createStatement()) {
			ResultSet rs = st.executeQuery(query);
			if(rs.next()) {
				return rs.getInt(1);
			}
		}
		return 0;
	}
	
	public void createTrade(String username, Trade trade) throws SQLException {

		String query = "insert into Trade (Id,CardId,Type,MinimumDamage,Status,Initiator) values (?,?,?,?,'Pending',?)";
		try (PreparedStatement stm = connection.prepareStatement(query)) {
			stm.setString(1, trade.getId());
			stm.setString(2, trade.getCardId());
			stm.setString(3, trade.getType());
			stm.setInt(4, trade.getMnimumDamage());
			stm.setString(5, username);
			stm.executeUpdate();

		}
	}
	
	public List<Trade> getAvailableTrades() throws SQLException{
		String query = "select * from Trade where Status = 'Pending' "; 
		List<Trade> tradeList = new ArrayList<>();
		try (PreparedStatement st = connection.prepareStatement(query)) {
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				Trade trade = new Trade();
				trade.setId(rs.getString("Id"));
				trade.setCardId(rs.getString("CardId"));
				trade.setType(rs.getString("Type"));
				trade.setMnimumDamage(rs.getInt("MinimumDamage"));
				trade.setInitiator(rs.getString("Initiator"));
				tradeList.add(trade);
			}
		} 
		return tradeList;
	}
	
	public void deleteTrade(String username, String id) throws SQLException {

		String query = "Delete from Trade where Initiator= ? and Id = ? ";
		try (PreparedStatement stm = connection.prepareStatement(query)) {
			stm.setString(1, username);
			stm.setString(2, id);
			stm.executeUpdate();
		}
	}

}
