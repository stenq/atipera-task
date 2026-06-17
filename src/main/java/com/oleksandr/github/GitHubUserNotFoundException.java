package com.oleksandr.github;

public class GitHubUserNotFoundException extends RuntimeException {
    public GitHubUserNotFoundException(String username) {
        super("GitHub user '" + username + "' not found");
    }
}