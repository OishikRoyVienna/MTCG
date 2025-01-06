package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import server.Service;

public class Router {
    private final Map<String, Service> serviceRegistry = new ConcurrentHashMap<>();

    // Route hinzufügen, z.B. "POST /users"
    public synchronized void addService(String route, Service service) {
        this.serviceRegistry.put(route, service);
    }

    // Route entfernen
    public void removeService(String route) {
        this.serviceRegistry.remove(route);
    }

    // Methode und Pfad kombinieren, um die richtige Route zu finden
    public synchronized Service resolve(String method, String path) {
    	System.out.println(method +" " +path);
        String route = method + " " + path;  // Kombiniere Methode und Pfad
        System.out.println("Size " +this.serviceRegistry.size());
        return this.serviceRegistry.get(route);
    }
}