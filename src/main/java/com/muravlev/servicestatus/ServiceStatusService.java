package com.muravlev.servicestatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


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
            service.setLastUpdateTime(LocalDateTime.now());
            service.setAlive(true);
            repository.save(service);
        }
    }

    // Регистрация нового сервиса с токеном и комментарием
    public ServiceStatus registerNewService(String guid, String serviceName, String comment) {
        logger.info("Registering new service: {}", serviceName);

        ServiceStatus service = new ServiceStatus();
        service.setGuid(UUID.randomUUID().toString());  // Генерация нового GUID
        service.setServiceName(serviceName);
        service.setComment(comment);
        service.setAlive(true);
        service.setLastUpdateTime(LocalDateTime.now());
        service.setToken(UUID.randomUUID().toString());  // Генерация токена

        repository.save(service);  // Сохранение в базу данных
        return service;
    }

    @Scheduled(fixedRate = 10000)  // Проверка каждые 10 секунд
    @Transactional
    public void checkForInactiveServices() {
        LocalDateTime now = LocalDateTime.now();
        List<ServiceStatus> services = repository.findAll();
        for (ServiceStatus service : services) {
            logger.info("Checking service: {}. Last update time: {}", service.getServiceName(), service.getLastUpdateTime());

            if (service.isAlive() && service.getLastUpdateTime().isBefore(now.minusSeconds(10))) {
                service.setAlive(false);
                repository.save(service);
                logger.warn("Service {} is down. Updating status to dead.", service.getServiceName());
                sendTelegramNotification("Service " + service.getServiceName() + " is down!");
            }
        }
    }

    private void sendTelegramNotification(String message) {
        logger.info("Sending Telegram notification: {}", message);
        eventPublisher.publishEvent(new ServiceStatusEvent(message));
    }
}


