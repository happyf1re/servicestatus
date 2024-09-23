package com.muravlev.servicestatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceRequest {
    private String guid;
    private String serviceName;
    private String comment;
    private String companyName;
    private Long deltaT; // Это добавили
}
