package com.iot_backend.service;

import com.iot_backend.entity.DataSensors;
import com.iot_backend.repository.DataSensorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DataSensorsService {
    @Autowired
    private DataSensorsRepository dataSensorsRepository;

    public Map<String, String> getLatestData() {
        Optional<DataSensors> latestSensorData = dataSensorsRepository.findTopByOrderByTimeDesc();
        Map<String, String> result = new HashMap<>();
        if (latestSensorData.isPresent()) {
            DataSensors data = latestSensorData.get();
            result.put("temperature", String.valueOf(data.getTemperature()));
            result.put("humidity", String.valueOf(data.getHumidity()));
            result.put("light", String.valueOf(data.getLight()));
            result.put("dust", String.valueOf(data.getDust()));
            result.put("time", String.valueOf(data.getTime()));
        } else {
            result.put("temperature", "0");
            result.put("humidity", "0");
            result.put("light", "0");
            result.put("dust", "0");
            result.put("time", "0");
        }
        return result;
    }

    public List<DataSensors> getRecentData() {
        return dataSensorsRepository.findTop20ByOrderByTimeDesc();
    }

    public Map<String, String> getMinMaxData() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);

        Object[] result = dataSensorsRepository.findMinMaxValues(startOfDay, endOfDay);

        Map<String, String> minMaxDataSensors = new HashMap<>();
        if (result.length > 0 && result[0] instanceof Object[]) { // Kiểm tra có dữ liệu và kiểu là Object[]
            Object[] data = (Object[]) result[0]; // Lấy mảng con đầu tiên

            minMaxDataSensors.put("minHumidity", data[2] != null ? String.valueOf(((Number)data[2]).intValue()) : "0");
            minMaxDataSensors.put("maxHumidity", data[3] != null ? String.valueOf(((Number)data[3]).intValue()) : "0");

            minMaxDataSensors.put("minLight", data[4] != null ? String.valueOf(((Number)data[4]).intValue()) : "0");
            minMaxDataSensors.put("maxLight", data[5] != null ? String.valueOf(((Number)data[5]).intValue()) : "0");

            minMaxDataSensors.put("minTemperature", data[0] != null ? data[0].toString() : "0");
            minMaxDataSensors.put("maxTemperature", data[1] != null ? data[1].toString() : "0");

            minMaxDataSensors.put("minDust", data[6] != null ? String.valueOf(((Number)data[6]).intValue()) : "0");
            minMaxDataSensors.put("maxDust", data[7] != null ? String.valueOf(((Number)data[7]).intValue()) : "0");

        } else {
            // Mặc định là "0" nếu không đủ giá trị
            minMaxDataSensors.put("minTemperature", "0");
            minMaxDataSensors.put("maxTemperature", "0");
            minMaxDataSensors.put("minHumidity", "0");
            minMaxDataSensors.put("maxHumidity", "0");
            minMaxDataSensors.put("minLight", "0");
            minMaxDataSensors.put("maxLight", "0");
            minMaxDataSensors.put("minDust", "0");
            minMaxDataSensors.put("maxDust", "0");
        }
        return minMaxDataSensors;
    }

    public Page<DataSensors> getSearchDataSensors(
            String temperature,
            String humidity,
            String light,
            String time,
            String sortBy,
            String sortDir,
            int page,
            int size) {

        if (temperature != null && temperature.trim().isEmpty()) temperature = null;
        if (humidity != null && humidity.trim().isEmpty()) humidity = null;
        if (light != null && light.trim().isEmpty()) light = null;
        if (time != null && time.trim().isEmpty()) time = null;

        Pageable pageable;

        if (sortBy != null && sortDir != null) {
            Sort sort = Sort.by(sortBy);
            if (sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())) {
                sort = sort.descending();
            } else {
                sort = sort.ascending();
            }
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size); // Không có sắp xếp
        }

        return dataSensorsRepository.findByTemperatureAndHumidityAndLightAndTime(temperature, humidity, light, time, pageable);
    }

    public Page<DataSensors> getSearchDataSensors2(
            String temperature,
            String humidity,
            String light,
            String dust,
            String time,
            String sortBy,
            String sortDir,
            int page,
            int size) {

        if (temperature != null && temperature.trim().isEmpty()) temperature = null;
        if (humidity != null && humidity.trim().isEmpty()) humidity = null;
        if (light != null && light.trim().isEmpty()) light = null;
        if (dust != null && dust.trim().isEmpty()) dust = null;
        if (time != null && time.trim().isEmpty()) time = null;

        Pageable pageable;

        if (sortBy != null && sortDir != null) {
            Sort sort = Sort.by(sortBy);
            if (sortDir.equalsIgnoreCase(Sort.Direction.DESC.name())) {
                sort = sort.descending();
            } else {
                sort = sort.ascending();
            }
            pageable = PageRequest.of(page, size, sort);
        } else {
            pageable = PageRequest.of(page, size); // Không có sắp xếp
        }

        return dataSensorsRepository.findByTemperatureAndHumidityAndLightAndTime2(temperature, humidity, light, dust, time, pageable);
    }

    public long countDustAboveThresholdInCurrentDay() {
        // Lấy thời gian bắt đầu và kết thúc của ngày hiện tại
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN); // 00:00 của ngày hiện tại
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);   // 23:59:59 của ngày hiện tại

        return dataSensorsRepository.countDustAboveThresholdInDay(startOfDay, endOfDay);
    }
}
