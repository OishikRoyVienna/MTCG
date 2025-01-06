package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.PlayerStat;
import model.User;
import model.UserDetails;

public class UserDao {

	private final Connection connection;

	public UserDao(Connection connection) {
		this.connection = connection;
	}

	public void createUser(User user) throws SQLException {
		String query = "insert into Users (Username, Password) values (?,?)";
		try (PreparedStatement stm = connection.prepareStatement(query)) {
			stm.setString(1, user.getUsername());
			stm.setString(2, user.getPassword());
			stm.executeUpdate();
		}
		createUserDetails(user);
		createPlayerStat(user);
	}
	
	public User getUser(String usernmae) throws SQLException {
		String query = "select * from Users where Username = ?";
		
		try (PreparedStatement st = connection.prepareStatement(query)) {
			st.setString(1, usernmae);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				User user = new User(rs.getString("Username"), rs.getString("Password"));
				user.setToken(rs.getString("Token"));
				return user;
			}
		}
		return null;
	}
	
	public void updateUser(User user) throws SQLException {
		String query = "update Users set Token = ? where Username = ?";
		try (PreparedStatement stm = connection.prepareStatement(query)) {
			stm.setString(1, user.getToken());
			stm.setString(2, user.getUsername());
			stm.executeUpdate();
		}
	}
	
	public void updateUserDetails(String username, UserDetails userDetails) throws SQLException {
		String query = "update UserDetails set Bio = ?, Image = ?, Name = ? where Username = ?";
		try (PreparedStatement stm = connection.prepareStatement(query)) {
			stm.setString(1, userDetails.getBio());
			stm.setString(2, userDetails.getImage());
			stm.setString(3, userDetails.getName());
			stm.setString(4, username);
			stm.executeUpdate();
		}
	}
	
	public UserDetails getUserDetails(String usernmae) throws SQLException {
		String query = "select * from UserDetails where Username = ?";
		
		try (PreparedStatement st = connection.prepareStatement(query)) {
			st.setString(1, usernmae);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				UserDetails user = new UserDetails();
				user.setBio(rs.getString("Bio"));
				user.setImage(rs.getString("Image"));
				user.setPackagesHold(rs.getInt("Packageshold"));
				user.setName(rs.getString("Name"));
				return user;
			}
		}
		return null;
	}
	
	public void updateUserDetailsPackageCount(String username, int count) throws SQLException {
		String query = "update UserDetails set Packageshold = ? where Username = ?";
		try (PreparedStatement stm = connection.prepareStatement(query)) {
			stm.setInt(1, count);
			stm.setString(2, username);
			stm.executeUpdate();
		}
	}
	
	public void createUserDetails(User user) throws SQLException {
		String query = "insert into UserDetails (Username) values (?)";
		try (PreparedStatement stm = connection.prepareStatement(query)) {
			stm.setString(1, user.getUsername());
			stm.executeUpdate();
		}
	}
	
	public void createPlayerStat(User user) throws SQLException {
		String query = "insert into PlayerStat (Games,Player,Rating,Wins,Loses) values (0,?,100,0,0)";
		try (PreparedStatement stm = connection.prepareStatement(query)) {
			stm.setString(1, user.getUsername());
			stm.executeUpdate();
		}
	}
	
	public synchronized String checkBattleRequest(String usernmae) throws SQLException {
		System.out.println("inside battle request "+usernmae);
		String query = "select * from BattleRequest where Status = 'Pending'";
		//connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
		connection.setAutoCommit(true);
		try (PreparedStatement st = connection.prepareStatement(query)) {
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				String player1 = rs.getString("Player1");
				String query2 = "update BattleRequest set Player2 = ? , Status = 'Accepted' where Player1 = ? and Status = ?";
				try (PreparedStatement stm = connection.prepareStatement(query2)) {
					stm.setString(1, usernmae);
					stm.setString(2, player1);
					stm.setString(3, "Pending");
					System.out.println("commitiing   "+usernmae);
					stm.executeUpdate();
				}
				return player1;
			}else {
				String query2 = "insert into BattleRequest (Player1, Status) values (?,?)";
				try (PreparedStatement stm = connection.prepareStatement(query2)) {
					stm.setString(1, usernmae);
					stm.setString(2, "Pending");
					stm.executeUpdate();
					System.out.println("commitiing otherrrr  "+usernmae);
				}
				return usernmae;
			}
		}
	}

	public void updatePlayerStat(PlayerStat playerStat) throws SQLException {
		String query = "update PlayerStat set Games =?,Rating =? ,Wins=? ,Loses = ?,Draws=? where Player = ?";
		try (PreparedStatement stm = connection.prepareStatement(query)) {
			stm.setInt(1, playerStat.getGames());
			stm.setInt(2, playerStat.getRating());
			stm.setInt(3, playerStat.getWins());
			stm.setInt(4, playerStat.getLoses());
			stm.setInt(5, playerStat.getDraws());
			stm.setString(6, playerStat.getUsername());
			stm.executeUpdate();
		}
	}
	
	public PlayerStat getPlayerStats(String usernmae) throws SQLException {
		String query = "select * from PlayerStat where Player = ?";
		
		try (PreparedStatement st = connection.prepareStatement(query)) {
			st.setString(1, usernmae);
			ResultSet rs = st.executeQuery();
			if(rs.next()) {
				PlayerStat playerstat = new PlayerStat();
				
				playerstat.setGames(rs.getInt("Games"));
				playerstat.setRating(rs.getInt("Rating"));
				playerstat.setWins(rs.getInt("Wins"));
				playerstat.setLoses(rs.getInt("Loses"));
				playerstat.setDraws(rs.getInt("Draws"));
				playerstat.setUsername(rs.getString("Player"));
				return playerstat;
			}
		}
		return null;
	}
	
	public List<PlayerStat> getScorecard() throws SQLException {
		String query = "select * from PlayerStat order by Rating desc";
		
		List<PlayerStat> list = new ArrayList<>();
		try (PreparedStatement st = connection.prepareStatement(query)) {
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				PlayerStat playerstat = new PlayerStat();
				
				playerstat.setRating(rs.getInt("Rating"));
				playerstat.setUsername(rs.getString("Player"));
				list.add(playerstat);
			}
		}
		return list;
	}
}
