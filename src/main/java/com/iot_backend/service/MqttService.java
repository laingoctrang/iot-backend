package com.iot_backend.service;

import com.iot_backend.config.MqttConfig;
import com.iot_backend.entity.ActionHistory;
import com.iot_backend.entity.DataSensors;
import com.iot_backend.repository.ActionHistoryRepository;
import com.iot_backend.repository.DataSensorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class MqttService {
    @Autowired
    private MqttConfig.MqttGateway mqttGateway;
    @Autowired
    private DataSensorsRepository dataSensorsRepository;
    @Autowired
    private ActionHistoryRepository actionHistoryRepository;

    // ConcurrentHashMap để quản lý latch cho từng thiết bị
    private ConcurrentHashMap<String, CountDownLatch> latchMap = new ConcurrentHashMap<>();

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMqttMessage(Message<String> message) {
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        String payload = message.getPayload();

        if ("topic/sensor".equals(topic)) {
            // Xử lý dữ liệu cảm biến, lưu vào DB
            DataSensors dataSensors = new DataSensors();
            String[] parseDataSensors = payload.split(",");
            dataSensors.setTemperature(Double.parseDouble(parseDataSensors[0]));
            dataSensors.setHumidity(Double.parseDouble(parseDataSensors[1]));
            dataSensors.setLight(Double.parseDouble(parseDataSensors[2]));
            dataSensors.setTime(LocalDateTime.now());
            dataSensorsRepository.save(dataSensors);
            System.out.println("Data sensors saved: " + payload);
        } else if ("topic/control".equals(topic)) {
            // Xử lý phản hồi từ thiết bị điều khiển
            System.out.println("Received control response: " + payload);
        } else if ("topic/status".equals(topic)) {
            String[] controlResponse = payload.split("_");
            String device = controlResponse[0];  // fan/ac/bulb
            String status = controlResponse[1];  // on/off

            // Cập nhật lịch sử hành động vào DB
            ActionHistory actionHistory = new ActionHistory();
            switch (device) {
                case "fan":
                    actionHistory.setDevice("Fan");
                    break;
                case "ac":
                    actionHistory.setDevice("Air conditioner");
                    break;
                case "bulb":
                    actionHistory.setDevice("Bulb");
                    break;
            }
            actionHistory.setAction(status.toUpperCase());
            actionHistory.setTime(LocalDateTime.now());
            actionHistoryRepository.save(actionHistory);

            System.out.println("Action history saved: " + device + " " + status);

            // Đếm ngược latch của thiết bị đó nếu tồn tại
            CountDownLatch latch = latchMap.get(device);
            if (latch != null) {
                latch.countDown();
                latchMap.remove(device);  // Xóa latch sau khi đã sử dụng
            }
        }
    }

    public boolean controlDevice(String device, String action) {
        // Tạo tin nhắn điều khiển
        String message = device + "_" + action;
        CountDownLatch latch = new CountDownLatch(1); // Tạo latch mới cho thiết bị

        // Lưu latch vào bản đồ
        latchMap.put(device, latch);

        try {
            mqttGateway.sendToMqtt(message, "topic/control");
            System.out.println("Sent control message: " + message);

            // Đợi phản hồi từ thiết bị hoặc timeout sau 15 giây
            boolean success = latch.await(15, TimeUnit.SECONDS);
            if (success) {
                return true;
            } else {
                System.out.println("Timeout waiting for action history of " + device);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
