package com.example.demo.repository;

import com.example.demo.entity.StatusAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusAccountRepository extends JpaRepository<StatusAccount, Integer> {
}
