# 6. Spring Security & OAuth2 Resource Server

> These notes use the code in the `springsecurity` project.

## Overview

This project demonstrates how to secure REST APIs using Spring Security and OAuth2 JWT tokens, acting as a resource server. It also shows how to integrate with an external OpenAM server for OAuth2 client and token management.

## Key Concepts

- **Spring Security**: provides authentication and authorization for Java applications
- **OAuth2 Resource Server**: the app validates JWTs issued by an external authorization server (OpenAM)
- **Custom Authorization**: uses custom logic to restrict access to endpoints based on JWT scopes and claims

## Dependencies

See `pom.xml` for details. Main dependencies:

- `spring-boot-starter-web`
- `spring-boot-starter-security`
- `spring-boot-starter-oauth2-resource-server`

## Configuration

See `src/main/resources/application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8080/openam/oauth2
server:
  port: 8081
logging:
  level:
    org.springframework.security: DEBUG
openam:
  amadmin:
    username: amadmin
    password: helloworld02
```

The `spring.security.resourceserver.jwt.issuer-uri` property is the URL to the public JWK (JSON web key). This will be discussing in the **OpenAM Setup** section.

## Security Setup

See `SecurityConfig.java`:

- disables CSRF (for APIs)
- secures `/hello` (authenticated)
- secures `/books/**` and `/movies/**` with custom `ScopedAuthorizationManager` (checks JWT scopes)
- all other endpoints are public
- configures JWT resource server

## Endpoints

- `GET /hello` — Requires authentication, returns the username from the JWT
- `GET/POST/DELETE /books?business=...` — Requires `books` scope
- `GET/POST/DELETE /movies?business=...` — Requires `movies` scope
- `POST /api-keys/{business}` — Creates OAuth2 client credentials for a business (calls OpenAM)
- `POST /access-token` — Exchanges client credentials for an access token (calls OpenAM)

## Custom Authorization

- `ScopedAuthorizationManager` checks that the JWT contains the required scope for the resource (e.g., `SCOPE_books` for `/books/**`)
- optionally, can check for a `business` claim in the JWT

## OpenAM Integration

- `OpenAmService` handles communication with OpenAM for client and token management
- `ApiKeysController` and `AccessTokenController` expose endpoints to create clients and fetch tokens

## Usage

1. start OpenAM on `localhost:8080` and configure it as an OAuth2 authorization server. Follow the steps in **OpenAM Setup** for this
2. start this Spring Boot app (`mvn spring-boot:run` in `projects/springsecurity`)
3. use `/api-keys/{business}` to create a client for a business
4. use `/access-token` to get a JWT for that client
5. call `/books`, `/movies`, or `/hello` with the JWT in the `Authorization: Bearer ...` header

## OpenAM Setup

1. to start the OpenAM server, run `./run-openam.sh`
2. open up to http://localhost:8080/openam in your browser
3. select "Create Default Configuration"
4. agree to the license agreement (unless you don't actually agree...)
5. set the Default User (`amAdmin`) password as `helloworld02`
6. set the Default Policy Agent password as whatever you want
7. when setup is done, login as the `amAdmin` user
8. click the "Top Level Realm" realm
9. click the "Services" tab on the left hand side
10. enable the OAuth 2.0 provider by clicking "+ Add a Service" and adding "OAuth2 Provider"
11. in the OAuth2 Provider settings, use these options:
    - enable "Use Stateless Access & Refresh Tokens"
    - disable "Issue Refresh Tokens"
    - disable "Issue Refresh Tokens on Refreshing Access Tokens"
    - change "OAuth2 Token Signing Algorithm" from `HS256` to `RS256` to use a public/private key pairing
    - enable "Enable "claims_parameter_supported"
    - enable "Always return claims in ID Tokens"


### What is `HS256` and `RS256` key signing?

- **HS256** (HMAC with SHA-256): uses a single shared secret key for both signing and verifying JWTs. Simpler, but both the issuer and the resource server must know the same secret
- **RS256** (RSA Signature with SHA-256): uses a private key to sign the JWT and a public key to verify it. More secure for distributed systems, since only the public key needs to be shared with resource servers

In most production OAuth2 setups, `RS256` is preferred because it allows the authorization server to keep its private key secret while exposing only the public key for verification.

## References

- https://www.baeldung.com/spring-security-oauth-resource-server
- https://forgerock.github.io/openam-community-edition/
- https://auth0.com/blog/rs256-vs-hs256-whats-the-difference/
