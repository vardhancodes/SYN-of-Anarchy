High-Throughput Java Web Server (From Scratch)

A multithreaded HTTP web server built entirely from scratch using raw Java Sockets (java.net.ServerSocket) and the java.util.concurrent package.

This project strips away heavy frameworks like Spring Boot to handle network I/O, raw TCP byte streams, and thread allocation manually. It is designed to demonstrate how backend frameworks work under the hood, how HTTP protocols are parsed at the byte level, and how to prevent thread exhaustion in high-performance environments like low-latency trading.

🧠 Architecture & Design

Instead of relying on a framework's embedded Tomcat server, this project implements two core mechanisms directly:

1. The TCP Socket Lifecycle

The server binds to port 8080. When a client connects, the server performs the TCP handshake, accepts the connection, and instantly hands the raw Socket over to a background worker to prevent blocking the main listening thread.

sequenceDiagram
    participant Client
    participant ServerSocket (Main)
    participant ThreadPool (Worker)
    
    ServerSocket->>ServerSocket: Bind & Listen (Port 8080)
    Client->>ServerSocket: TCP SYN (Request Connection)
    ServerSocket-->>Client: TCP SYN-ACK
    Client->>ServerSocket: TCP ACK (Established)
    
    Note over ServerSocket: accept() returns a Socket object
    ServerSocket->>ThreadPool: Offload Socket to Worker Thread
    ServerSocket->>ServerSocket: Loop back to listen for next client
    
    Client->>ThreadPool: Send Raw HTTP Request Bytes
    ThreadPool->>ThreadPool: Parse Request & Route
    ThreadPool-->>Client: Send HTTP/1.1 Response Bytes
    ThreadPool->>ThreadPool: Close Socket (Release Resources)


2. The Worker Thread Pool

If a server processes requests synchronously, one slow client can freeze the entire application. We utilize a bounded FixedThreadPool to concurrently handle multiple connections, ensuring maximum throughput.

graph TD
    A[Browser Client] -->|TCP Connection| D(ServerSocket accept queue)
    B[cURL Client] -->|TCP Connection| D
    C[Mobile Client] -->|TCP Connection| D
    
    D -->|Hands off Socket| E{Thread Pool Executor}
    
    E -->|Assigns Task| F[Worker Thread 1]
    E -->|Assigns Task| G[Worker Thread 2]
    E -->|Assigns Task| H[Worker Thread N]
    
    F --> I[ClientHandler.run]
    G --> I
    H --> I


📦 Project Modules

The architecture is divided into four distinct components to maintain a clean separation of concerns:

WebServer.java: The main entry point. Binds the ServerSocket to a port, initializes the ExecutorService (Thread Pool), and runs the infinite listening loop.

ClientHandler.java: The Runnable task executed by the thread pool. It reads the raw input stream, routes the request to the correct handler based on the URI, and closes the TCP socket when finished.

HttpRequest.java: The raw HTTP parser. Reads the stream line-by-line, splitting out the HTTP Method, URI, Protocol Version, HTTP Headers, and uses the Content-Length header to perfectly extract the raw JSON body from POST requests.

HttpResponse.java: The response builder. Constructs strict HTTP/1.1 compliant response strings (Status Line -> Headers -> Blank Line -> Byte Payload).

🚀 Features

Multithreaded Execution: Capable of handling concurrent clients without blocking.

Static File Serving: Serves HTML, CSS, JS, and generic text files from a wwwroot directory.

MIME Type Inference: Automatically detects and appends the correct Content-Type headers for web browsers.

POST Request & JSON Parsing: Capable of reading HTTP headers to calculate body size, capturing raw JSON payloads sent to specific API endpoints.

Graceful 404 Handling: Properly formatted error responses for missing routes.

💻 How to Run

Prerequisites

Java Development Kit (JDK) 8 or higher.

Eclipse IDE (or any standard Java IDE).

Setup

Clone this repository.

Ensure the wwwroot folder exists in the root directory (at the same level as src).

Place an index.html file inside the wwwroot folder.

Run WebServer.java as a Java Application.

You should see the following in your console:
Server started on port 8080

Testing the Endpoints

1. Test Static Web Pages (GET)
Open your browser and navigate to:
http://localhost:8080

2. Test the Trading API (POST)
Open a terminal (Command Prompt) and fire a JSON payload at the server:

curl -X POST http://localhost:8080/api/trade -H "Content-Type: application/json" -d "{\"ticker\": \"AAPL\", \"action\": \"BUY\", \"quantity\": 50}"


Expected Output:
{"status": "success", "message": "Order received", "payload": {"ticker": "AAPL", "action": "BUY", "quantity": 50}}