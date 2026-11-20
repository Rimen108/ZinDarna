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
import java.io.IOException;
import java.util.List;
import com.zindarna.projectservice.model.SelectedProduct;

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

    @PostMapping
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }
    @GetMapping("/user/{userId}")
    public List<Project> getProjectsByUser(@PathVariable String userId) {
        return projectService.getProjectsByUser(userId);
    }
    @GetMapping("/{projectId}")
    public Project getProjectById(@PathVariable String projectId) {
        return projectService.getProjectById(projectId);
    }
    @PutMapping("/{projectId}")
    public Project updateProject(
            @PathVariable String projectId,
            @RequestBody Project updatedProject) {

        return projectService.updateProject(projectId, updatedProject);
    }
    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable String projectId) {
        projectService.deleteProject(projectId);
    }
    @PostMapping(
            value = "/{projectId}/photos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Project addPhotoToProject(
            @PathVariable String projectId,
            @RequestParam("file") MultipartFile file) throws IOException {

        return projectService.addPhotoToProject(projectId, file);
    }
    @DeleteMapping("/{projectId}/photos/{photoId}")
    public Project deletePhotoFromProject(
            @PathVariable String projectId,
            @PathVariable String photoId) {

        return projectService.deletePhotoFromProject(projectId, photoId);
    }
    @GetMapping("/{projectId}/photos")
    public List<RoomPhoto> getProjectPhotos(
            @PathVariable String projectId) {

        return projectService.getProjectPhotos(projectId);
    }
    @PostMapping("/{projectId}/products")
    public Project addProductToProject(
            @PathVariable String projectId,
            @RequestParam String productId) {

        return projectService.addProductToProject(projectId, productId);
    }
    @PutMapping("/{projectId}/products/{productId}/status")
    public Project updateProductStatus(
            @PathVariable String projectId,
            @PathVariable String productId,
            @RequestParam ProductSelectionStatus status) {

        return projectService.updateProductStatus(
                projectId,
                productId,
                status
        );
    }
    @DeleteMapping("/{projectId}/products/{productId}")
    public Project removeProductFromProject(
            @PathVariable String projectId,
            @PathVariable String productId) {

        return projectService.removeProductFromProject(projectId, productId);
    }
    @GetMapping("/{projectId}/photos/{photoId}/image")
    public ResponseEntity<byte[]> getPhoto(
            @PathVariable String projectId,
            @PathVariable String photoId) throws IOException {

        Project project = projectService.getProjectById(projectId);

        RoomPhoto photo = project.getRoomPhotos().stream()
                .filter(p -> p.getPhotoId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Photo introuvable."));

        byte[] image = photoService.getPhoto(photoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + photo.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(photo.getContentType()))
                .body(image);
    }
}
