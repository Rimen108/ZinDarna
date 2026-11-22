
package com.zindarna.projectservice.controller;

import com.zindarna.projectservice.enums.ProductSelectionStatus;
import com.zindarna.projectservice.model.Project;
import com.zindarna.projectservice.model.RoomPhoto;
import com.zindarna.projectservice.service.PhotoService;
import com.zindarna.projectservice.service.ProjectService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final PhotoService photoService;
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService,
                             PhotoService photoService) {
        this.projectService = projectService;
        this.photoService = photoService;
    }

    // Récupérer l'identifiant de l'utilisateur connecté
    private String getUserId(Jwt jwt) {
        return jwt.getSubject();
    }

    // Créer un projet
    @PostMapping
    public Project createProject(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Project project) {

        String userId = getUserId(jwt);

        // Le propriétaire vient de Keycloak, jamais du Body
        project.setUserId(userId);

        return projectService.createProject(project);
    }

    // Récupérer les projets de l'utilisateur connecté
    @GetMapping("/user/me")
    public List<Project> getMyProjects(
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserId(jwt);

        return projectService.getProjectsByUser(userId);
    }

    // Récupérer un projet
    @GetMapping("/{projectId}")
    public Project getProjectById(
            @PathVariable String projectId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserId(jwt);

        return projectService.getProjectById(projectId, userId);
    }

    // Modifier un projet
    @PutMapping("/{projectId}")
    public Project updateProject(
            @PathVariable String projectId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Project updatedProject) {

        String userId = getUserId(jwt);

        return projectService.updateProject(
                projectId,
                userId,
                updatedProject
        );
    }

    // Supprimer un projet
    @DeleteMapping("/{projectId}")
    public void deleteProject(
            @PathVariable String projectId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserId(jwt);

        projectService.deleteProject(projectId, userId);
    }

    // Ajouter une photo
    @PostMapping(
            value = "/{projectId}/photos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Project addPhotoToProject(
            @PathVariable String projectId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("file") MultipartFile file) throws IOException {

        String userId = getUserId(jwt);

        return projectService.addPhotoToProject(
                projectId,
                userId,
                file
        );
    }

    // Supprimer une photo
    @DeleteMapping("/{projectId}/photos/{photoId}")
    public Project deletePhotoFromProject(
            @PathVariable String projectId,
            @PathVariable String photoId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserId(jwt);

        return projectService.deletePhotoFromProject(
                projectId,
                userId,
                photoId
        );
    }

    // Récupérer les photos d'un projet
    @GetMapping("/{projectId}/photos")
    public List<RoomPhoto> getProjectPhotos(
            @PathVariable String projectId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserId(jwt);

        return projectService.getProjectPhotos(projectId, userId);
    }

    // Ajouter un produit au projet
    @PostMapping("/{projectId}/products")
    public Project addProductToProject(
            @PathVariable String projectId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam String productId) {

        String userId = getUserId(jwt);

        return projectService.addProductToProject(
                projectId,
                userId,
                productId
        );
    }

    // Modifier le statut d'un produit
    @PutMapping("/{projectId}/products/{productId}/status")
    public Project updateProductStatus(
            @PathVariable String projectId,
            @PathVariable String productId,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam ProductSelectionStatus status) {

        String userId = getUserId(jwt);

        return projectService.updateProductStatus(
                projectId,
                userId,
                productId,
                status
        );
    }

    // Supprimer un produit du projet
    @DeleteMapping("/{projectId}/products/{productId}")
    public Project removeProductFromProject(
            @PathVariable String projectId,
            @PathVariable String productId,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = getUserId(jwt);

        return projectService.removeProductFromProject(
                projectId,
                userId,
                productId
        );
    }

    // Récupérer l'image d'une photo
    @GetMapping("/{projectId}/photos/{photoId}/image")
    public ResponseEntity<byte[]> getPhoto(
            @PathVariable String projectId,
            @PathVariable String photoId,
            @AuthenticationPrincipal Jwt jwt) throws IOException {

        String userId = getUserId(jwt);

        // Vérifier que le projet appartient à l'utilisateur
        Project project = projectService.getProjectById(projectId, userId);

        RoomPhoto photo = project.getRoomPhotos().stream()
                .filter(p -> p.getPhotoId().equals(photoId))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("Photo introuvable.")
                );

        byte[] image = photoService.getPhoto(photoId);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + photo.getFileName() + "\""
                )
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .body(image);
    }
}
