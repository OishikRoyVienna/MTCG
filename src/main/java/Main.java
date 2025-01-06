import database.DatabaseHelper;
import server.Server;
import utils.Router;
import routing.ClientHandler;


public class Main {
    public static void main(String[] args) {
        Main main = new Main();  // Router konfigurieren
        Router router1 = new Router();
        Router router = main.configureRouter(router1);
        Server server = new Server(10001, router);  // Server auf Port 10001 starten
        try {
        	 DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
             databaseHelper.createTables();
            server.start();  // Server starten
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final Router configureRouter(final Router router) {
        
        ClientHandler clientHandler = new ClientHandler();

        router.addService("GET /", clientHandler);
        router.addService("GET /cards", clientHandler);
        router.addService("GET /deck", clientHandler);
        router.addService("GET /users/*", clientHandler);
        router.addService("GET /stats", clientHandler);
        router.addService("GET /scoreboard", clientHandler);
        router.addService("GET /tradings", clientHandler);
        router.addService("PUT /deck", clientHandler);
        router.addService("PUT /users/*", clientHandler);
        router.addService("POST /users", clientHandler);
        router.addService("POST /sessions", clientHandler);
        router.addService("POST /packages", clientHandler);
        router.addService("POST /transactions/packages", clientHandler);
        router.addService("POST /battles", clientHandler);
        router.addService("POST /tradings", clientHandler);
        router.addService("DELETE /tradings/*", clientHandler);

        return router;  // Gib den konfigurierten Router zurück
    }
}