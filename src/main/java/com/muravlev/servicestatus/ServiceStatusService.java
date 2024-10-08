package com.muravlev.servicestatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static ch.qos.logback.core.spi.ComponentTracker.DEFAULT_TIMEOUT;


@Service
public class ServiceStatusService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceStatusService.class);

    private final ServiceStatusRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ServiceStatusService(ServiceStatusRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public List<ServiceStatus> getAllServices() {
        logger.info("Fetching all services");
        return repository.findAll();
    }

    public ServiceStatus getServiceByGuid(String guid) {
        logger.info("Fetching service by GUID: {}", guid);
        return repository.findByGuid(guid);
    }

    public void updateServiceStatus(String guid, String serviceName) {
        ServiceStatus service = repository.findByGuid(guid);
        if (service == null) {
            logger.info("Service not found. Registering new service: {}", serviceName);
            service = new ServiceStatus();
            service.setGuid(guid);
            service.setServiceName(serviceName);
            service.setAlive(true);
            service.setLastUpdateTime(LocalDateTime.now());
            repository.save(service);
        } else {
            service.updateAliveStatus(true);
            service.setLastUpdateTime(LocalDateTime.now());
            repository.save(service);

            // Отправляем уведомление только если сервис перешел из DEAD в ALIVE
            if (!service.isPreviousIsAlive()) {
                sendTelegramNotification("Service " + service.getServiceName() + " is now alive!");
            }
        }
    }

    // Регистрация нового сервиса с токеном и комментарием
    public ServiceStatus registerNewService(String guid, String serviceName, String comment, Long deltaT, String companyName) {
        logger.info("Registering new service: {}", serviceName);

        ServiceStatus service = new ServiceStatus();
        service.setGuid(UUID.randomUUID().toString());
        service.setServiceName(serviceName);
        service.setComment(comment);
        service.setAlive(false);
        service.setCompanyName(companyName);
        service.setLastUpdateTime(LocalDateTime.now());
        service.setToken(UUID.randomUUID().toString());
        service.setDeltaT(deltaT); // Устанавливаем deltaT

        repository.save(service);
        return service;
    }

    @Scheduled(fixedRate = 10000)  // Проверка каждые 10 секунд
    @Transactional
    public void checkForInactiveServices() {
        LocalDateTime now = LocalDateTime.now();
        List<ServiceStatus> services = repository.findAll();
        for (ServiceStatus service : services) {
            long secondsSinceLastUpdate = ChronoUnit.SECONDS.between(service.getLastUpdateTime(), now);
            long timeout = service.getDeltaT() != null ? service.getDeltaT() : DEFAULT_TIMEOUT;

            if (service.isAlive() && secondsSinceLastUpdate > timeout) {
                service.updateAliveStatus(false);
                repository.save(service);

                if (service.isPreviousIsAlive()) {
                    sendTelegramNotification("Service " + service.getServiceName() + " is down!");
                }
            }
        }
    }

    private void sendTelegramNotification(String message) {
        logger.info("Sending Telegram notification: {}", message);
        eventPublisher.publishEvent(new ServiceStatusEvent(message));
    }
}


