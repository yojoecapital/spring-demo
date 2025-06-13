package com.learning.sprintsecurity.service;

import java.security.SecureRandom;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.learning.sprintsecurity.model.AccessToken;
import com.learning.sprintsecurity.model.AccessTokenRequest;
import com.learning.sprintsecurity.model.AdminTokenId;
import com.learning.sprintsecurity.model.ClientKeys;
import com.learning.sprintsecurity.model.CreateClientKeysRequest;
import com.learning.sprintsecurity.model.OpemAmOAuth2Client;
import com.learning.sprintsecurity.model.OpenAmAccessToken;

import lombok.extern.java.Log;

@Log
@Service
public class OpenAmService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openam.amadmin.username}")
    private String amadminUsername;

    @Value("${openam.amadmin.password}")
    private String amadminPassword;

    private static final String OAUTH2_CLIENT_URL = "http://localhost:8080/openam/json/realms/root/realm-config/agents/OAuth2Client";
    private static final String AUTHENTICATION_URL = "http://localhost:8080/openam/json/realms/root/authenticate";
    private static final String ACCESS_TOKEN_URL = "http://localhost:8080/openam/oauth2/access_token";

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int PASSWORD_LENGTH = 16;

    private static String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int idx = random.nextInt(PASSWORD_CHARS.length());
            sb.append(PASSWORD_CHARS.charAt(idx));
        }
        return sb.toString();
    }

    private void setIPlanetDirectoryProHeader(HttpHeaders headers) {
        HttpHeaders amadminHeaders = new HttpHeaders();
        amadminHeaders.set("X-OpenAM-Username", amadminUsername);
        amadminHeaders.set("X-OpenAM-Password", amadminPassword);
        log.info("Creating headers for OpenAM authentication with: " + amadminUsername + ", " + amadminPassword);
        AdminTokenId adminTokenId = restTemplate.exchange(
            AUTHENTICATION_URL, 
            HttpMethod.POST, 
            new HttpEntity<>(amadminHeaders), 
            AdminTokenId.class
        ).getBody();
        log.info("Admin ID Token: " + adminTokenId.getTokenId());
        headers.set("iPlanetDirectoryPro", adminTokenId.getTokenId());
    }

    private void setHeaders(HttpHeaders headers, String contentType) {
        headers.set("Content-Type", contentType);
        headers.set("Accept-API-Version", "resource=1.0");
    }

    public ClientKeys createOAuth2Client(String business, CreateClientKeysRequest createClientKeysRequest) {
        String clientId = business + "-" + System.currentTimeMillis();
        String clientSecret = generatePassword();
        String url = OAUTH2_CLIENT_URL + "/" + clientId;
        OpemAmOAuth2Client oAuth2Client = new OpemAmOAuth2Client(
            business, clientSecret, createClientKeysRequest.getDefaultMaxAge(), createClientKeysRequest.getDefaultScopes() 
        );
        HttpHeaders headers = new HttpHeaders();
        setIPlanetDirectoryProHeader(headers);
        setHeaders(headers, "application/json");
        log.info("Creating OAuth2 client: " + oAuth2Client);
        restTemplate.exchange(
            url, HttpMethod.PUT, 
            new HttpEntity<>(oAuth2Client, headers), 
            Void.class
        );
        return new ClientKeys(clientId, clientSecret);
    }

    public void deleteOAuth2Client(String clientId) {
        String url = OAUTH2_CLIENT_URL + "/" + clientId;
        HttpHeaders headers = new HttpHeaders();
        setIPlanetDirectoryProHeader(headers);
        setHeaders(headers, "application/json");
        log.info("Deleting OAuth2 client for business: " + clientId);
        restTemplate.exchange(
            url, HttpMethod.DELETE, 
            new HttpEntity<>(headers), 
            Void.class
        );
    }

    public AccessToken getAccessToken(AccessTokenRequest accessTokenRequest, String business) {
        String claimsJson = String.format("{\"business\":\"%s\"}", business);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(ACCESS_TOKEN_URL)
            .queryParam("grant_type", "client_credentials")
            .queryParam("client_id", accessTokenRequest.getClientId())
            .queryParam("client_secret", accessTokenRequest.getClientSecret())
            .queryParam("claims", claimsJson);
        if (!accessTokenRequest.getScopes().isEmpty()) {
            uriComponentsBuilder.queryParam("scope", String.join(" ", accessTokenRequest.getScopes()));
        }
        log.info("Requesting access token for client: " + accessTokenRequest.getClientId());
        log.info(uriComponentsBuilder.toUriString());
        HttpHeaders headers = new HttpHeaders();
        setHeaders(headers, "application/x-www-form-urlencoded");
        UriComponents uriComponents = uriComponentsBuilder.build().encode();
        OpenAmAccessToken openAmAccessToken = restTemplate.exchange(
            uriComponents.toUri(), HttpMethod.POST,
            new HttpEntity<>(headers),
            OpenAmAccessToken.class
        ).getBody();
        return new AccessToken(
            openAmAccessToken.getAccessToken(),
            Arrays.asList(openAmAccessToken.getScope().split(" ")),
            openAmAccessToken.getTokenType(),
            openAmAccessToken.getExpiresIn()
        );
    }
}
