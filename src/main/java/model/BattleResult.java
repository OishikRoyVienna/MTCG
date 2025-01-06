package model;

import java.util.List;

public class BattleResult {

	private List<Battle> battles;
	private List<Card> removedCards;
	private String winner;
	private String loser;
	
	public List<Battle> getBattles() {
		return battles;
	}
	public void setBattles(List<Battle> battles) {
		this.battles = battles;
	}
	public List<Card> getRemovedCards() {
		return removedCards;
	}
	public void setRemovedCards(List<Card> removedCards) {
		this.removedCards = removedCards;
	}
	public String getWinner() {
		return winner;
	}
	public void setWinner(String winner) {
		this.winner = winner;
	}
	public String getLoser() {
		return loser;
	}
	public void setLoser(String loser) {
		this.loser = loser;
	}
	
	
}
