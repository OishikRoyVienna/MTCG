package utils;

import http.ContentType;
import http.HttpStatus;
import server.Request;
import server.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private Socket clientSocket;
    private volatile Router router;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    public RequestHandler(Socket clientSocket, Router router) throws IOException {
        this.clientSocket = clientSocket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        this.printWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
        this.router = router;
    }

    @Override
    public void run() {
        try {
            Response response;
            Request request = new RequestBuilder().buildRequest(this.bufferedReader);

            if (request.getPathname() == null) {
                response = new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "[]"
                );
            } else {
            	System.out.println(this.router.resolve(request.getMethod().name(), request.getPathname()) == null);
            	String path = request.getPathname();
            	if(request.getPathname().contains("/users/") && request.getPathParts().size() == 2) {
            		path = "/users/*";
            	}else if (request.getPathname().contains("/tradings/") && request.getPathParts().size() == 2) {
            		path = "/tradings/*";
            	}
                response = this.router.resolve(request.getMethod().name(), path).handleRequest(request);
            }
            printWriter.write(response.get());
            printWriter.flush();
        } catch (IOException e) {
            System.err.println(Thread.currentThread().getName() + " Error: " + e.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printWriter != null) {
                    printWriter.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}