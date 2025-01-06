package service;

import java.util.List;

import model.Card;
import model.Trade;

public interface PackageService {

	public boolean createPackage(List<Card> cards);
	public boolean acquirePackage(String username);
	public boolean isPackageAvailable();
	public boolean isEnoughMoney(String username);
	public List<Card> showCards(String username, boolean isdeck);
	public boolean configureDeck(String username,List<Card> cards);
	public boolean createTrade(Trade trade);
	public List<Trade> getAvailableTrades();
	public boolean updateTrade(Trade trade);
	public boolean deleteTrade(String username, String id);
}
