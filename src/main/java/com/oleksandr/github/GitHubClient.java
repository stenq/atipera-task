package com.oleksandr.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class GitHubClient {

    private final RestClient restClient;

    public GitHubClient(@Value("${github.api.url:https://api.github.com}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<RepositoryResponse> fetchRepositories(String username) {
        try {
            List<Map<String, Object>> repos = restClient.get()
                    .uri("/users/{username}/repos", username)
                    .retrieve()
                    .body(List.class);

            if (repos == null) return List.of();

            List<RepositoryResponse> result = new ArrayList<>();

            for (Map<String, Object> repo : repos) {
                boolean fork = (boolean) repo.get("fork");
                if (fork) continue;

                String repoName = (String) repo.get("name");

                Map<String, Object> owner = (Map<String, Object>) repo.get("owner");
                String ownerLogin = (String) owner.get("login");

                List<Map<String, Object>> branches = restClient.get()
                        .uri("/repos/{owner}/{repo}/branches", ownerLogin, repoName)
                        .retrieve()
                        .body(List.class);

                List<BranchInfo> branchInfos = new ArrayList<>();
                for (Map<String, Object> branch : branches) {
                    String branchName = (String) branch.get("name");
                    Map<String, Object> commit = (Map<String, Object>) branch.get("commit");
                    String sha = (String) commit.get("sha");
                    branchInfos.add(new BranchInfo(branchName, sha));
                }

                result.add(new RepositoryResponse(repoName, ownerLogin, branchInfos));
            }

            return result;

        } catch (HttpClientErrorException.NotFound e) {
            throw new GitHubUserNotFoundException(username);
        }
    }
}