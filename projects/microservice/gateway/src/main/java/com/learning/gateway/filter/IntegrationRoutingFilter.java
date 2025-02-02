package com.learning.gateway.filter;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class IntegrationRoutingFilter extends ZuulFilter {

    @Autowired
    private IntegrationRoutingConfiguration configuration;

    @Override
    public String filterType() {
        return "route"; 
    }

    @Override
    public int filterOrder() {
        return 1; 
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String host = request.getHeader("Host");
        if (host == null)
            return false;
        String subdomain = extractSubdomain(host);
        if (subdomain == null)
            return false;
        System.out.println("PASSING SHOULD FILTER");
        return configuration.getMap().containsKey(subdomain); 
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String host = request.getHeader("Host");
        if (host == null)
            return null;
        String subdomain = extractSubdomain(host);
        if (subdomain == null)
            return null;
        String integrationPath = configuration.getMap().get(subdomain);
        if (integrationPath == null)
            return null;
        String requestURI = request.getRequestURI();
        String newURI = integrationPath + requestURI;
        ctx.set("requestURI", newURI);
        System.out.println("FORWARDING FROM " + host + " TO " + newURI);
        return null;
    }

    private String extractSubdomain(final String host) {
        if (host.contains(".")) {
            return host.split("\\.")[0];
        }
        return null;
    }
}
