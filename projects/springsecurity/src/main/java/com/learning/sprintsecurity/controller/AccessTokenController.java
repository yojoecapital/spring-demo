package com.learning.sprintsecurity.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.learning.sprintsecurity.model.AccessToken;
import com.learning.sprintsecurity.model.AccessTokenRequest;
import com.learning.sprintsecurity.repository.ClientIdsRepository;
import com.learning.sprintsecurity.service.OpenAmService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccessTokenController {
    private final OpenAmService openAmService;
    private final ClientIdsRepository clientIdsRepository;

    @PostMapping("/access-token")
    public ResponseEntity<AccessToken> getAccessToken(
        @RequestBody AccessTokenRequest accessTokenRequest
    ) {
        try {
            String business = clientIdsRepository.getBusinessByClientId(accessTokenRequest.getClientId());
            AccessToken accessToken = openAmService.getAccessToken(accessTokenRequest, business);
            return new ResponseEntity<>(accessToken, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
