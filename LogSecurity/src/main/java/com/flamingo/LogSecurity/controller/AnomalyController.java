package com.flamingo.LogSecurity.controller;

import com.flamingo.LogSecurity.model.AnomalyResult;
import com.flamingo.LogSecurity.model.UnifiedLog;
import com.flamingo.LogSecurity.service.AnomalyService;
import com.flamingo.LogSecurity.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class AnomalyController {

    private final LogService logService;
    private final AnomalyService anomalyService;

    @Autowired
    public AnomalyController(LogService logService, AnomalyService anomalyService) {
        this.logService = logService;
        this.anomalyService = anomalyService;
    }

    /**
     * Second Web Service:
     * Detect anomalies across the in-memory logs and provide a reason.
     */
    @GetMapping("/anomalies")
    public List<AnomalyResult> detectAnomalies() {
        List<UnifiedLog> allLogs = logService.getAllLogs();
        return anomalyService.detectAnomalies(allLogs);
    }
}
