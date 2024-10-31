package com.iot_backend.service;

import com.iot_backend.entity.ActionHistory;
import com.iot_backend.entity.DataSensors;
import com.iot_backend.repository.ActionHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ActionHistoryService {

    @Autowired
    private ActionHistoryRepository actionHistoryRepository;

    public Page<ActionHistory> getSearchActionHistory(
            String device,
            String action,
            String time,
            String sortBy,
            String sortDir,
            int page,
            int size) {

        if (device != null && device.trim().isEmpty()) device = null;
        if (action != null && action.trim().isEmpty()) action = null;
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

        return actionHistoryRepository.findByDeviceAndActionAndTime(device, action, time, pageable);
    }
}
