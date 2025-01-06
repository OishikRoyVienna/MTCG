package service;

import java.util.List;

import model.PlayerStat;
import model.User;
import model.UserDetails;

public interface UserService {
	
	public User login(User user);
	public User getUser(String username);
	public boolean updateUser(String username, UserDetails userDetails);
	public boolean registerUser(User user);
	public boolean validateToken(String token);
	public UserDetails getUserDetails(String username);
	public void battle(String username);
	public PlayerStat getPlayerStat(String username);
	public List<PlayerStat> getScoreboard();
}
