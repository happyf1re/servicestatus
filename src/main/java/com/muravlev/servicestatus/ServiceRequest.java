package com.muravlev.servicestatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceRequest {
    private String guid;
    private String serviceName;
    private String comment;
}
