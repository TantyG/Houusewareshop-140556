package com.example.demo.repository;

import com.example.demo.entity.StatusSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusSubCategoryRepository extends JpaRepository<StatusSubCategory, Integer> {
}
