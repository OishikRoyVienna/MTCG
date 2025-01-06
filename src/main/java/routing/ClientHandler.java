package routing;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import http.ContentType;
import http.HttpStatus;
import model.Card;
import model.PlayerStat;
import model.Trade;
import model.User;
import model.UserDetails;
import server.Request;
import server.Response;
import server.Service;
import service.PackageService;
import service.UserService;
import service.impl.PackageServiceImpl;
import service.impl.UserServiceImpl;

public class ClientHandler implements Service {

    private UserService userService;
    private PackageService packageService;
    
    public ClientHandler() {}

    @Override
    public Response handleRequest(Request request) {
        String method = request.getMethod().name();  // Verwende .name(), um den String der Methode zu bekommen
        String path = request.getPathname();  // Verwende die vorhandene getPath()-Methode
        System.out.println("Request received: " + request.getMethod().name() + " " + request.getPathname());

        // Routing basierend auf Methode und Pfad
        if("GET".equals(method)) {
        	if("/".equals(path)) {
        		return processWelcome();
        	}else if("/cards".equals(path)) {
        		String token = request.getHeaderMap().getHeader("Authorization");
        		return showCards(token,false);
        	}else if("/deck".equals(path)) {
        		String token = request.getHeaderMap().getHeader("Authorization");
        		return showCards(token,true);
        	}else if(path.contains("/users/") && request.getPathParts().size()==2) {
        		String token = request.getHeaderMap().getHeader("Authorization");
        		return getUserDetails(token,request.getPathParts().get(1));
        	}else if(path.contains("/stats")) {
        		String token = request.getHeaderMap().getHeader("Authorization");
        		return getPlayerStat(token);
        	}else if(path.contains("/scoreboard")) {
	        	String token = request.getHeaderMap().getHeader("Authorization");
	        	return getScorecard(token);
        	}else if(path.contains("/tradings")) {
	        	String token = request.getHeaderMap().getHeader("Authorization");
	        	return getAvailableTrades(token);
        	}
        	
        }else if ("POST".equals(method)){
        	if("/users".equals(path)) {
        		return processRegister(request.getBody());
        	}else if("/sessions".equals(path)) {
        		return processLogin(request.getBody());
        	}else if("/packages".equals(path)) {
        		String token = request.getHeaderMap().getHeader("Authorization");
        		return processPackage(token,request.getBody());
        	}else if("/transactions/packages".equals(path)) {
        		String token = request.getHeaderMap().getHeader("Authorization");
        		return acquirePackage(token,request.getBody());
        	}else if("/battles".equals(path)) {
        		String token = request.getHeaderMap().getHeader("Authorization");
        		return battleRound(token);
        	}else if(path.contains("/tradings")) {
	        	String token = request.getHeaderMap().getHeader("Authorization");
	        	return createTrade(token,request.getBody());
        	}
        	
        }else if ("PUT".equals(method)) {
        	if("/deck".equals(path)) {
        		String token = request.getHeaderMap().getHeader("Authorization");
        		return configureDeck(token,request.getBody());
        	}else if(path.contains("/users/") && request.getPathParts().size()==2) {
        		String token = request.getHeaderMap().getHeader("Authorization");
        		return updateUserDetails(token,request.getPathParts().get(1), request.getBody());
        	}
        	
        }else if ("DELETE".equals(method)) {
        	 if(path.contains("/tradings/") && request.getPathParts().size()==2) {
        		String token = request.getHeaderMap().getHeader("Authorization");
        		return deleteTrade(token,request.getPathParts().get(1));
        	}
        }
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Not Found"+"\r\n");
    }

    private Response processWelcome() {
        return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Welcome to the MTCG Server!"+"\r\n");
    }

    public Response processRegister(String body) {
        try {
            JSONObject json = new JSONObject(body);
            String username = json.getString("Username");
            String password = json.getString("Password");

            if (username == null || password == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Missing username or password"+"\r\n");
            }
            
            userService = new UserServiceImpl();
            User user = userService.getUser(username);
            
            if(null != user) {
            	return new Response(HttpStatus.CONFLICT, ContentType.PLAIN_TEXT, "Conflict: User already exists"+"\r\n");
            }
            
            user = new User(username, password);
            userService.registerUser(user);

            return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "User registered successfully"+"\r\n");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
    }

