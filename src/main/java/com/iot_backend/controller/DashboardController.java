package com.iot_backend.controller;

import com.iot_backend.entity.DataSensors;
import com.iot_backend.service.DataSensorsService;
import com.iot_backend.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DataSensorsService dataSensorsService;

    @Autowired
    private MqttService mqttService;

    @GetMapping("/latest")
    public ResponseEntity<Map<String, String>> getLatestDataSensors() {
        Map<String, String> latestData = dataSensorsService.getLatestData();
        return ResponseEntity.ok(latestData);
    }

    @GetMapping("/recent")
    public List<DataSensors> getRecentDataSensors() {
        List<DataSensors> recentData = dataSensorsService.getRecentData();
        return recentData;
    }

    @GetMapping("/min-max")
    public Map<String, String> getMinMaxData() {
        return dataSensorsService.getMinMaxData();
    }



    @PostMapping("/control")
    public ResponseEntity<Map<String, Object>> controlDevice(
            @RequestParam String device,
            @RequestParam String action) {

        Map<String, Object> response = new HashMap<>();

        // Kiểm tra các tham số
        if (device == null || action == null) {
            response.put("message", "Invalid device or action parameter.");
            return ResponseEntity.badRequest().body(response);
        }

        // Thực hiện điều khiển thiết bị
        boolean success = mqttService.controlDevice(device, action);
        if (success) {
            response.put("device", device);
            response.put("action", action);
            response.put("status", "success");
            response.put("message", "Device " + device + " turned " + action);
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to control device.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/dust-count")
    public long getDustCountAboveThresholdToday() {
        return dataSensorsService.countDustAboveThresholdInCurrentDay();
    }
}
