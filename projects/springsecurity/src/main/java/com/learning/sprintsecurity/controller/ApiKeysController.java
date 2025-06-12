package com.learning.sprintsecurity.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.learning.sprintsecurity.model.ClientKeys;
import com.learning.sprintsecurity.model.CreateClientKeysRequest;
import com.learning.sprintsecurity.model.DeleteClientKeysRequest;
import com.learning.sprintsecurity.repository.ClientIdsRepository;
import com.learning.sprintsecurity.service.OpenAmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@RestController
@RequiredArgsConstructor
public class ApiKeysController {
   
    private final OpenAmService openAmService;
    private final ClientIdsRepository clientIdsRepository;


    @PostMapping("/api-keys/{business}")
    public ResponseEntity<ClientKeys> createClientKey(
        @PathVariable String business, 
        @RequestBody CreateClientKeysRequest createClientKeysRequest
    ) {
        try {
            ClientKeys clientKeys = openAmService.createOAuth2Client(business, createClientKeysRequest);
            clientIdsRepository.addClientId(business, clientKeys.getClientId());
            return new ResponseEntity<>(clientKeys, HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/api-keys/{business}")
    public ResponseEntity<Void> deleteClientKey(
        @PathVariable String business,
        @RequestBody DeleteClientKeysRequest deleteClientKeysRequest
    ) {
        try {
            List<String> clientIds = clientIdsRepository.getClientIds(business);
            if (clientIds == null || !clientIds.contains(deleteClientKeysRequest.getClientId())) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            openAmService.deleteOAuth2Client(deleteClientKeysRequest.getClientId());
            clientIds.remove(deleteClientKeysRequest.getClientId());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/api-keys/{business}")
    public ResponseEntity<List<String>> getClientKeys(
        @PathVariable String business
    ) {
        List<String> clientIds = clientIdsRepository.getClientIds(business);
        if (clientIds == null || clientIds.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(clientIds, HttpStatus.OK);
    }
}
