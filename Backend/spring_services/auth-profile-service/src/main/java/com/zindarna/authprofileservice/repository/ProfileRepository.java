
package com.zindarna.authprofileservice.repository;

import com.zindarna.authprofileservice.model.Profile;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {

    Optional<Profile> findByKeycloakUserId(String keycloakUserId);

    // Rechercher les demandes fournisseurs selon leur statut
    List<Profile> findByAccountTypeAndSupplierStatus(
            String accountType,
            String supplierStatus
    );
}
