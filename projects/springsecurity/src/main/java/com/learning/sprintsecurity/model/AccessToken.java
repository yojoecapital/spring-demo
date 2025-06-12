package com.learning.sprintsecurity.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessToken {
    private String accessToken;

    private List<String> scopes;

    private String tokenType;

    private int expiresIn;
}
