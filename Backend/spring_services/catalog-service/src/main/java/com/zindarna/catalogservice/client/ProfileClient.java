
package com.zindarna.catalogservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProfileClient {

    @Value("${auth-profile-service.base-url}")
    private String profileServiceUrl;

    private final RestTemplate restTemplate;

    public ProfileClient() {
        this.restTemplate = new RestTemplate();
    }

    public ProfileResponse getMyProfile(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        org.springframework.http.HttpEntity<Void> request =
                new org.springframework.http.HttpEntity<>(headers);

        ResponseEntity<ProfileResponse> response =
                restTemplate.exchange(
                        profileServiceUrl + "/api/profiles/me",
                        HttpMethod.GET,
                        request,
                        ProfileResponse.class
                );

        return response.getBody();
    }

    public static class ProfileResponse {

        private String id;
        private String keycloakUserId;
        private String accountType;
        private String supplierStatus;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getKeycloakUserId() {
            return keycloakUserId;
        }

        public void setKeycloakUserId(String keycloakUserId) {
            this.keycloakUserId = keycloakUserId;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public String getSupplierStatus() {
            return supplierStatus;
        }

        public void setSupplierStatus(String supplierStatus) {
            this.supplierStatus = supplierStatus;
        }
    }
}
