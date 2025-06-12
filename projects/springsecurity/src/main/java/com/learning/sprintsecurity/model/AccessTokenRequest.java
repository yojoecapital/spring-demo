package com.learning.sprintsecurity.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class AccessTokenRequest {
    private String clientId;
    private String clientSecret;
    private List<String> scopes = new ArrayList<>();
}
