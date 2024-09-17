package com.muravlev.servicestatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledTasks {

    @Autowired
    private ServiceStatusService serviceStatusService;

    // Проверка каждые 30 секунд
    @Scheduled(fixedRate = 30000)
    public void checkServices() {
        serviceStatusService.checkForInactiveServices();
    }
}
