package com.iot_backend.repository;

import com.iot_backend.entity.ActionHistory;
import com.iot_backend.entity.DataSensors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActionHistoryRepository extends JpaRepository<ActionHistory, Long> {


    @Query("SELECT a FROM ActionHistory a WHERE " +
            "(?1 IS NULL OR CAST(a.device AS string) LIKE CONCAT(?1, '%')) AND " +
            "(?2 IS NULL OR CAST(a.action AS string) LIKE CONCAT(?2, '%')) AND " +
            "(?3 IS NULL OR a.time >= ?3) AND " +
            "(?4 IS NULL OR a.time <= ?4)")
    Page<ActionHistory> findByDeviceAndActionAndTime(String device, String action, LocalDateTime timeStart, LocalDateTime timeEnd, Pageable pageable);

}
