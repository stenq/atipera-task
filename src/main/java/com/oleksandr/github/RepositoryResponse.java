package com.oleksandr.github;

import java.util.List;

public class RepositoryResponse {

    private String repositoryName;
    private String ownerLogin;
    private List<BranchInfo> branches;

    public RepositoryResponse(String repositoryName, String ownerLogin, List<BranchInfo> branches) {
        this.repositoryName = repositoryName;
        this.ownerLogin = ownerLogin;
        this.branches = branches;
    }

    public String getRepositoryName() { return repositoryName; }
    public String getOwnerLogin() { return ownerLogin; }
    public List<BranchInfo> getBranches() { return branches; }
}