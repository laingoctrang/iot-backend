package com.iot_backend.repository;

import com.iot_backend.entity.DataSensors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DataSensorsRepository extends JpaRepository<DataSensors, Long>, PagingAndSortingRepository<DataSensors, Long> {
    Optional<DataSensors> findTopByOrderByTimeDesc();
    List<DataSensors> findTop20ByOrderByTimeDesc();
    @Query("SELECT MIN(d.temperature), MAX(d.temperature), MIN(d.humidity), MAX(d.humidity), MIN(d.light), MAX(d.light) " +
            "FROM DataSensors d WHERE d.time >= :startOfDay AND d.time <= :endOfDay")
    Object[] findMinMaxValues(LocalDateTime startOfDay, LocalDateTime endOfDay);

//    List<DataSensors> findAllByOrderByTemperatureAsc();
//    List<DataSensors> findAllByOrderByTemperatureDesc();
//
//    List<DataSensors> findAllByOrderByHumidityAsc();
//    List<DataSensors> findAllByOrderByHumidityDesc();
//
//    List<DataSensors> findAllByOrderByLightAsc();
//    List<DataSensors> findAllByOrderByLightDesc();
//
//    List<DataSensors> findAllByOrderByTimeAsc();
//    List<DataSensors> findAllByOrderByTimeDesc();

    @Query("SELECT d FROM DataSensors d WHERE " +
            "(?1 IS NULL OR CAST(d.temperature AS string) LIKE CONCAT(?1, '%')) AND " +
            "(?2 IS NULL OR CAST(d.humidity AS string) LIKE CONCAT(?2, '%')) AND " +
            "(?3 IS NULL OR CAST(d.light AS string) LIKE CONCAT(?3, '%')) AND " +
            "(?4 IS NULL OR d.time >= ?4) AND " +
            "(?5 IS NULL OR d.time <= ?5)")
    Page<DataSensors> findByTemperatureAndHumidityAndLightAndTime(
            String temperature,
            String humidity,
            String light,
            LocalDateTime timeStart,
            LocalDateTime timeEnd,
            Pageable pageable);

    @Query("SELECT d FROM DataSensors d WHERE " +
            "(?1 IS NULL OR CAST(d.temperature AS string) LIKE CONCAT(?1, '%')) AND " +
            "(?2 IS NULL OR CAST(d.humidity AS string) LIKE CONCAT(?2, '%')) AND " +
            "(?3 IS NULL OR CAST(d.light AS string) LIKE CONCAT(?3, '%')) AND " +
            "(?4 IS NULL OR (CAST(d.time AS string) LIKE CONCAT('%', ?4)))")
    Page<DataSensors> findByTemperatureAndHumidityAndLightAndTime(
            String temperature,
            String humidity,
            String light,
            String time,
            Pageable pageable);

}

