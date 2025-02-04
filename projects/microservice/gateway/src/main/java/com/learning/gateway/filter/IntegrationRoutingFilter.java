package com.learning.gateway.filter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class IntegrationRoutingFilter extends ZuulFilter {

    @Autowired
    private IntegrationRoutingConfiguration configuration;

    @Autowired
    private EurekaClient eurekaClient;

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
        RequestContext context = RequestContext.getCurrentContext();
        String serviceId = (String) context.get("serviceId");
        if (!serviceId.equals(configuration.getServiceId()))
            return false;
        String subdomain = extractSubdomain(context);
        return subdomain != null && configuration.getMap().containsKey(subdomain);
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        String subdomain = extractSubdomain(context);
        if (subdomain == null)
            return null;
        Application application =
                eurekaClient.getApplication(configuration.getForwardTo());
        List<InstanceInfo> instances = application.getInstances();
        if (instances.isEmpty()) {
            System.out.println("No instances available for: " + configuration.getForwardTo());
            return null;
        }
        InstanceInfo instance = (InstanceInfo)instances.get(0);
        String requestURI = configuration.getMap().get(subdomain) + context.get("requestURI");
        String homePageUrl = instance.getHomePageUrl();
        try {
            URL url = new URL(homePageUrl + requestURI);
            System.out.println("URL: " + url);
            context.setRouteHost(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String extractSubdomain(final RequestContext context) {
        HttpServletRequest request = context.getRequest();
        String serverName = request.getServerName();
        if (serverName.contains(".")) {
            return serverName.split("\\.")[0];
        }
        return null;
    }
}
