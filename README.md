# GitHub Repositories Explorer

A simple REST API built with Spring Boot 4 that lists non-fork GitHub repositories for a given user, including branch information.

## Tech Stack

- Java 25
- Spring Boot 4.1
- Gradle (Kotlin DSL)
- JUnit 5
- MockRestServiceServer (for integration tests)

## Requirements

- Java 25
- No additional setup required (Gradle wrapper included)

## How to Run

From the project root:

./gradlew bootRun

The application starts on port 8080.

## How to Run Tests

./gradlew test

## API Endpoint

### Get user's non-fork repositories

GET /api/github/{username}/repositories

#### Success Response (200 OK)

[
{
"repositoryName": "Hello-World",
"ownerLogin": "octocat",
"branches": [
{
"name": "main",
"lastCommitSha": "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d"
}
]
}
]

#### User Not Found Response (404 Not Found)

{
"status": 404,
"message": "GitHub user 'username' not found"
}

## Example Usage

curl http://localhost:8080/api/github/stenq/repositories

## Project Structure

The project uses a simple Controller / Service / Client architecture with all classes in a single package (com.oleksandr.github), as recommended in the task description.

## Notes on Testing

The integration tests use Spring's built-in MockRestServiceServer to simulate the GitHub API responses without making real HTTP calls.

The tests cover:
- Happy path: filtering out forks and mapping branches with commit SHAs
- Error path: returning proper 404 response when a GitHub user does not exist
