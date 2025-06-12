package com.learning.sprintsecurity.model;

import java.util.List;

import lombok.Data;

@Data
public class CreateClientKeysRequest {
    private List<String> defaultScopes;
    private int defaultMaxAge;
    private String userPassword;
}
