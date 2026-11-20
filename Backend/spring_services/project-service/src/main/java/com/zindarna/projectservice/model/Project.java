package com.zindarna.projectservice.model;

import com.zindarna.projectservice.enums.RoomType;
import com.zindarna.projectservice.enums.DecorationStyle;
import com.zindarna.projectservice.enums.ProjectStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    private String userId;

    private String name;

    private String description;

    private RoomType roomType;

    private Double length;

    private Double width;

    private Double height;

    private BigDecimal budget;

    private DecorationStyle decorationStyle;

    private ProjectStatus status;

    private List<RoomPhoto> roomPhotos = new ArrayList<>();

    private List<SelectedProduct> selectedProducts = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
