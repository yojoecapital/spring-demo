package com.learning.gateway.filter;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "integration-routing-configuration")
@Data
public class IntegrationRoutingConfiguration {
    private Map<String, String> map;
    private String serviceId;
    private String forwardTo;
}
