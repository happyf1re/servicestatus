package com.muravlev.servicestatus;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TestService implements Runnable {
    private final String serviceName;
    private final String guid;
    private boolean active;

    public TestService(String serviceName) {
        this.serviceName = serviceName;
        this.guid = UUID.randomUUID().toString();
        this.active = true;
    }

    public void run() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/api/heartbeat";

        // Заголовки, включая токен авторизации
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer valid_token_for_testing");

        // Тело запроса
        ServiceRequest request = new ServiceRequest();
        request.setGuid(guid);
        request.setServiceName(serviceName);

        HttpEntity<ServiceRequest> entity = new HttpEntity<>(request, headers);

        // Таймер для отключения сервиса через 1 минуту
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                active = false;
                System.out.println(serviceName + " is stopped.");
            }
        }, 30000); // Отключить через 1 минуту

        while (true) {
            if (active) {
                try {
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                    System.out.println("Response from server: " + response.getBody());
                    Thread.sleep(10000); // Отправлять каждые 10 секунд
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Если сервис не активен, просто ждем
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread service1 = new Thread(new TestService("Service1"));
        Thread service2 = new Thread(new TestService("Service2"));
        Thread service3 = new Thread(new TestService("Service3"));
        Thread service4 = new Thread(new TestService("Service4"));

        service1.start();
        service2.start();
        service3.start();
        service4.start();
    }
}
