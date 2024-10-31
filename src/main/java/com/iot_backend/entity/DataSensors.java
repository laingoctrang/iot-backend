package com.iot_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Table(name = "data_sensors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataSensors {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double temperature;
    private Double humidity;
    private Double light;
    private Double dust;
    private LocalDateTime time;

//    public DataSensors(Double temperature, Double humidity, Double light, LocalDateTime time) {
//        this.temperature = temperature;
//        this.humidity = humidity;
//        this.light = light;
//        this.time = time;
//    }

    public DataSensors(Double temperature, Double humidity, Double light, Double dust, LocalDateTime time) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.light = light;
        this.dust = dust;
        this.time = time;
    }
}
