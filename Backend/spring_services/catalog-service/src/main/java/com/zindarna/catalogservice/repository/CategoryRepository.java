package com.zindarna.catalogservice.repository;

import com.zindarna.catalogservice.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository
        extends MongoRepository<Category, String> {
}