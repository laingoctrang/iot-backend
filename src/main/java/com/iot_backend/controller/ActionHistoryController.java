package com.iot_backend.controller;

import com.iot_backend.entity.ActionHistory;
import com.iot_backend.entity.DataSensors;
import com.iot_backend.repository.ActionHistoryRepository;
import com.iot_backend.service.ActionHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/action-history")
public class ActionHistoryController {

    @Autowired
    private ActionHistoryService actionHistoryService;

    @GetMapping("/")
    public Page<ActionHistory> getSearchDataSensors(
            @RequestParam(name = "device", required = false) String device,
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "time", required = false) String time,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return actionHistoryService.getSearchActionHistory(device, action, time, sortBy, sortDir, page, size);
    }
}
