package com.iot_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "action_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "device")
    private String device;

    @Column(name = "action")
    private String action;

    @Column(name = "time")
    private LocalDateTime time;

    public ActionHistory(String device, String action, LocalDateTime time) {
        this.device = device;
        this.action = action;
        this.time = time;
    }
}
