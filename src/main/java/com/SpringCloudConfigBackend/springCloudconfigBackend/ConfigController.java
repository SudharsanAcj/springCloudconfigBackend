package com.SpringCloudConfigBackend.springCloudconfigBackend;

import com.SpringCloudConfigBackend.springCloudconfigBackend.model.ApplicationConfigRequest;
import com.SpringCloudConfigBackend.springCloudconfigBackend.model.GitResponse;
import com.SpringCloudConfigBackend.springCloudconfigBackend.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/configs")
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private ConfigService configService;

    @GetMapping("/{application}/{environment}")
    public ResponseEntity<List<ApplicationConfigRequest>> getConfigs(
            @PathVariable String application,
            @PathVariable String environment) {
        try {
            logger.info("Fetching configurations for application: {}, environment: {}", application, environment);
            List<ApplicationConfigRequest> configs = configService.getConfigurations(application, environment);
            return ResponseEntity.ok(configs);
        } catch (IOException e) {
            logger.error("Error fetching configurations for application: {}, environment: {}", application, environment, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{application}/{environment}/git-responses")
    public ResponseEntity<List<GitResponse>> getGitResponses(
            @PathVariable String application,
            @PathVariable String environment) {
        logger.info("Fetching git responses for application: {}, environment: {}", application, environment);
        List<GitResponse> responses = configService.getGitResponses(application, environment);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{application}/{environment}")
    public ResponseEntity<GitResponse> createPullRequest(
            @PathVariable String application,
            @PathVariable String environment,
            @RequestBody List<ApplicationConfigRequest> configs) {
        logger.info("Creating pull request for application: {}, environment: {}", application, environment);
        GitResponse gitResponse = configService.createPullRequest(application, environment, configs);
        if (gitResponse != null) {
            logger.info("Pull request created successfully for application: {}, environment: {}", application, environment);
            return ResponseEntity.ok(gitResponse);
        } else {
            logger.error("Error creating pull request for application: {}, environment: {}", application, environment);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
