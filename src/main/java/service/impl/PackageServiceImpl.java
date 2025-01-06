package service.impl;

import java.sql.SQLException;
import java.util.List;
import dao.PackageDao;
import dao.UserDao;
import database.DatabaseHelper;
import model.Card;
import model.Package;
import model.Trade;
import model.UserDetails;
import service.PackageService;

public class PackageServiceImpl implements PackageService {

	private PackageDao packageDao;
	private UserDao userDao;
	
	@Override
	public boolean createPackage(List<Card> cards) {
		try {
			packageDao = new PackageDao(DatabaseHelper.getInstance().getConnection());
			packageDao.createPackage(cards);
			return true;
		}catch(SQLException e) {
			System.out.println("Error create package "+e);
			return false;
		}
	}
	@Override
	public boolean acquirePackage(String username) {
		try {
			packageDao = new PackageDao(DatabaseHelper.getInstance().getConnection());
			List<Package> packages = packageDao.getAvailablePackages();
			if(packages.size()> 0) {
				Package p = packages.get(0);
				packageDao.updatePackage(p);
				List<Card> cards = packageDao.getCardDetails(p.getId());
				packageDao.insertToUserCard(username, cards);
				userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			    UserDetails userDetails = userDao.getUserDetails(username);
			    userDao.updateUserDetailsPackageCount(username, userDetails.getPackagesHold()+1);
				
			}
			return true;
		}catch(SQLException e) {
			System.out.println("Error create package "+e);
			return false;
		}
	}
	@Override
	public boolean isPackageAvailable() {
		try {
			packageDao = new PackageDao(DatabaseHelper.getInstance().getConnection());
			List<Package> packages = packageDao.getAvailablePackages();
			if(packages.size() > 0) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	@Override
	public boolean isEnoughMoney(String username) {
		try {
			userDao = new UserDao(DatabaseHelper.getInstance().getConnection());
			UserDetails userDetails =  userDao.getUserDetails(username);
			if(userDetails.getPackagesHold() < 4) {
				return true;
			}
			
			return false;
		}catch(SQLException e) {
			System.out.println("Error create package "+e);
			return false;
		}
	}
	@Override
	public List<Card> showCards(String username, boolean isdeck) {
		try {
			packageDao = new PackageDao(DatabaseHelper.getInstance().getConnection());
			return  packageDao.getCards(username,isdeck);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	@Override
	public boolean configureDeck(String username, List<Card> cards) {
		try {
			packageDao = new PackageDao(DatabaseHelper.getInstance().getConnection());
			packageDao.updateUserCardDeck(username,cards,true);
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	@Override
	public boolean createTrade(Trade trade) {
		try {
			packageDao = new PackageDao(DatabaseHelper.getInstance().getConnection());
			packageDao.createTrade(trade.getInitiator(),trade);
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	@Override
	public List<Trade> getAvailableTrades() {
		try {
			packageDao = new PackageDao(DatabaseHelper.getInstance().getConnection());
			return packageDao.getAvailableTrades();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	@Override
	public boolean updateTrade(Trade trade) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean deleteTrade(String username, String id) {
		try {
			packageDao = new PackageDao(DatabaseHelper.getInstance().getConnection());
			 packageDao.deleteTrade(username,id);
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

}
