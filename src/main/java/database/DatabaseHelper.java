package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseHelper {
    private static final String DATA_CONNECTION_STRING = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";
    private Connection connection;
    private static DatabaseHelper instance;
    
    private DatabaseHelper() throws SQLException{
    	try {
    		 this.connection = DriverManager.getConnection(DATA_CONNECTION_STRING, USER, PASSWORD);
    	}catch (SQLException e) {
			System.out.println("Error creating connection "+ e);
		}
    }
    
    public static DatabaseHelper getInstance() throws SQLException{
    	if(null == instance) {
    		synchronized (DatabaseHelper.class) {
				if(null == instance) {
					instance = new DatabaseHelper();
				}
			}
    	}
    	return instance;
    }
    
    
    public Connection getConnection() {
    	return connection;
    }

    
    public void createTables() {
        try (Statement statement = this.connection.createStatement()) {

            //connection.setAutoCommit(false);

            // Tabelle Users erstellen
            statement.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS Users (
                            Id SERIAL,
                            Username VARCHAR(255) PRIMARY KEY,
                            Password VARCHAR(255),
                            Token VARCHAR(255)
                            
                        );
                        
                        
                        CREATE TABLE IF NOT EXISTS Package (
                            Id SERIAL PRIMARY KEY,
                            Name VARCHAR(255),
                            Available BOOLEAN DEFAULT TRUE
                        );
                        
                         CREATE TABLE IF NOT EXISTS Card (
                            Id VARCHAR(255) PRIMARY KEY,
                            Name VARCHAR(255),
                            Damage DECIMAL(10,1),
                            PackageId BIGINT,
                            FOREIGN KEY (PackageId) REFERENCES Package (Id)
                        );
                        
                         CREATE TABLE IF NOT EXISTS UserDetails (
                            Id SERIAL PRIMARY KEY,
                            Username VARCHAR(255),
                            Name VARCHAR(255),
                            Bio VARCHAR(255),
                            Image VARCHAR(255),
                            Packageshold BIGINT DEFAULT 0,
                            FOREIGN KEY (Username) REFERENCES USERS (Username)
                        );
                        
                         CREATE TABLE IF NOT EXISTS UserCard (
                            Id SERIAL PRIMARY KEY,
                            UserId VARCHAR(255),
                            CardId VARCHAR(255),
                            Deck BOOLEAN DEFAULT FALSE,
                            FOREIGN KEY (UserId) REFERENCES USERS (Username),
                            FOREIGN KEY (CardId) REFERENCES Card (Id)

                        );
                        
                         CREATE TABLE IF NOT EXISTS Battle (
                            Id SERIAL PRIMARY KEY,
                            Battle BIGINT,
                            Round BIGINT,
                            Player1 VARCHAR(255),
                            Player2 VARCHAR(255),
                            Player1Card VARCHAR(255),
                            Player2Card VARCHAR(255),
                            Log VARCHAR(255),
                            Winner VARCHAR(255),
                            RoundStatus VARCHAR(255),
                            BattleStatus VARCHAR(255),
                            FOREIGN KEY (Player1) REFERENCES USERS (Username),
                            FOREIGN KEY (Player2) REFERENCES USERS (Username)

                        );
                        
                         CREATE TABLE IF NOT EXISTS PlayerStat (
                            Id SERIAL PRIMARY KEY,
                            Games BIGINT,
                            Player VARCHAR(255),
                            Rating BIGINT,
                            Wins BIGINT,
                            Loses BIGINT,
                            Draws BIGINT,
            				FOREIGN KEY (Player) REFERENCES USERS (Username)
                        );
                        
                         CREATE TABLE IF NOT EXISTS BattleRequest (
                            Id SERIAL PRIMARY KEY,
                            Player1 VARCHAR(255),
                            Player2 VARCHAR(255),
                            Status VARCHAR(255),
            				FOREIGN KEY (Player1) REFERENCES USERS (Username),
            				FOREIGN KEY (Player2) REFERENCES USERS (Username)
                        );
                        
                        CREATE TABLE IF NOT EXISTS Trade (
                            Id VARCHAR(255) PRIMARY KEY,
                            CardId VARCHAR(255),
                            Type VARCHAR(255),
                            MinimumDamage BIGINT,
                            Status VARCHAR(255),
                            Initiator VARCHAR(255),
                            Approver VARCHAR(255),
            				FOREIGN KEY (CardId) REFERENCES Card (Id),
            				FOREIGN KEY (Initiator) REFERENCES USERS (Username),
            				FOREIGN KEY (Approver) REFERENCES USERS (Username)
                        );
                        
                    """);
            
         
            //connection.commit();  // Transactions abschließen
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}