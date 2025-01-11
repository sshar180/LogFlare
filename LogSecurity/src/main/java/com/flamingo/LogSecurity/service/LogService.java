package com.flamingo.LogSecurity.service;


import com.flamingo.LogSecurity.model.UnifiedLog;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages storing and normalizing logs in memory.
 */
@Service
public class LogService {

    private final List<UnifiedLog> inMemoryLogStore = new ArrayList<>();

    /**
     * Normalize/validate incoming logs, then store.
     */
    public UnifiedLog parseAndStoreLog(UnifiedLog log) {
        UnifiedLog normalizedLog = normalizeLog(log);
        inMemoryLogStore.add(normalizedLog);
        return normalizedLog;
    }

    /**
     * Returns all logs in memory.
     */
    public List<UnifiedLog> getAllLogs() {
        return inMemoryLogStore;
    }

    /**
     * Basic normalization logic, e.g., setting defaults if missing.
     */
    private UnifiedLog normalizeLog(UnifiedLog log) {
        if (log.getSeverity() == null) {
            log.setSeverity("INFO");
        }
        if (log.getTimestamp() == null) {
            log.setTimestamp(Instant.now().toString());
        }
        return log;
    }

    /**
     * Parse & store multiple logs in one go.
     */
    public List<UnifiedLog> parseAndStoreLogs(List<UnifiedLog> logs) {
        List<UnifiedLog> storedLogs = new ArrayList<>();
        for (UnifiedLog log : logs) {
            // reuse the same logic for each log
            UnifiedLog normalizedLog = normalizeLog(log);
            inMemoryLogStore.add(normalizedLog);
            storedLogs.add(normalizedLog);
        }
        return storedLogs;
    }
}
