
package com.zindarna.projectservice.service;

import com.zindarna.projectservice.enums.ProductSelectionStatus;
import com.zindarna.projectservice.enums.ProjectStatus;
import com.zindarna.projectservice.model.RoomPhoto;
import com.zindarna.projectservice.model.SelectedProduct;
import com.zindarna.projectservice.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import com.zindarna.projectservice.model.Project;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

import com.zindarna.projectservice.exception.ResourceNotFoundException;
import com.zindarna.projectservice.exception.BadRequestException;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final PhotoService photoService;

    public ProjectService(ProjectRepository projectRepository,
                          PhotoService photoService) {
        this.projectRepository = projectRepository;
        this.photoService = photoService;
    }

    // Créer un projet
    public Project createProject(Project project) {

        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());

        if (project.getStatus() == null) {
            project.setStatus(ProjectStatus.DRAFT);
        }

        return projectRepository.save(project);
    }

    // Récupérer les projets d'un utilisateur
    public List<Project> getProjectsByUser(String userId) {
        return projectRepository.findByUserId(userId);
    }

    // Récupérer un projet en vérifiant son propriétaire
    public Project getProjectById(String projectId, String userId) {

        Project project = getProjectById(projectId);

        if (!userId.equals(project.getUserId())) {
            throw new ResourceNotFoundException("Projet introuvable.");
        }

        return project;
    }

    // Récupérer un projet par son ID
    public Project getProjectById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Projet introuvable avec l'identifiant : " + projectId
                ));
    }

    // Modifier un projet
    public Project updateProject(String projectId, String userId,
                                 Project updatedProject) {

        Project existingProject = getProjectById(projectId, userId);

        existingProject.setName(updatedProject.getName());
        existingProject.setDescription(updatedProject.getDescription());
        existingProject.setRoomType(updatedProject.getRoomType());
        existingProject.setLength(updatedProject.getLength());
        existingProject.setWidth(updatedProject.getWidth());
        existingProject.setHeight(updatedProject.getHeight());
        existingProject.setBudget(updatedProject.getBudget());
        existingProject.setDecorationStyle(updatedProject.getDecorationStyle());
        existingProject.setStatus(updatedProject.getStatus());

        existingProject.setUpdatedAt(LocalDateTime.now());

        return projectRepository.save(existingProject);
    }

    // Supprimer un projet
    public void deleteProject(String projectId, String userId) {

        Project project = getProjectById(projectId, userId);

        if (project.getRoomPhotos() != null) {
            for (RoomPhoto photo : project.getRoomPhotos()) {
                photoService.deletePhoto(photo.getPhotoId());
            }
        }

        projectRepository.delete(project);
    }

    // Ajouter une photo
    public Project addPhotoToProject(String projectId, String userId,
                                     MultipartFile file) throws IOException {

        Project project = getProjectById(projectId, userId);

        String photoId = photoService.uploadPhoto(file);

        RoomPhoto roomPhoto = new RoomPhoto(
                photoId,
                file.getOriginalFilename(),
                file.getContentType(),
                LocalDateTime.now()
        );

        if (project.getRoomPhotos() == null) {
            project.setRoomPhotos(new ArrayList<>());
        }

        project.getRoomPhotos().add(roomPhoto);
        project.setUpdatedAt(LocalDateTime.now());

        return projectRepository.save(project);
    }

    // Supprimer une photo
    public Project deletePhotoFromProject(String projectId, String userId,
                                          String photoId) {

        Project project = getProjectById(projectId, userId);

        if (project.getRoomPhotos() == null) {
            throw new ResourceNotFoundException(
                    "Aucune photo trouvée dans ce projet."
            );
        }

        RoomPhoto photoToDelete = project.getRoomPhotos()
                .stream()
                .filter(photo -> photo.getPhotoId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Photo introuvable dans ce projet."
                ));

        photoService.deletePhoto(photoId);
        project.getRoomPhotos().remove(photoToDelete);
        project.setUpdatedAt(LocalDateTime.now());

        return projectRepository.save(project);
    }

    // Ajouter un produit
    public Project addProductToProject(String projectId, String userId,
                                       String productId) {

        Project project = getProjectById(projectId, userId);

        if (project.getSelectedProducts() == null) {
            project.setSelectedProducts(new ArrayList<>());
        }

        boolean alreadyExists = project.getSelectedProducts()
                .stream()
                .anyMatch(product -> product.getProductId().equals(productId));

        if (alreadyExists) {
            throw new BadRequestException(
                    "Ce produit existe déjà dans le projet."
            );
        }

        SelectedProduct selectedProduct = new SelectedProduct(
                productId,
                1,
                ProductSelectionStatus.INSPIRATION,
                LocalDateTime.now()
        );

        project.getSelectedProducts().add(selectedProduct);
        project.setUpdatedAt(LocalDateTime.now());

        return projectRepository.save(project);
    }

    // Modifier le statut d'un produit
    public Project updateProductStatus(String projectId, String userId,
                                       String productId,
                                       ProductSelectionStatus newStatus) {

        Project project = getProjectById(projectId, userId);

        if (project.getSelectedProducts() == null) {
            throw new ResourceNotFoundException(
                    "Aucun produit dans ce projet."
            );
        }

        SelectedProduct selectedProduct = project.getSelectedProducts()
                .stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit introuvable dans ce projet."
                ));

        selectedProduct.setStatus(newStatus);
        project.setUpdatedAt(LocalDateTime.now());

        return projectRepository.save(project);
    }

    // Supprimer un produit
    public Project removeProductFromProject(String projectId, String userId,
                                            String productId) {

        Project project = getProjectById(projectId, userId);

        if (project.getSelectedProducts() == null) {
            throw new ResourceNotFoundException(
                    "Aucun produit dans ce projet."
            );
        }

        boolean removed = project.getSelectedProducts()
                .removeIf(product -> product.getProductId().equals(productId));

        if (!removed) {
            throw new ResourceNotFoundException(
                    "Produit introuvable dans ce projet."
            );
        }

        project.setUpdatedAt(LocalDateTime.now());

        return projectRepository.save(project);
    }

    // Récupérer les photos d'un projet
    public List<RoomPhoto> getProjectPhotos(String projectId, String userId) {

        Project project = getProjectById(projectId, userId);

        if (project.getRoomPhotos() == null) {
            return new ArrayList<>();
        }

        return project.getRoomPhotos();
    }
}
