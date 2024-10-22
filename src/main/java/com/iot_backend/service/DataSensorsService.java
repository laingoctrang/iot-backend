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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DataSensorsService {
    @Autowired
    private DataSensorsRepository dataSensorsRepository;


//    public Page<DataSensors> getDataSensorsWithPagination(int page, int size) {
//        return dataSensorsRepository.findAll(PageRequest.of(page, size));
//    }

    public Map<String, String> getLatestData() {
        Optional<DataSensors> latestSensorData = dataSensorsRepository.findTopByOrderByTimeDesc();
        Map<String, String> result = new HashMap<>();
        if (latestSensorData.isPresent()) {
            DataSensors data = latestSensorData.get();
            result.put("temperature", String.valueOf(data.getTemperature()));
            result.put("humidity", String.valueOf(data.getHumidity()));
            result.put("light", String.valueOf(data.getLight()));
        } else {
            result.put("temperature", "0");
            result.put("humidity", "0");
            result.put("light", "0");
        }
        return result;
    }

    public List<DataSensors> getRecentData() {
        return dataSensorsRepository.findTop20ByOrderByTimeDesc();
    }

    public Map<String, String> getMinMaxData() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        Object[] result = dataSensorsRepository.findMinMaxValues(startOfDay, endOfDay);

        Map<String, String> minMaxDataSensors = new HashMap<>();
        if (result.length >= 6) { // Kiểm tra nếu mảng có đủ 6 giá trị
            minMaxDataSensors.put("minTemperature", result[0] != null ? result[0].toString() : "0");
            minMaxDataSensors.put("maxTemperature", result[1] != null ? result[1].toString() : "0");
            minMaxDataSensors.put("minHumidity", result[2] != null ? result[2].toString() : "0");
            minMaxDataSensors.put("maxHumidity", result[3] != null ? result[3].toString() : "0");
            minMaxDataSensors.put("minLight", result[4] != null ? result[4].toString() : "0");
            minMaxDataSensors.put("maxLight", result[5] != null ? result[5].toString() : "0");
        } else {
            // Nếu không đủ giá trị, trả về "0" cho tất cả
            minMaxDataSensors.put("minTemperature", "0");
            minMaxDataSensors.put("maxTemperature", "0");
            minMaxDataSensors.put("minHumidity", "0");
            minMaxDataSensors.put("maxHumidity", "0");
            minMaxDataSensors.put("minLight", "0");
            minMaxDataSensors.put("maxLight", "0");
        }
        return minMaxDataSensors;
    }

//    public Page<DataSensors> getSortDataSensors(String sortBy, String sortDir, int page, int size) {
//        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        return dataSensorsRepository.findAll(pageable);
//    }

    public Page<DataSensors> getSearchDataSensors(
            String temperature,
            String humidity,
            String light,
            String date,
            String time,
            String sortBy,
            String sortDir,
            int page,
            int size) {

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

        LocalDateTime timeStart = null;
        LocalDateTime timeEnd = null;

        if (date != null && !date.isEmpty()) {
            // Nếu có giá trị `date`, tìm kiếm trong khoảng thời gian của ngày đó
            LocalDate parsedDate = LocalDate.parse(date); // Parse date theo yyyy-MM-dd

            // Nếu có `time`, kết hợp với `date` thành `datetime`
            if (time != null && !time.isEmpty()) {
                LocalTime parsedTime = LocalTime.parse(time); // Parse time theo HH:mm:ss
                timeStart = LocalDateTime.of(parsedDate, parsedTime); // Kết hợp thành datetime chính xác
                timeEnd = timeStart.plusSeconds(0); // Tìm đúng giây của bản ghi
            } else {
                // Nếu chỉ có `date`, tìm kiếm trong cả ngày
                timeStart = parsedDate.atStartOfDay(); // Bắt đầu từ 00:00:00
                timeEnd = parsedDate.atTime(LocalTime.MAX); // Kết thúc tại 23:59:59
            }
        } else if (time != null && !time.isEmpty()) {
            return dataSensorsRepository.findByTemperatureAndHumidityAndLightAndTime(temperature, humidity, light, time, pageable);
        }

        return dataSensorsRepository.findByTemperatureAndHumidityAndLightAndTime(temperature, humidity, light, timeStart, timeEnd, pageable);
    }
}
