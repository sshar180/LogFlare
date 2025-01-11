

# Spring Boot LogFlare (Security Log Demo)

This repository contains a **Spring Boot** project that demonstrates:
- **Log ingestion** (single or bulk)
- **In-memory storage** (no external database required)
- **Basic anomaly detection** for security-related logs

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Project Structure](#project-structure)
- [How to Run](#how-to-run)
- [API Endpoints](#api-endpoints)
    - [Single Log Ingestion](#single-log-ingestion-post-apilogsparse)
    - [Bulk Log Ingestion](#bulk-log-ingestion-post-apilogsparsebulk)
    - [Get All Logs](#get-all-logs-get-apilogs)
    - [Get Anomalies](#get-anomalies-get-apilogsanomalies)
- [Anomaly Detection Rules](#anomaly-detection-rules)
- [Test Cases](#test-cases)
    - [Test Case 1: Repeated Failed Logins](#test-case-1-repeated-failed-logins)
    - [Test Case 2: Excessive Firewall Blocks](#test-case-2-excessive-firewall-blocks)
    - [Test Case 3: GeoLocation Anomaly](#test-case-3-geolocation-anomaly)
    - [Test Case 4: Mixed Logs (Multiple Anomalies)](#test-case-4-mixed-logs-multiple-anomalies)
    - [Bonus: Krutika (3 Fails) + Shubham (2 Success)](#bonus-krutika-3-fails--shubham-2-success)
- [License](#license)

---

## Overview

This application showcases **security-focused logging** in a **microservice** context. It collects logs (e.g., firewall logs, login events), stores them **in memory**, and applies **simple rules** to detect anomalous behavior:

1. **Repeated Failed Logins**
2. **Excessive Firewall Blocks**
3. **GeoLocation Anomalies**

You can easily expand these rules or integrate with real databases for production use.

---

## Features

1. **Ingest single or multiple logs** via REST APIs.
2. **Store logs in memory**—no database setup required.
3. **Detect anomalies** based on out-of-the-box rules (e.g., repeated login failures).
4. **Lombok** integration to reduce boilerplate code.
5. **Modular design** (Controller, Service, Model) for easy extension.

---

## Technologies

- **Java 17+** (though Java 11+ often works as well)
- **Spring Boot 2.x/3.x** (Web starter)
- **Lombok** (for getters/setters, constructors)
- **Maven** (for build and dependency management)

---

## Project Structure

The project is organized following standard **Spring Boot** conventions with a clear separation of concerns across controllers, services, and models.

```plaintext
src
└── main
    └── java
        └── com
            └── example
                └── securitylog
                    ├── controller
                    │   ├── LogController.java         # Handles single and bulk log ingestion, and fetch operations
                    │   └── AnomalyController.java     # Handles anomaly detection requests
                    │
                    ├── model
                    │   ├── UnifiedLog.java            # Represents the core log data model
                    │   └── AnomalyResult.java         # Represents the anomaly detection result model
                    │
                    ├── service
                    │   ├── LogService.java            # Manages log storage and retrieval in memory
                    │   └── AnomalyService.java        # Contains logic for anomaly detection
                    │
                    └── SecurityLogApplication.java    # Main entry point for the Spring Boot application
```
## API Endpoints

### Single Log Ingestion: `POST /api/logs/parse`
- **Request Body**: A single `UnifiedLog` JSON object.
- **Response**: The stored (normalized) `UnifiedLog`.

### Bulk Log Ingestion: `POST /api/logs/parse/bulk`
- **Request Body**: An array of `UnifiedLog` JSON objects.
- **Response**: A JSON array of the stored (normalized) logs.

### Get All Logs: `GET /api/logs`
- **Response**: A JSON array of all logs in memory.

### Get Anomalies: `GET /api/logs/anomalies`
- **Response**: A JSON array of anomalies, each with a `type` and `reason`.

---

## Anomaly Detection Rules

### RepeatedFailedLogins
- **Condition**: 3 or more failed login events (`logType = LOGIN`, `status = FAILED`) for the same `(username, sourceIP)` within 10 minutes.

### ExcessiveFirewallBlocks
- **Condition**: 5 or more `BLOCK` events (`logType = FIREWALL`, `action = BLOCK`) from the same `sourceIP`.

### GeoLocationAnomaly
- **Condition**: `geoLocation = "Unknown"` (or another suspicious indicator).

---
# Test Cases

Below are detailed **test cases** for the security log detection project. Each test involves submitting logs using the **`POST /api/logs/parse/bulk`** endpoint and verifying detected anomalies using **`GET /api/logs/anomalies`**.

---

## Test Case 1: Repeated Failed Logins

**Goal:**  
Detect repeated failed login attempts for the same user within a short time window.

**Input Payload (POST `/api/logs/parse/bulk`):**
```json
[
    {
      "logType": "LOGIN",
      "timestamp": "2025-01-10T12:00:00Z",
      "sourceIP": "192.168.1.50",
      "username": "Krutika",
      "status": "FAILED",
      "message": "First failed login"
    },
    {
      "logType": "LOGIN",
      "timestamp": "2025-01-10T12:03:00Z",
      "sourceIP": "192.168.1.50",
      "username": "Krutika",
      "status": "FAILED",
      "message": "Second failed login"
    },
    {
      "logType": "LOGIN",
      "timestamp": "2025-01-10T12:08:00Z",
      "sourceIP": "192.168.1.50",
      "username": "Krutika",
      "status": "FAILED",
      "message": "Third failed login"
    },
    {
      "logType": "LOGIN",
      "timestamp": "2025-01-10T12:10:00Z",
      "sourceIP": "192.168.1.60",
      "username": "Shubham",
      "status": "SUCCESS",
      "message": "First successful login"
    },
    {
      "logType": "LOGIN",
      "timestamp": "2025-01-10T12:11:00Z",
      "sourceIP": "192.168.1.60",
      "username": "Shubham",
      "status": "SUCCESS",
      "message": "Second successful login"
    }
  ]
```
---

## Test Case 2: Excessive Firewall Blocks

**Goal:**  
Detect multiple consecutive firewall block events from the same `sourceIP`.  

**Input Payload (POST `/api/logs/parse/bulk`):**
```json
[
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T13:00:00Z",
    "sourceIP": "10.0.0.5",
    "action": "BLOCK",
    "message": "Blocked traffic #1"
  },
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T13:01:00Z",
    "sourceIP": "10.0.0.5",
    "action": "BLOCK",
    "message": "Blocked traffic #2"
  },
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T13:02:00Z",
    "sourceIP": "10.0.0.5",
    "action": "BLOCK",
    "message": "Blocked traffic #3"
  },
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T13:03:00Z",
    "sourceIP": "10.0.0.5",
    "action": "BLOCK",
    "message": "Blocked traffic #4"
  },
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T13:04:00Z",
    "sourceIP": "10.0.0.5",
    "action": "BLOCK",
    "message": "Blocked traffic #5"
  }
]
```
---

## Test Case 3: GeoLocation Anomaly

**Goal:**  
Detect events with suspicious or missing geolocation data where `geoLocation = "Unknown"`.

**Input Payload (POST `/api/logs/parse/bulk`):**
```json
[
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T14:00:00Z",
    "sourceIP": "8.8.8.8",
    "action": "ALLOW",
    "geoLocation": "Unknown",
    "message": "DNS request allowed"
  },
  {
    "logType": "LOGIN",
    "timestamp": "2025-01-10T14:01:00Z",
    "sourceIP": "8.8.8.8",
    "username": "bob",
    "status": "SUCCESS",
    "geoLocation": "Unknown",
    "message": "Login from unusual location"
  }
]
```

## Test Case 4: Mixed Logs (Multiple Anomalies)

**Goal:**  
Detect multiple anomalies in a single bulk request, including:
- **Repeated Failed Logins**
- **Excessive Firewall Blocks**
- **GeoLocation Anomaly**

**Input Payload (POST `/api/logs/parse/bulk`):**
```json
[
  {
    "logType": "LOGIN",
    "timestamp": "2025-01-10T15:00:00Z",
    "sourceIP": "192.168.1.50",
    "username": "alice",
    "status": "FAILED"
  },
  {
    "logType": "LOGIN",
    "timestamp": "2025-01-10T15:03:00Z",
    "sourceIP": "192.168.1.50",
    "username": "alice",
    "status": "FAILED"
  },
  {
    "logType": "LOGIN",
    "timestamp": "2025-01-10T15:09:00Z",
    "sourceIP": "192.168.1.50",
    "username": "alice",
    "status": "FAILED"
  },
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T15:02:00Z",
    "sourceIP": "10.0.0.5",
    "action": "BLOCK"
  },
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T15:03:00Z",
    "sourceIP": "10.0.0.5",
    "action": "BLOCK"
  },
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T15:04:00Z",
    "sourceIP": "10.0.0.5",
    "action": "BLOCK"
  },
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T15:05:00Z",
    "sourceIP": "10.0.0.5",
    "action": "BLOCK"
  },
  {
    "logType": "FIREWALL",
    "timestamp": "2025-01-10T15:06:00Z",
    "sourceIP": "10.0.0.5",
    "action": "BLOCK"
  },
  {
    "logType": "LOGIN",
    "timestamp": "2025-01-10T15:07:00Z",
    "sourceIP": "8.8.8.8",
    "username": "bob",
    "status": "FAILED",
    "geoLocation": "Unknown"
  }
]

