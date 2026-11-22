
package com.zindarna.authprofileservice.service;

import com.zindarna.authprofileservice.model.Profile;
import com.zindarna.authprofileservice.repository.ProfileRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    // Créer un profil selon le type de compte
    public Profile createProfile(Profile profile) {

        // Vérifier le type de compte
        if (profile.getAccountType() == null ||
                (!profile.getAccountType().equals("CLIENT") &&
                        !profile.getAccountType().equals("SUPPLIER"))) {

            throw new IllegalArgumentException("Type de compte invalide");
        }

        // Vérifier si un profil existe déjà
        Optional<Profile> existingProfile =
                profileRepository.findByKeycloakUserId(
                        profile.getKeycloakUserId()
                );

        if (existingProfile.isPresent()) {
            throw new IllegalStateException(
                    "Un profil existe déjà pour cet utilisateur"
            );
        }

        // Appliquer les règles selon le type de compte
        if ("SUPPLIER".equals(profile.getAccountType())) {

            profile.setSupplierStatus("PENDING");
            profile.setRejectionReason(null);

        } else {

            profile.setSupplierStatus(null);
            profile.setCompanyName(null);
            profile.setBusinessId(null);
            profile.setBusinessAddress(null);
            profile.setRejectionReason(null);
        }

        return profileRepository.save(profile);
    }

    // Rechercher un profil par identifiant Keycloak
    public Optional<Profile> getProfileByKeycloakUserId(
            String keycloakUserId) {

        return profileRepository.findByKeycloakUserId(keycloakUserId);
    }

    // Consulter tous les profils
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    // Consulter les demandes fournisseurs en attente
    public List<Profile> getPendingSupplierRequests() {

        return profileRepository.findByAccountTypeAndSupplierStatus(
                "SUPPLIER",
                "PENDING"
        );
    }

    // Accepter une demande fournisseur
    public Profile approveSupplierRequest(String id) {

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        if (!"SUPPLIER".equals(profile.getAccountType()) ||
                !"PENDING".equals(profile.getSupplierStatus())) {

            throw new IllegalStateException(
                    "Cette demande n'est pas en attente"
            );
        }

        // Modifier le statut dans MongoDB
        profile.setSupplierStatus("APPROVED");
        profile.setRejectionReason(null);

        return profileRepository.save(profile);
    }

    // Refuser une demande fournisseur
    public Profile rejectSupplierRequest(String id, String reason) {

        Profile profile = profileRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Profile not found"));

        if (!"SUPPLIER".equals(profile.getAccountType()) ||
                !"PENDING".equals(profile.getSupplierStatus())) {

            throw new IllegalStateException(
                    "Cette demande n'est pas en attente"
            );
        }

        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException(
                    "Le motif du refus est obligatoire"
            );
        }

        profile.setSupplierStatus("REJECTED");
        profile.setRejectionReason(reason);

        return profileRepository.save(profile);
    }

    // Modifier son profil
    public Profile updateProfile(String id, Profile updatedProfile) {

        return profileRepository.findById(id)
                .map(profile -> {

                    profile.setFirstName(updatedProfile.getFirstName());
                    profile.setLastName(updatedProfile.getLastName());
                    profile.setPhone(updatedProfile.getPhone());
                    profile.setPhoto(updatedProfile.getPhoto());
                    profile.setAddress(updatedProfile.getAddress());

                    return profileRepository.save(profile);
                })
                .orElseThrow(() ->
                        new RuntimeException("Profile not found")
                );
    }

    // Supprimer un profil
    public void deleteProfile(String id) {

        if (!profileRepository.existsById(id)) {
            throw new RuntimeException("Profile not found");
        }

        profileRepository.deleteById(id);
    }
}
