package com.zindarna.projectservice.model;

import com.zindarna.projectservice.enums.ProductSelectionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectedProduct {

    private String productId;

    private Integer quantity;

    private ProductSelectionStatus status;

    private LocalDateTime addedAt;

}
