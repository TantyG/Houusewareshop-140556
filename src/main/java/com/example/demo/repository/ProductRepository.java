package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "select p from Product p inner join p.subCategory sub where sub.category.id=?1",
    countQuery = "select count (p) from Product p inner join p.subCategory sub where sub.category.id=?1")
    public Page<Product> findByCategoryId(int categoryId, Pageable pageable);

    public Page<Product> findBySubCategoryId(int subCategoryId, Pageable pageable);

    @Query(value = "SELECT p from Product p where p.name like ?1 or p.description like ?1",
    countQuery = "SELECT count(p) from Product p where p.name like ?1 or p.description like ?1")

    public Page<Product> search(String keyword, Pageable pageable);
}
