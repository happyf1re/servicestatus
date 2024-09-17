package com.muravlev.servicestatus;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceStatusRepository extends JpaRepository<ServiceStatus, Long> {

    ServiceStatus findByGuid(String guid);
}
