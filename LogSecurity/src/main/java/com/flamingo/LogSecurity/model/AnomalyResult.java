package com.flamingo.LogSecurity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a detected anomaly with a simple type/reason.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyResult {
    // e.g "RepeatedFailedLogins"
    private String type;
    // e.g " 3+ failed logins within 10 minutes for Krutika|192.168.1.101"
    private String reason;
}