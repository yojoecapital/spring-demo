package com.learning.sprintsecurity.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OpemAmOAuth2Client {

    private static final List<String> DEFAULT_SCOPES = Arrays.asList(
        "read:movies",
        "write:movies",
        "read:books",
        "write:books"
    );

    private static List<String> nameScopes(List<String> scopes, String business) {
        return DEFAULT_SCOPES.stream()
            .map(scope -> scope + ":" + business)
            .toList();
    }

    private static List<String> indexScopes(List<String> scopes) {
        return IntStream.range(0, scopes.size())
            .mapToObj(i -> "[" + i + "]=" + scopes.get(i))
            .toList();
    }

    public OpemAmOAuth2Client(String business, String clientSecret, int defaultMaxAge, List<String> defaultScopes) {
        if (defaultMaxAge <= 0) {
            throw new IllegalArgumentException("Default max age must be greater than 0.");
        }
        this.defaultMaxAge = defaultMaxAge;
        this.userPassword = clientSecret;
        this.defaultScopes = indexScopes(defaultScopes);

        List<String> allowedScopes = nameScopes(DEFAULT_SCOPES, business);
        scopes = indexScopes(allowedScopes);
        for (String defaultScope : defaultScopes) {
            if (!allowedScopes.contains(defaultScope)) {
                throw new IllegalArgumentException("Default scope " + defaultScope + " is not in the scopes list.");
            }
        }
    }

    @JsonProperty("userpassword")
    private String userPassword;

    @JsonProperty("com.forgerock.openam.oauth2provider.clientType")
    private String clientType = "Confidential";

    @JsonProperty("com.forgerock.openam.oauth2provider.scopes")
    private List<String> scopes;

    @JsonProperty("com.forgerock.openam.oauth2provider.responseTypes")    
    private List<String> responseTypes = Arrays.asList("[0]=id_token");
    @JsonProperty("com.forgerock.openam.oauth2provider.subjectType")
    private String subjectType = "Public";

    @JsonProperty("com.forgerock.openam.oauth2provider.defaultScopes")
    private List<String> defaultScopes;

    @JsonProperty("com.forgerock.openam.oauth2provider.tokenEndPointAuthMethod")
    private String tokenEndPointAuthMethod = "client_secret_basic";

    @JsonProperty("com.forgerock.openam.oauth2provider.defaultMaxAge")
    private int defaultMaxAge;
 
    private String sunIdentityServerDeviceStatus = "Active";

    @JsonProperty("com.forgerock.openam.oauth2provider.publicKeyLocation")
    private String publicKeyLocation = "jwks_uri";

    @JsonProperty("com.forgerock.openam.oauth2provider.jwksURI")
    private String jwksURI = "http://localhost:8080/openam/oauth2/connect/jwk_uri";
}
