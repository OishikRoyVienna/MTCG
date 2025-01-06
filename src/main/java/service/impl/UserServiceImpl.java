package service.impl;

import java.sql.SQLException;
import java.util.List;

import dao.PackageDao;
import dao.UserDao;
import database.DatabaseHelper;
import model.BattleResult;
import model.Card;
import model.PlayerStat;
import model.User;
import model.UserDetails;
import service.UserService;
import utils.BattleGameStarter;

public class UserServiceImpl implements UserService{
	
	private UserDao userDao;
	private PackageDao packagedo;

	@Override
	public User login(User user) {
		
		try {
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			User usr = userDao.getUser(user.getUsername());
			if(user.getPassword().equals(usr.getPassword())) {
				usr.setToken(user.getUsername() +"-mtcgToken");
				userDao.updateUser(usr);
				return usr;
			}
			return null;
		}catch(SQLException e) {
			System.out.println("Error login "+e);
			return null;
		}
		
	}

	@Override
	public User getUser(String username) {
		try {
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			User usr = userDao.getUser(username);
			return usr;
		}catch(SQLException e) {
			System.out.println("Error getting user "+e);
			return null;
		}
	}

	@Override
	public boolean updateUser(String username, UserDetails userDetails) {
		
		try {
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			userDao.updateUserDetails(username,userDetails);
			return true;
		}catch(SQLException e) {
			System.out.println("Error update user "+e);
			return false;
		}
		
	}

	@Override
	public boolean registerUser(User user) {
		try {
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			userDao.createUser(user);
			return true;
		}catch(SQLException e) {
			System.out.println("Error register user "+e);
			return false;
		}
	}

	@Override
	public boolean validateToken(String token) {
		if(token == null || token.isEmpty()) {
			return false;
		}
		String username = token.split(" ")[1].split("-")[0];
		try {
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			User user = userDao.getUser(username);
			if(token.split(" ")[1].equals(user.getToken())) {
				return true;
			}
			
		}catch(SQLException e) {
			System.out.println("Error validate token "+e);
			
		}
		return false;
	}

	@Override
	public UserDetails getUserDetails(String username) {
		try {
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			return userDao.getUserDetails(username);
		}catch(SQLException e) {
			System.out.println("Error validate token "+e);
			
		}
		return null;
	}

	@Override
	public void battle(String username) {
		try {
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			String player = userDao.checkBattleRequest(username);
			if (!player.equals(username)) {
				startBattle(player, username);
			}
		} catch (SQLException e) {
			System.out.println("Error " + e);

		}
		
	}
	
	public void startBattle(String player1, String player2) {
		try {
			packagedo = new PackageDao(DatabaseHelper.getInstance().getConnection());
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			List<Card> player1Cards = packagedo.getCards(player1, true);
			List<Card> player2Cards = packagedo.getCards(player2, true);
			BattleResult br = new BattleResult();
			BattleGameStarter bs = new BattleGameStarter();
			System.out.println("battle started ");
			br = bs.startBattle(player1, player2, player1Cards, player2Cards);
			System.out.println("battle finish  "+br.getBattles().size());
			if(br.getRemovedCards() != null) {
				packagedo.updateUserCardAfterBattle(br.getWinner(), br.getLoser(), br.getRemovedCards());
				
			}
				packagedo.insertBattleDetails(br.getBattles());
				PlayerStat ps1 = userDao.getPlayerStats(player1);
				PlayerStat ps2 = userDao.getPlayerStats(player2);
				
				ps1.setGames(ps1.getGames()+1);
				ps2.setGames(ps2.getGames()+1);
				if(ps1.getUsername().equals(br.getWinner())) {
					ps1.setWins(ps1.getWins()+1);
					ps1.setRating(ps1.getRating()+3);
					ps2.setLoses(ps2.getLoses()+1);
					ps2.setRating(ps2.getRating()-5);
					
				}else if(ps2.getUsername().equals(br.getWinner())){
					ps2.setWins(ps2.getWins()+1);
					ps2.setRating(ps2.getRating()+3);
					ps1.setLoses(ps1.getLoses()+1);
					ps1.setRating(ps1.getRating()-5);
				} else {
					ps1.setDraws(ps1.getDraws()+1);
					ps2.setDraws(ps2.getDraws()+1);
				}
				userDao.updatePlayerStat(ps1);
				userDao.updatePlayerStat(ps2);
			
			
		} catch (SQLException e) {
			System.out.println("Error " + e);

		}
	}

	@Override
	public PlayerStat getPlayerStat(String username) {
		try {
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			return userDao.getPlayerStats(username);
		}catch(SQLException e) {
			System.out.println("Error validate token "+e);
			
		}
		return null;
	}

	@Override
	public List<PlayerStat> getScoreboard() {
		try {
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			return userDao.getScorecard();
		}catch(SQLException e) {
			System.out.println("Error validate token "+e);
			
		}
		return null;
	}

}
