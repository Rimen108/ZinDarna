
package com.zindarna.orderservice.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class SupplierProfileClient {

    private final RestClient restClient;

    public SupplierProfileClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder
    ) {
        this.restClient = restClientBuilder.build();
    }

    public String getApprovedSupplierProfileId(Jwt jwt) {

        // Forward the authenticated user's JWT
        SupplierProfileResponse profile = restClient.get()
                .uri("http://auth-profile-service/api/profiles/me")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + jwt.getTokenValue()
                )
                .retrieve()
                .body(SupplierProfileResponse.class);

        // Verify that a profile was returned
        if (profile == null || profile.getId() == null) {
            throw new AccessDeniedException(
                    "Supplier profile not found"
            );
        }

        // Verify account type
        if (!"SUPPLIER".equals(profile.getAccountType())) {
            throw new AccessDeniedException(
                    "This account is not a supplier"
            );
        }

        // Verify supplier approval
        if (!"APPROVED".equals(profile.getSupplierStatus())) {
            throw new AccessDeniedException(
                    "Supplier account is not approved"
            );
        }

        return profile.getId();
    }

    // Response received from Auth/Profile Service
    public static class SupplierProfileResponse {

        private String id;
        private String accountType;
        private String supplierStatus;

        public SupplierProfileResponse() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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
