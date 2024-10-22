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
                timeEnd = timeStart.plusSeconds(1); // Tìm đúng giây của bản ghi
            } else {
                // Nếu chỉ có `date`, tìm kiếm trong cả ngày
                timeStart = parsedDate.atStartOfDay(); // Bắt đầu từ 00:00:00
                timeEnd = parsedDate.atTime(LocalTime.MAX); // Kết thúc tại 23:59:59
            }
        } else if (time != null && !time.isEmpty()) {
            // Nếu chỉ có `time`, tìm tất cả các bản ghi có `time` tương ứng (bất kể ngày)
            LocalTime parsedTime = LocalTime.parse(time); // Parse time theo HH:mm:ss
            timeStart = LocalDateTime.of(LocalDate.of(2024, 1, 1), parsedTime); // Sử dụng ngày giả định
            timeEnd = LocalDateTime.now();
        }

        return actionHistoryRepository.findByDeviceAndActionAndTime(device, action, timeStart, timeEnd, pageable);
    }
}
