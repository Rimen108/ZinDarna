package com.zindarna.projectservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomPhoto {

    private String photoId;

    private String fileName;

    private String contentType;

    private LocalDateTime uploadedAt;

}
