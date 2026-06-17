package com.oleksandr.github;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GitHubControllerIntegrationTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("github.api.url", wireMock::baseUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnNonForkRepositoriesWithBranches() throws Exception {
        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/users/testuser/repos"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            [
                              {"name": "repo1", "fork": false, "owner": {"login": "testuser"}},
                              {"name": "repo2", "fork": true,  "owner": {"login": "testuser"}}
                            ]
                            """)));

        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/repos/testuser/repo1/branches"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            [
                              {"name": "main", "commit": {"sha": "abc123"}}
                            ]
                            """)));

        mockMvc.perform(get("/api/github/testuser/repositories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].repositoryName").value("repo1"))
                .andExpect(jsonPath("$[0].ownerLogin").value("testuser"))
                .andExpect(jsonPath("$[0].branches[0].name").value("main"))
                .andExpect(jsonPath("$[0].branches[0].lastCommitSha").value("abc123"));
    }

    @Test
    void shouldReturn404WhenUserDoesNotExist() throws Exception {
        wireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/users/nonexistent/repos"))
                .willReturn(WireMock.aResponse().withStatus(404)));

        mockMvc.perform(get("/api/github/nonexistent/repositories"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }
}