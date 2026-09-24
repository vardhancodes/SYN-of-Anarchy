package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String uri;
    private String version;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpRequest(BufferedReader in) throws IOException {
        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty Request");
        }

        String[] parts = requestLine.split(" ");
        if (parts.length == 3) {
            this.method = parts[0];
            this.uri = parts[1];
            this.version = parts[2];
        }

        
        String headerLine;
        while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] bodyChars = new char[contentLength];
            int read = in.read(bodyChars, 0, contentLength);
            if (read == contentLength) {
                this.body = new String(bodyChars);
            }
        }
    }

    public String getMethod() { return method; }
    public String getUri() { return uri.equals("/") ? "/index.html" : uri; }
    public String getBody() { return body; }
}