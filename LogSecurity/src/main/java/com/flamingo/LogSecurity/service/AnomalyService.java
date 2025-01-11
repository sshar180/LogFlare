package com.flamingo.LogSecurity.service;

import com.flamingo.LogSecurity.model.AnomalyResult;
import com.flamingo.LogSecurity.model.UnifiedLog;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Duration;
import java.util.*;

/**
 * Contains logic to detect anomalies across the logs.
 */
@Service
public class AnomalyService {

    /**
     * Detect anomalies by applying multiple rules across all logs.
     */
    public List<AnomalyResult> detectAnomalies(List<UnifiedLog> allLogs) {
        List<AnomalyResult> anomalies = new ArrayList<>();

        // Rule 1: Repeated failed logins
        anomalies.addAll(checkRepeatedFailedLogins(allLogs));

        // Rule 2: Excessive firewall blocks
        anomalies.addAll(checkExcessiveFirewallBlocks(allLogs));

        // Rule 3: GeoLocation-based anomaly
        anomalies.addAll(checkGeoLocationAnomalies(allLogs));

        // Add more rules as needed

        return anomalies;
    }

    /**
     * Rule: 3+ failed logins for same (username, sourceIP) within 10 minutes.
     */
    private List<AnomalyResult> checkRepeatedFailedLogins(List<UnifiedLog> logs) {
        Map<String, List<Instant>> failMap = new HashMap<>();
        List<AnomalyResult> results = new ArrayList<>();

        for (UnifiedLog log : logs) {
            if ("LOGIN".equalsIgnoreCase(log.getLogType()) &&
                    "FAILED".equalsIgnoreCase(log.getStatus())) {

                String key = log.getUsername() + "|" + log.getSourceIP();
                failMap.putIfAbsent(key, new ArrayList<>());
                failMap.get(key).add(Instant.parse(log.getTimestamp()));
            }
        }

        for (Map.Entry<String, List<Instant>> entry : failMap.entrySet()) {
            List<Instant> timestamps = entry.getValue();
            // Sort chronologically
            timestamps.sort(Comparator.naturalOrder());

            // Check for 3+ fails within 10 minutes
            for (int i = 0; i < timestamps.size() - 2; i++) {
                Instant first = timestamps.get(i);
                Instant third = timestamps.get(i + 2);
                long diffMinutes = Duration.between(first, third).toMinutes();
                if (diffMinutes <= 10) {
                    results.add(new AnomalyResult(
                            "RepeatedFailedLogins",
                            "3+ failed logins within 10 minutes for " + entry.getKey()
                    ));
                    break; // No need to check further for this key
                }
            }
        }

        return results;
    }

    /**
     * Rule: If same sourceIP has >= 5 firewall BLOCK actions, flag it.
     */
    private List<AnomalyResult> checkExcessiveFirewallBlocks(List<UnifiedLog> logs) {
        Map<String, Integer> blockCount = new HashMap<>();
        List<AnomalyResult> results = new ArrayList<>();

        for (UnifiedLog log : logs) {
            if ("FIREWALL".equalsIgnoreCase(log.getLogType()) &&
                    "BLOCK".equalsIgnoreCase(log.getAction())) {

                String ip = log.getSourceIP();
                blockCount.put(ip, blockCount.getOrDefault(ip, 0) + 1);
            }
        }

        // Threshold of 5 blocks
        for (Map.Entry<String, Integer> entry : blockCount.entrySet()) {
            if (entry.getValue() >= 5) {
                results.add(new AnomalyResult(
                        "ExcessiveFirewallBlocks",
                        "Source IP " + entry.getKey() + " had >= 5 blocks"
                ));
            }
        }
        return results;
    }

    /**
     * Rule: Simple check if geoLocation is "Unknown" (or suspicious).
     */
    private List<AnomalyResult> checkGeoLocationAnomalies(List<UnifiedLog> logs) {
        List<AnomalyResult> results = new ArrayList<>();

        for (UnifiedLog log : logs) {
            if (log.getGeoLocation() != null &&
                    "Unknown".equalsIgnoreCase(log.getGeoLocation())) {

                results.add(new AnomalyResult(
                        "GeoLocationAnomaly",
                        "Event from Unknown location (IP=" + log.getSourceIP() + ")"
                ));
            }
        }
        return results;
    }
}