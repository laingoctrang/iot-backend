package com.iot_backend.controller;

import com.iot_backend.entity.DataSensors;
import com.iot_backend.service.DataSensorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@RestController
@RequestMapping("/api/data-sensors")
public class DataSensorsController {

    @Autowired
    private DataSensorsService dataSensorsService;

    //    @GetMapping("/")
//    public Page<DataSensors> getAllData(
//            @RequestParam(defaultValue = "1") int page, // Bắt đầu từ 1
//            @RequestParam(defaultValue = "10") int size) {
//        // Giảm đi 1 để phù hợp với chỉ số của Spring Data JPA
//        PageRequest pageRequest = PageRequest.of(page - 1, size);
//        return dataSensorsService.getAllData(pageRequest);
//    }

//    @GetMapping("/")
//    public Page<DataSensors> getDataSensorsWithPagination(
//            @RequestParam(value = "page") int page,
//            @RequestParam(value = "size") int size) {
//        System.out.println(page + size);
//        return dataSensorsService.getDataSensorsWithPagination(page, size);
//    }

//    @GetMapping("/sort")
//    public Page<DataSensors> getSortDataSensors(
//        @RequestParam(name = "sortBy") String sortBy,
//        @RequestParam(name = "sortDir") String sortDir,
//        @RequestParam(name = "page") int page,
//        @RequestParam(name = "size") int size) {
//
//        return dataSensorsService.getSortDataSensors(sortBy, sortDir, page, size);
//    }

    @GetMapping("/")
    public Page<DataSensors> getSearchDataSensors(
            @RequestParam(name = "temperature", required = false) String temperature,
            @RequestParam(name = "humidity", required = false) String humidity,
            @RequestParam(name = "light", required = false) String light,
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "time", required = false) String time,
            @RequestParam(name = "sortBy", required = false) String sortBy,
            @RequestParam(name = "sortDir", required = false) String sortDir,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        return dataSensorsService.getSearchDataSensors(temperature, humidity, light, date, time, sortBy, sortDir, page, size);
    }
}
