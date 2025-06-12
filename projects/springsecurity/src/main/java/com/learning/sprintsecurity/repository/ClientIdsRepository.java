package com.learning.sprintsecurity.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class ClientIdsRepository {
    private Map<String, List<String>> businessToClientIds = new HashMap<>();

    public List<String> getClientIds(String businessName) {
        return businessToClientIds.get(businessName);
    }

    public void addClientId(String businessName, String clientId) {
        businessToClientIds.computeIfAbsent(businessName, k -> new java.util.ArrayList<>()).add(clientId);
    }
}
