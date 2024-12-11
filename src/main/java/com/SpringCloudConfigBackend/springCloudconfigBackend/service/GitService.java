package com.SpringCloudConfigBackend.springCloudconfigBackend.service;

import com.SpringCloudConfigBackend.springCloudconfigBackend.model.ApplicationConfigRequest;
import com.SpringCloudConfigBackend.springCloudconfigBackend.model.GitResponse;
import com.SpringCloudConfigBackend.springCloudconfigBackend.repository.GitResponseRepository;

import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Service
public class GitService {

    private static final Logger logger = LoggerFactory.getLogger(GitService.class);

    private static final String REPO_NAME = "SudharsanAcj/cloudconfig";
    private static final String GITHUB_TOKEN = "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";

    @Autowired
    private GitResponseRepository gitResponseRepository;

    public List<ApplicationConfigRequest> getConfigurations(String application, String environment) throws IOException {
        logger.debug("Fetching configurations from GitHub for application: {}, environment: {}", application, environment);
        GitHub github = new GitHubBuilder().withOAuthToken(GITHUB_TOKEN).build();
        GHRepository repo = github.getRepository(REPO_NAME);
        String filePath = application + "-" + environment + ".properties";
        GHContent content = repo.getFileContent(filePath);
        String configContent = new String(content.read().readAllBytes());
        return parseConfigs(configContent);
    }

    public GitResponse createPullRequest(String application, String environment, List<ApplicationConfigRequest> configs) {
        try {
            logger.debug("Starting pull request creation process for application: {}, environment: {}", application, environment);
            GitHub github = new GitHubBuilder().withOAuthToken(GITHUB_TOKEN).build();
            GHRepository repo = github.getRepository(REPO_NAME);

            String defaultBranch = repo.getDefaultBranch();
            String sha = repo.getBranch(defaultBranch).getSHA1();

            String branchName = "update-configs-" + application + "-" + environment + "-" + System.currentTimeMillis();
            repo.createRef("refs/heads/" + branchName, sha);

            String filePath = application + "-" + environment + ".properties";
            Map<String, String> newConfigs = configs.stream()
                    .filter(config -> config.getConfigKey() != null && config.getConfigValue() != null)
                    .collect(Collectors.toMap(ApplicationConfigRequest::getConfigKey, ApplicationConfigRequest::getConfigValue));

            String updatedContent;
            try {
                GHContent existingContent = repo.getFileContent(filePath, branchName);
                String existingContentStr = new String(existingContent.read().readAllBytes());
                updatedContent = mergeConfigs(existingContentStr, newConfigs);
                existingContent.update(updatedContent, "Update configurations", branchName);
            } catch (Exception e) {
                if (e.getMessage().contains("404")) {
                    updatedContent = mergeConfigs("", newConfigs);
                    repo.createContent()
                            .content(updatedContent)
                            .path(filePath)
                            .branch(branchName)
                            .message("Create new configurations for " + application + " - " + environment)
                            .commit();
                } else {
                    throw e;
                }
            }

            GHPullRequest pullRequest = repo.createPullRequest("Update Configurations", branchName, defaultBranch, "Please review and merge.");
            GitResponse gitResponse = new GitResponse(String.valueOf(pullRequest.getNumber()), pullRequest.getHtmlUrl().toString(), application, environment);
            gitResponseRepository.save(gitResponse);
            logger.info("Pull request created successfully: {}", pullRequest.getHtmlUrl().toString());
            return gitResponse;
        } catch (IOException e) {
            logger.error("Error creating pull request for application: {}, environment: {}", application, environment, e);
            return null;
        }
    }

    public List<GitResponse> getGitResponses(String application, String environment) {
        logger.debug("Fetching git responses from database for application: {}, environment: {}", application, environment);
        return gitResponseRepository.findByApplicationAndEnvironment(application, environment);
    }

    private List<ApplicationConfigRequest> parseConfigs(String configContent) {
        return Arrays.stream(configContent.split("\n"))
                .map(line -> line.split("=", 2))
                .filter(parts -> parts.length == 2)
                .map(parts -> new ApplicationConfigRequest(parts[0].trim(), parts[1].trim()))
                .collect(Collectors.toList());
    }

    private static String mergeConfigs(String existingContent, Map<String, String> newConfigs) {
        LinkedHashMap<String, String> configMap = new LinkedHashMap<>();

        Arrays.stream(existingContent.split("\n"))
                .map(line -> line.split("=", 2))
                .filter(parts -> parts.length == 2)
                .forEach(parts -> configMap.put(parts[0].trim(), parts[1].trim()));

        newConfigs.forEach(configMap::put);

        return configMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("\n"));
    }
}
