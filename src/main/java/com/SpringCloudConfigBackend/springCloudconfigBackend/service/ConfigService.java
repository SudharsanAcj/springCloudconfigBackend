package com.SpringCloudConfigBackend.springCloudconfigBackend.service;

import com.SpringCloudConfigBackend.springCloudconfigBackend.model.ApplicationConfigRequest;
import com.SpringCloudConfigBackend.springCloudconfigBackend.model.GitResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    @Autowired
    private GitService gitService;

    public List<ApplicationConfigRequest> getConfigurations(String application, String environment) throws IOException {
        logger.debug("Getting configurations for application: {}, environment: {}", application, environment);
        return gitService.getConfigurations(application, environment);
    }

    public GitResponse createPullRequest(String application, String environment, List<ApplicationConfigRequest> configs) {
        logger.debug("Creating pull request for application: {}, environment: {}", application, environment);
        return gitService.createPullRequest(application, environment, configs);
    }

    public List<GitResponse> getGitResponses(String application, String environment) {
        logger.debug("Getting git responses for application: {}, environment: {}", application, environment);
        return gitService.getGitResponses(application, environment);
    }
}
