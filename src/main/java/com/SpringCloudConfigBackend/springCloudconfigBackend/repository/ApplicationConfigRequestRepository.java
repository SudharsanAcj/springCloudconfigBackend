package com.SpringCloudConfigBackend.springCloudconfigBackend.repository;

import com.SpringCloudConfigBackend.springCloudconfigBackend.model.ApplicationConfigRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationConfigRequestRepository extends JpaRepository<ApplicationConfigRequest, Long> {
    List<ApplicationConfigRequest> findByApplicationAndEnvironment(String application, String environment);
}
