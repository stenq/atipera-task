package com.oleksandr.github;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitHubService {

    private final GitHubClient client;

    public GitHubService(GitHubClient client) {
        this.client = client;
    }

    public List<RepositoryResponse> getRepositories(String username) {
        return client.fetchRepositories(username);
    }
}