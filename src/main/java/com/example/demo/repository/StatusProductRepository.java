package com.example.demo.repository;

import com.example.demo.entity.StatusProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusProductRepository extends JpaRepository<StatusProduct, Integer> {
}
