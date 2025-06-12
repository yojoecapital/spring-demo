package com.learning.sprintsecurity.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientKeys {
    private String clientId;
    private String clientSecret;
}
