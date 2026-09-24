package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final String WEB_ROOT = "wwwroot";

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream()
        ) {
            HttpRequest request = new HttpRequest(in);
            System.out.println("Handling request: " + request.getMethod() + " " + request.getUri());

            if (request.getMethod().equals("POST") && request.getUri().equals("/api/trade")) {
                System.out.println("Received Payload: " + request.getBody());
                
                String responseJson = "{\"status\": \"success\", \"message\": \"Order received\", \"payload\": " + request.getBody() + "}";
                HttpResponse.send(out, 200, "OK", "application/json", responseJson.getBytes());
                return;
            }

            if (!request.getMethod().equals("GET")) {
                HttpResponse.send(out, 405, "Method Not Allowed", "text/plain", "Method not supported".getBytes());
                return;
            }

            Path filePath = Paths.get(WEB_ROOT, request.getUri());
            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                byte[] fileBytes = Files.readAllBytes(filePath);
                String contentType = getContentType(filePath.toString());
                HttpResponse.send(out, 200, "OK", contentType, fileBytes);
            } else {
                HttpResponse.send404(out);
            }

        } catch (IOException e) {
            System.err.println("Handler Error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html";
        if (fileName.endsWith(".css")) return "text/css";
        if (fileName.endsWith(".js")) return "application/javascript";
        if (fileName.endsWith(".json")) return "application/json";
        return "text/plain";
    }
}