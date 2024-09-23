package com.muravlev.servicestatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServiceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guid;
    private Long deltaT;
    private String companyName;
    private String serviceName;
    private LocalDateTime lastUpdateTime;
    private boolean isAlive;
    private String token;
    private String comment;
    // Добавляем поле для хранения предыдущего состояния
    @Transient
    private boolean previousIsAlive;

    // Добавляем метод для обновления состояния и хранения предыдущего
    public void updateAliveStatus(boolean newStatus) {
        this.previousIsAlive = this.isAlive;
        this.isAlive = newStatus;
    }
}
