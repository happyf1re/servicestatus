package com.muravlev.servicestatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ServiceStatusController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceStatusController.class);

    @Autowired
    private ServiceStatusService serviceStatusService;

    @PostMapping("/heartbeat")
    public ResponseEntity<String> receiveHeartbeat(@RequestHeader("Authorization") String token, @RequestBody ServiceRequest request) {
        logger.info("Received heartbeat from service: {}", request.getServiceName());

        if (!isValidToken(token)) {
            logger.warn("Unauthorized access attempt with token: {}", token);
            sendUnauthorizedAccessNotification(token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        serviceStatusService.updateServiceStatus(request.getGuid(), request.getServiceName());
        return ResponseEntity.ok("Heartbeat received successfully.");
    }

    @PostMapping("/register")
    public ResponseEntity<ServiceStatus> registerService(@RequestBody ServiceRequest request) {
        if (serviceStatusService.getServiceByGuid(request.getGuid()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        ServiceStatus serviceStatus = serviceStatusService.registerNewService(
                request.getGuid(),
                request.getServiceName(),
                request.getComment(),
                request.getDeltaT(),
                request.getCompanyName()
        );

        return ResponseEntity.ok(serviceStatus);
    }

    @GetMapping("/services")
    public List<ServiceStatus> getAllServices() {
        return serviceStatusService.getAllServices();
    }

    private void sendUnauthorizedAccessNotification(String token) {
        String message = "Unauthorized access attempt detected with token: " + token;
        // Отправка уведомления в Telegram
    }

    private boolean isValidToken(String token) {
        // Логика проверки токена
        return true; // Заглушка для проверки
    }
}

