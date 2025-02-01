package com.learning.gateway.filter;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.route.SendForwardFilter;
import org.springframework.stereotype.Component;
import com.netflix.zuul.context.RequestContext;

@Component
public class IntegrationFilter extends SendForwardFilter {

    @Autowired
    private IntegrationProperties integrationProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String serverName = request.getServerName();
        System.out.println("HOST: " + serverName);
        String subdomain = getSubdomain(serverName);
        System.out.println("SUBDOMAIN: " + subdomain);
        if (subdomain != null) {
            System.out.println("SUBDOMAIN ACCEPTED");
            Map<String, String> map = integrationProperties.getMap();
            String forwardTo = map.getOrDefault(subdomain, null);
            if (forwardTo != null) {
                System.out.println("Forwarding request to: " + forwardTo);
                String requestURI = request.getRequestURI();
                String mappedURI = forwardTo + requestURI;
                System.out.println("Mapped URI: " + mappedURI);
            }
        }
        return null;
    }

    private String getSubdomain(String host) {
        String[] parts = host.split("\\.");
        if (parts.length >= 2) return parts[0];
        return null;
    }
}
