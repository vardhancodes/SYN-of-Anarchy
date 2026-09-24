# ⚡ SYN-of-Anarchy

> **A multithreaded HTTP web server built from scratch in Java — no Spring Boot, no Tomcat, no external web framework.**

[![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)](https://www.oracle.com/java/)
[![HTTP](https://img.shields.io/badge/Protocol-HTTP%2F1.1-blue)](#)
[![Concurrency](https://img.shields.io/badge/Concurrency-Fixed%20Thread%20Pool-purple)](#)
[![Sockets](https://img.shields.io/badge/Networking-TCP%20Sockets-red)](#)
[![GitHub](https://img.shields.io/badge/Source-GitHub-black?logo=github)](https://github.com/vardhancodes/SYN-of-Anarchy)

---

## 🧠 What is SYN-of-Anarchy?

**SYN-of-Anarchy** is a lightweight HTTP server implemented directly on top of Java's networking and concurrency APIs.

Instead of hiding networking behind frameworks such as Spring Boot or Tomcat, this project manually handles:

- TCP socket connections
- HTTP request parsing
- HTTP response construction
- Thread-pool based concurrency
- Static file serving
- MIME type detection
- JSON `POST` requests
- Basic API routing
- HTTP error handling

The goal is simple:

> **Understand what happens underneath a typical Java web server.**

---

# 🏗️ Architecture

The server is split into four main components:

```mermaid
flowchart TD
    A[Browser / cURL] --> B[ServerSocket :8080]
    B --> C[accept]
    C --> D[Thread Pool]

    D --> E[ClientHandler]
    E --> F[HttpRequest]

    F --> G{HTTP Method}

    G -->|GET| H[Resolve File Path]
    H --> I[wwwroot]
    I --> J[HttpResponse]

    G -->|POST /api/trade| K[Read JSON Body]
    K --> J

    G -->|Unsupported Method| L[405 Method Not Allowed]
    H -->|File Missing| M[404 Not Found]

    J --> A
    L --> A
    M --> A
