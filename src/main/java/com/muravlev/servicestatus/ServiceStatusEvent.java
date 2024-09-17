package com.muravlev.servicestatus;

public class ServiceStatusEvent {

    private final String message;

    public ServiceStatusEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
