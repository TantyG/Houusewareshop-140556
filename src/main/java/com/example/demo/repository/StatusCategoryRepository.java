package com.example.demo.repository;

import com.example.demo.entity.StatusCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusCategoryRepository extends JpaRepository<StatusCategory, Integer> {
}
