# URL Shortener

A robust and efficient URL shortener service built with Java and Spring Boot. This application allows users to convert long URLs into short, manageable links. It provides features like custom aliases, expiration dates, and click tracking. The service is designed with a clean architecture, comprehensive test coverage, and a full CI/CD pipeline for automated testing and deployment.

## Features

*   **URL Shortening:** Generate unique, short codes (Base62) for long URLs.
*   **Custom Aliases:** Create personalized and memorable short links.
*   **Expiration Control:** Set an expiration period for temporary links.
*   **Redirection:** Seamlessly redirects users from the short link to the original long URL.
*   **Analytics:** Tracks the number of clicks and the last accessed time for each link.
*   **REST API:** A well-defined API for creating and managing short URLs.
*   **Rate Limiting:** Protects the creation endpoint from abuse with IP-based rate limiting (10 requests/minute).
*   **Scheduled Cleanup:** Automatically removes expired URLs from the database daily.
*   **Containerized:** Includes a `Dockerfile` for easy deployment and scaling.
*   **CORS Support:** Configured for integration with web frontends (e.g., from `localhost:3000` (for testing) and `cemtbi.github.io`).

## Technology Stack

*   **Backend:** Java 17, Spring Boot, Spring Data JPA, Spring Web
*   **Database:** PostgreSQL (Production), H2 (Testing)
*   **Build:** Maven
*   **Testing:** JUnit 5, Mockito, Testcontainers
*   **API Documentation:** Springdoc OpenAPI (Swagger UI)
*   **Others:** Lombok, Bucket4j (Rate Limiting)
*   **DevOps:** Docker, GitHub Actions

## API Endpoints

The service exposes REST endpoints for managing URLs. The full API documentation is available via Swagger UI at `/swagger-ui.html` when the application is running.

### Create a Short URL

*   **Endpoint:** `POST /api/urls`
*   **Description:** Creates a new short URL.
*   **Request Body:**
    ```json
    {
      "url": "https://github.com/CemTbi/UrlShortener",
      "alias": "my-repo", // Optional: 3-32 alphanumeric characters and underscores
      "expiryDays": 7     // Optional: Number of days until the link expires
    }
    ```
*   **Responses:**
    *   `201 Created`: Successfully created the short URL. The response body contains the details of the created link.
    *   `400 Bad Request`: Invalid request body (e.g., malformed URL, invalid alias).
    *   `429 Too Many Requests`: Triggered if the rate limit is exceeded.

### Retrieve Short URL Details

*   **Endpoint:** `GET /api/urls/{code}`
*   **Description:** Retrieves the details of a specific short URL without redirecting.
*   **Path Variable:**
    *   `code`: The unique code or alias of the short URL.
*   **Responses:**
    *   `200 OK`: Returns the short URL details.
    *   `404 Not Found`: The specified code does not exist or has expired.

### Redirect to Original URL

*   **Endpoint:** `GET /{code}`
*   **Description:** Redirects the user to the original long URL associated with the code and increments the click count.
*   **Path Variable:**
    *   `code`: The unique code or alias of the short URL.
*   **Responses:**
    *   `302 Found`: Redirects to the original URL.
    *   `404 Not Found`: The specified code does not exist or has expired.

## Getting Started

### Prerequisites

*   Java 17
*   Maven
*   Docker (for containerized deployment)

### Running Locally

1.  Clone the repository:
    ```bash
    git clone https://github.com/CemTbi/UrlShortener.git
    cd UrlShortener
    ```

2.  Run the application using the Maven wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start using the `test` profile by default, which connects to an in-memory H2 database.
    *   **Application:** `http://localhost:10000`
    *   **H2 Console:** `http://localhost:10000/h2-console`
    *   **Swagger UI:** `http://localhost:10000/swagger-ui.html`

### Running with Docker

The repository includes a `Dockerfile` to build and run the application in a container.

1.  Build the Docker image:
    ```bash
    docker build -t urlshortener .
    ```

2.  Run the container. For production use, you must provide the database connection details as environment variables and activate the `prod` profile.
    ```bash
    docker run -p 10000:10000 \
      -e SPRING_PROFILES_ACTIVE=prod \
      -e DB_HOST=<your_db_host> \
      -e DB_NAME=<your_db_name> \
      -e DB_USER=<your_db_user> \
      -e DB_PASSWORD=<your_db_password> \
      urlshortener
    ```

## Configuration

The application uses Spring Profiles to manage different configurations:

*   `application-test.properties`: (Default) Uses an in-memory H2 database for local development and testing.
*   `application-prod.properties`: Configured for a PostgreSQL database. It requires the following environment variables:
    *   `DB_HOST`: The hostname of the PostgreSQL server.
    *   `DB_NAME`: The database name.
    *   `DB_USER`: The database user.
    *   `DB_PASSWORD`: The user's password.
    *   `PORT`: (Optional) The port for the application server.

## Testing

The project is equipped with a comprehensive suite of unit and integration tests.

To run all tests, execute the following command from the root directory:
```bash
./mvnw verify
```
Test coverage reports for both unit and integration tests are generated by JaCoCo in the `target/site/` directory.

## CI/CD Pipeline

This repository is configured with GitHub Actions to automate the build, test, and deployment process. The workflows handle:

*   Running builds, unit tests, and integration tests on every push and pull request.
*   Performing static code analysis with SonarCloud.
*   Scanning for vulnerabilities in code and Docker images with Trivy.
*   Building and pushing the Docker image to GitHub Container Registry (GHCR) on pushes to the `main` branch.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=CemTbi_UrlShortener&metric=alert_status&token=22d62a353d7d5fb8cb13d823b8f5a99bcb586f67)](https://sonarcloud.io/summary/new_code?id=CemTbi_UrlShortener)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=CemTbi_UrlShortener&metric=ncloc&token=22d62a353d7d5fb8cb13d823b8f5a99bcb586f67)](https://sonarcloud.io/summary/new_code?id=CemTbi_UrlShortener)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=CemTbi_UrlShortener&metric=reliability_rating&token=22d62a353d7d5fb8cb13d823b8f5a99bcb586f67)](https://sonarcloud.io/summary/new_code?id=CemTbi_UrlShortener)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=CemTbi_UrlShortener&metric=security_rating&token=22d62a353d7d5fb8cb13d823b8f5a99bcb586f67)](https://sonarcloud.io/summary/new_code?id=CemTbi_UrlShortener)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=CemTbi_UrlShortener&metric=sqale_rating&token=22d62a353d7d5fb8cb13d823b8f5a99bcb586f67)](https://sonarcloud.io/summary/new_code?id=CemTbi_UrlShortener)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=CemTbi_UrlShortener&metric=vulnerabilities&token=22d62a353d7d5fb8cb13d823b8f5a99bcb586f67)](https://sonarcloud.io/summary/new_code?id=CemTbi_UrlShortener)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=CemTbi_UrlShortener&metric=bugs&token=22d62a353d7d5fb8cb13d823b8f5a99bcb586f67)](https://sonarcloud.io/summary/new_code?id=CemTbi_UrlShortener)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=CemTbi_UrlShortener&metric=code_smells&token=22d62a353d7d5fb8cb13d823b8f5a99bcb586f67)](https://sonarcloud.io/summary/new_code?id=CemTbi_UrlShortener)