    private Response processLogin(String body) {
        try {
            JSONObject json = new JSONObject(body);
            String username = json.getString("Username");
            String password = json.getString("Password");

            if (username == null || password == null) {
                return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Missing username or password"+"\r\n");
            }

            userService = new UserServiceImpl();
            User user = userService.getUser(username);
            if (user == null) {
                return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Not Found: User does not exist"+"\r\n");
            }
            
            user = new User(username, password);
            // Verify password
            user = userService.login(user);
            if (null == user) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized: Invalid username or password"+"\r\n");
            }

            return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "OK: Login successful. Token: " + user.getToken()+"\r\n");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
    }
    
    private Response processPackage(String token,String body) {
        try {
        	List<Card> cards = new ArrayList<>();
        	userService = new UserServiceImpl();
        	if(userService.validateToken(token)) {
        		JSONArray jsonArray = new JSONArray(body);
        		for(int i = 0; i<jsonArray.length(); i++) {
        			JSONObject json = jsonArray.getJSONObject(i);
        			Card card = new Card(json.getString("Name"), json.getInt("Damage"), json.getString("Id"));
        			cards.add(card);
        		}
        		packageService = new PackageServiceImpl();
        		packageService.createPackage(cards);
        		return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Package created successfully"+"\r\n");
        	}
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	
        }catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
        
    }
    
    private Response acquirePackage(String token,String body) {
        try {
        	userService = new UserServiceImpl();
        	packageService = new PackageServiceImpl();
        	if(userService.validateToken(token)) {
        		String username = token.split(" ")[1].split("-")[0];
        		if(!packageService.isEnoughMoney(username)) {
        			return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Not enough money"+"\r\n");
        		}
        		if(!packageService.isPackageAvailable()) {
        			return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "No package available"+"\r\n");
        		}
        		packageService.acquirePackage(username);
        		return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Package acquired successfully"+"\r\n");
        	}
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	
        }catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
        
    }

    private Response showCards(String token, boolean deck) {
        try {
        	userService = new UserServiceImpl();
        	packageService = new PackageServiceImpl();
        	if(userService.validateToken(token)) {
        		String username = token.split(" ")[1].split("-")[0];
        		List<Card> cards = packageService.showCards(username,deck);
        		JSONArray jsonArray  = new JSONArray();
        		for (Card card : cards) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("Id", card.getId());
					jsonObject.put("Name", card.getName());
					jsonObject.put("Damage", card.getDamage());
					jsonArray.put(jsonObject);
				}
        		
        		return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, jsonArray.toString()+"\r\n");
        	}
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	
        }catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
        
    }
    
    private Response configureDeck(String token,String body) {
        try {
        	userService = new UserServiceImpl();
        	packageService = new PackageServiceImpl();
        	if(userService.validateToken(token)) {
        		String username = token.split(" ")[1].split("-")[0];
        		List<Card> cards = new ArrayList<Card>();
        		JSONArray jsonArray = new JSONArray(body);
        		for(int i = 0; i<jsonArray.length(); i++) {
        			Card card = new Card();
        			card.setId(jsonArray.getString(i));
        			cards.add(card);
        		}
        		if(cards.size() != 4) {
        			return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Four card should select"+"\r\n");
        		}
        		if(packageService.configureDeck(username, cards)) {
        			return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, "Deck Created"+"\r\n");
        		}
        		
        	}
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	
        }catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
        
    }
    
    public Response updateUserDetails(String token, String pathValue, String requestBody) {
        try {
        	if(token == null || token.isEmpty()) {
        		return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	}
        	String username = token.split(" ")[1].split("-")[0];
        	
        	if(!pathValue.equals(username)) {
        		return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	}
        	
        	userService = new UserServiceImpl();
        	if(userService.validateToken(token)) {
        		JSONObject json = new JSONObject(requestBody);
        		UserDetails ud = new UserDetails();
        		ud.setBio(json.getString("Bio"));
        		ud.setName(json.getString("Name"));
        		ud.setImage(json.getString("Image"));
        		
        		if(userService.updateUser(username, ud)) {
        			return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "User updated successfully"+"\r\n"); 
        		}

        	}
        	
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
    }
    
  
    
    private Response getUserDetails(String token, String pathValue) {
    	 try {
         	if(token == null || token.isEmpty()) {
         		return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
         	}
         	String username = token.split(" ")[1].split("-")[0];
         	
         	if(!pathValue.equals(username)) {
         		return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
         	}
         	
         	userService = new UserServiceImpl();
         	if(userService.validateToken(token)) {
         		UserDetails ud = userService.getUserDetails(username);
         		JSONObject json = new JSONObject();
         		json.put("Name", ud.getName());
         		json.put("Bio", ud.getBio());
         		json.put("Image", ud.getImage());
  
         		return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT,json.toString()+"\r\n"); 

         	}
         	
         	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
         } catch (Exception e) {
             return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
         }
    }
    
    private Response battleRound(String token) {
        try {
        	if(token == null || token.isEmpty()) {
        		return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	}
        	String username = token.split(" ")[1].split("-")[0];
        	
        	userService = new UserServiceImpl();
        	if(userService.validateToken(token)) {
        		userService.battle(username);
				return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Started"+"\r\n");
        	}
        
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	
        }catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
        
    }
    
    private Response getPlayerStat(String token) {
        try {
        	userService = new UserServiceImpl();
        	packageService = new PackageServiceImpl();
        	if(userService.validateToken(token)) {
        		String username = token.split(" ")[1].split("-")[0];
        		PlayerStat ps = userService.getPlayerStat(username);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("user", username);
					jsonObject.put("Games", ps.getGames());
					jsonObject.put("Rating", ps.getRating());
					jsonObject.put("Wins", ps.getWins());
					jsonObject.put("Loses", ps.getLoses());
					jsonObject.put("Draws", ps.getDraws());
        		
        		return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, jsonObject.toString()+"\r\n");
        	}
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	
        }catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
        
    }
    
    private Response getScorecard(String token) {
        try {
        	userService = new UserServiceImpl();
        	packageService = new PackageServiceImpl();
        	if(userService.validateToken(token)) {
        		String username = token.split(" ")[1].split("-")[0];
        		List<PlayerStat> ps =  userService.getScoreboard();
        		JSONArray jsonArray = new JSONArray();
        		for (PlayerStat player : ps) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("Name", player.getUsername());
					jsonObject.put("Rating", player.getRating());
					jsonArray.put(jsonObject);
				}
        		return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, jsonArray.toString()+"\r\n");
        		
        	}
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	
        }catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
        
    }
    
    private Response getAvailableTrades(String token) {
        try {
        	userService = new UserServiceImpl();
        	packageService = new PackageServiceImpl();
        	if(userService.validateToken(token)) {
        		String username = token.split(" ")[1].split("-")[0];
        		List<Trade> trades =  packageService.getAvailableTrades();
        		JSONArray jsonArray = new JSONArray();
        		for (Trade trade : trades) {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("Id", trade.getId());
					jsonObject.put("Card Id", trade.getCardId());
					jsonObject.put("Type", trade.getType());
					jsonObject.put("Minimum Damage", trade.getMnimumDamage());
					jsonObject.put("Initiator", trade.getInitiator());
					jsonArray.put(jsonObject);
				}
        		return new Response(HttpStatus.OK, ContentType.PLAIN_TEXT, jsonArray.toString()+"\r\n");
        		
        	}
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	
        }catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
        
    }
    
    private Response createTrade(String token,String body) {
        try {
        	userService = new UserServiceImpl();
        	if(userService.validateToken(token)) {
        		String username = token.split(" ")[1].split("-")[0];
        		JSONObject json = new JSONObject(body);
        		Trade trade = new Trade();
        		trade.setInitiator(username);
        		trade.setId(json.getString("Id"));
        		trade.setMnimumDamage(json.getInt("MinimumDamage"));
        		trade.setCardId(json.getString("CardToTrade"));
        		trade.setType(json.getString("Type"));
        	
        		packageService = new PackageServiceImpl();
        		packageService.createTrade(trade);
        		return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Trade created successfully"+"\r\n");
        	}
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	
        }catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
        
    }
    
    public Response deleteTrade(String token, String pathValue) {
        try {
        	if(token == null || token.isEmpty()) {
        		return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	}
        	String username = token.split(" ")[1].split("-")[0];
        	
        	if(!pathValue.equals(username)) {
        		return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        	}
        	
        	userService = new UserServiceImpl();
        	if(packageService.deleteTrade(username, pathValue)) {
        		return new Response(HttpStatus.CREATED, ContentType.PLAIN_TEXT, "Trade Deleted"+"\r\n");
        	}
        	
        	return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized"+"\r\n");
        } catch (Exception e) {
            return new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, "Bad Request: Invalid JSON"+"\r\n");
        }
    }
    
}