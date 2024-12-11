package com.SpringCloudConfigBackend.springCloudconfigBackend.repository;

import com.SpringCloudConfigBackend.springCloudconfigBackend.model.GitResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GitResponseRepository extends JpaRepository<GitResponse, Long> {
    List<GitResponse> findByApplicationAndEnvironment(String application, String environment);
}
