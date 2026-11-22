
package com.zindarna.authprofileservice.controller;

import com.zindarna.authprofileservice.model.Profile;
import com.zindarna.authprofileservice.service.ProfileService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // Consulter son propre profil
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Profile> getMyProfile(
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakUserId = jwt.getSubject();

        return profileService.getProfileByKeycloakUserId(keycloakUserId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Créer son profil ou soumettre une demande fournisseur
    @PostMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Profile> createMyProfile(
            @RequestBody Profile profile,
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakUserId = jwt.getSubject();

        // L'identité vient toujours du token Keycloak
        profile.setId(null);
        profile.setKeycloakUserId(keycloakUserId);

        Profile createdProfile = profileService.createProfile(profile);

        return ResponseEntity.ok(createdProfile);
    }

    // Modifier son propre profil
    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('CLIENT', 'SUPPLIER', 'ADMIN')")
    public ResponseEntity<Profile> updateMyProfile(
            @RequestBody Profile updatedProfile,
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakUserId = jwt.getSubject();

        return profileService.getProfileByKeycloakUserId(keycloakUserId)
                .map(profile -> {
                    Profile updated = profileService.updateProfile(
                            profile.getId(), updatedProfile
                    );
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Consulter tous les profils : ADMIN uniquement
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Profile>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    // Consulter les demandes fournisseurs en attente : ADMIN uniquement
    @GetMapping("/admin/supplier-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Profile>> getPendingSupplierRequests() {
        return ResponseEntity.ok(
                profileService.getPendingSupplierRequests()
        );
    }

    // Accepter une demande fournisseur : ADMIN uniquement
    @PutMapping("/admin/supplier-requests/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Profile> approveSupplierRequest(
            @PathVariable String id) {

        return ResponseEntity.ok(
                profileService.approveSupplierRequest(id)
        );
    }

    // Refuser une demande fournisseur : ADMIN uniquement
    @PutMapping("/admin/supplier-requests/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Profile> rejectSupplierRequest(
            @PathVariable String id,
            @RequestBody RejectRequest request) {

        return ResponseEntity.ok(
                profileService.rejectSupplierRequest(
                        id, request.getReason()
                )
        );
    }

    // DTO pour le motif de refus
    public static class RejectRequest {

        private String reason;

        public RejectRequest() {
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    // Supprimer un profil : ADMIN uniquement
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProfile(@PathVariable String id) {

        profileService.deleteProfile(id);

        return ResponseEntity.noContent().build();
    }
}
