package com.server;

import java.io.IOException;
import java.io.OutputStream;

public class HttpResponse {
    
    public static void send(OutputStream out, int statusCode, String statusText, String contentType, byte[] content) throws IOException {
        String responseHeader = "HTTP/1.1 " + statusCode + " " + statusText + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + content.length + "\r\n"
                + "Connection: close\r\n\r\n";
        
       
        out.write(responseHeader.getBytes());
       
        out.write(content);
        out.flush();
    }
    
    public static void send404(OutputStream out) throws IOException {
        String errorMsg = "<h1>404 Not Found</h1>";
        send(out, 404, "Not Found", "text/html", errorMsg.getBytes());
    }
}