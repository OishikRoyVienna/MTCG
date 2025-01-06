package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Battle;
import model.BattleResult;
import model.Card;

public class BattleGameStarter {
	
	private int maxRounds= 100;

	public BattleResult startBattle(String player1, String player2, List<Card> player1Cards, List<Card> player2Cards){
		int round = 1;
		
		
		List<Battle> battles = new ArrayList<>();
		
		while(round <= maxRounds && player1Cards.size() > 0 && player2Cards.size() > 0) {
			String log = "";
			Card player1Card = getRandomCard(player1Cards);
			Card player2Card = getRandomCard(player2Cards);
			Battle b = new Battle();
			
			log = player1 + " got " + player1Card.getName() + " and "+ player2 + " got " + player2Card.getName() +" | ";
			
			int damage1 =damageCalculator(player1Card, player2Card);
			int damage2 = damageCalculator(player2Card, player1Card);
			
			if(damage1 > damage2) {
				log = log + player1 + " wins round "+ round;
				player2Cards.remove(player2Card);
				b.setRoundStatus(player1 + " Wins");
			}else if (damage1 < damage2) {
				log = log + player2 + " wins round "+ round;
				player1Cards.remove(player1Card);
				b.setRoundStatus(player2 + " Wins");
			}else {
				log = log +" round draws";
				b.setRoundStatus("Draw");
			}
			
			
			b.setRound(round);
			b.setPlayer1(player1);
			b.setPlayer2(player2);
			b.setPlayer1Card(player1Card.getId());
			b.setPlayer2Card(player2Card.getId());
			b.setLog(log);
			battles.add(b);
			
			round++;
		}
		
		BattleResult br = new BattleResult();
		String battlewinner; 
		if(player1Cards.size() > player2Cards.size()) {
			battlewinner = player1;
			br.setWinner(player1);
			br.setLoser(player2);
			br.setRemovedCards(player2Cards);
		}else if(player1Cards.size() < player2Cards.size()) {
			battlewinner = player2;
			br.setWinner(player2);
			br.setLoser(player1);
			br.setRemovedCards(player1Cards);
		}else {
			battlewinner = "DRAW";
		}
		for (Battle battle : battles) {
			battle.setWinner(battlewinner);
		}
		br.setBattles(battles);
			
		return br;
		
	}
	
	private Card getRandomCard(List<Card> cards) {
		if(cards.isEmpty()) {
			return null;
		}
		Random r = new Random();
		return cards.get(r.nextInt(cards.size()));
	}
	
	private int damageCalculator(Card player1, Card player2) {
		
		if(player1.getName().contains("WaterSpell") && player2.getName().contains("FireSpell")) {
			return player1.getDamage() * 2;
		} else if (player1.getName().contains("FireSpell") && player2.getName().contains("RegularSpell")) {
			return player1.getDamage() * 2;
		} else if (player1.getName().contains("RegularSpell") && player2.getName().contains("WaterSpell")) {
			return player1.getDamage() * 2;
		} else if(player1.getName().contains("FireSpell") && player2.getName().contains("WaterSpell")) {
			return player1.getDamage() / 2;
		} else if(player1.getName().contains("Dragon") && player2.getName().contains("WaterGoblin")) {
			return Integer.MAX_VALUE;
		} else if(player1.getName().contains("Wizzard") && player2.getName().contains("Ork")) {
			return Integer.MAX_VALUE;
		} else if(player1.getName().contains("WaterSpell") && player2.getName().contains("Knight")) {
			return Integer.MAX_VALUE;
		} else if(player1.getName().contains("Spell") && player2.getName().contains("Kraken")) {
			return 0;
		} else if(player1.getName().contains("FireElves") && player2.getName().contains("Dragons")) {
			return Integer.MAX_VALUE;
		} else {
			return player1.getDamage();
		}
		
	}
}
