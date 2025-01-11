package com.flamingo.LogSecurity.controller;

import com.flamingo.LogSecurity.model.UnifiedLog;
import com.flamingo.LogSecurity.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * * First Web Service:
 * */
@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * Parse a log entry, store it in memory, and return the unified object.
     */
    @PostMapping("/parse")
    public UnifiedLog parseLogEntry(@RequestBody UnifiedLog incomingLog) {
        return logService.parseAndStoreLog(incomingLog);
    }

    /**
     *  Endpoint to handle multiple logs at once.
     */
    @PostMapping("/parse/bulk")
    public List<UnifiedLog> parseMultipleLogs(@RequestBody List<UnifiedLog> logs) {
        // The service method parseAndStoreLogs handles normalization & storage
        return logService.parseAndStoreLogs(logs);
    }

    /**
     *  Retrieve all stored logs.
     * Endpoint: GET /api/logs
     */
    @GetMapping
    public List<UnifiedLog> getAllLogs() {
        return logService.getAllLogs();
    }
}