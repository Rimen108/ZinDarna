package com.zindarna.projectservice.repository;

import com.zindarna.projectservice.model.Project;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {

    List<Project> findByUserId(String userId);

}
