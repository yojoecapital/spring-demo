package com.learning.sprintsecurity.service;

import java.security.SecureRandom;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private HttpHeaders createHeaders(String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-OpenAM-Username", amadminUsername);
        headers.set("X-OpenAM-Password", amadminPassword);
        log.info("Creating headers for OpenAM authentication with: " + amadminUsername + ", " + amadminPassword);
        AdminTokenId adminTokenId = restTemplate.exchange(
            AUTHENTICATION_URL, 
            HttpMethod.POST, 
            new HttpEntity<>(headers), 
            AdminTokenId.class
        ).getBody();
        log.info("Admin ID Token: " + adminTokenId.getTokenId());
        HttpHeaders returnHeaders = new HttpHeaders();
        returnHeaders.set("iPlanetDirectoryPro", adminTokenId.getTokenId());
        returnHeaders.set("Content-Type", contentType);
        returnHeaders.set("Accept-API-Version", "resource=1.0");
        return returnHeaders;
    }

    private void debugJsonPayload(Object payload) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);
            log.info(json);
        } catch (Exception e) {
            log.warning("Failed to serialize: " + e.getMessage());
        }
    }

    public ClientKeys createOAuth2Client(String business, CreateClientKeysRequest createClientKeysRequest) {
        String clientId = business + "-" + System.currentTimeMillis();
        String clientSecret = generatePassword();
        String url = OAUTH2_CLIENT_URL + "/" + clientId;
        OpemAmOAuth2Client oAuth2Client = new OpemAmOAuth2Client(
            business, clientSecret, createClientKeysRequest.getDefaultMaxAge(), createClientKeysRequest.getDefaultScopes() 
        );
        log.info("Creating OAuth2 client: " + oAuth2Client);
        debugJsonPayload(oAuth2Client);
        restTemplate.exchange(
            url, HttpMethod.PUT, 
            new HttpEntity<>(oAuth2Client, createHeaders("application/json")), 
            Void.class
        );
        return new ClientKeys(clientId, clientSecret);
    }

    public void deleteOAuth2Client(String clientId) {
        String url = OAUTH2_CLIENT_URL + "/" + clientId;
        log.info("Deleting OAuth2 client for business: " + clientId);
        restTemplate.exchange(
            url, HttpMethod.DELETE, 
            new HttpEntity<>(createHeaders("application/json")), 
            Void.class
        );
    }

    public AccessToken getAccessToken(AccessTokenRequest accessTokenRequest) {
        String url = ACCESS_TOKEN_URL + String.format(
            "?grant_type=client_credentials&client_id=%s&client_secret=%s", 
            accessTokenRequest.getClientId(), accessTokenRequest.getClientSecret()
        );
        if (!accessTokenRequest.getScopes().isEmpty()) {
            url += "&scope=" + String.join(" ", accessTokenRequest.getScopes());
        }
        log.info("Requesting access token for client: " + accessTokenRequest.getClientId());
        log.info(url);
        OpenAmAccessToken openAmAccessToken = restTemplate.exchange(
            url, HttpMethod.POST, 
            new HttpEntity<>(createHeaders("application/x-www-form-urlencoded")), 
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
