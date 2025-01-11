package com.flamingo.LogSecurity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a unified log structure for different log types.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedLog {

    // Common fields
    private String logType;     // e.g., FIREWALL, LOGIN
    private String timestamp;   // e.g., ISO 8601 string
    private String sourceIP;
    private String message;
    private String severity;    // e.g., INFO, WARNING, CRITICAL
    private String deviceId;

    // Firewall-specific
    private String destinationIP;
    private Integer port;
    private String action;      // e.g., ALLOW, BLOCK, DROP

    // Login-specific
    private String username;
    private String status;      // e.g., SUCCESS, FAILED

    // Enhanced security fields (optional)
    private String eventCategory;   // e.g., AUTH_ATTEMPT, TRAFFIC
    private String userAgent;       // for web/app logs
    private String correlationId;
    private String hostName;
    private String geoLocation;     // e.g., "US", "UK", "Unknown"
    private List<String> tags;      // flexible labeling
}