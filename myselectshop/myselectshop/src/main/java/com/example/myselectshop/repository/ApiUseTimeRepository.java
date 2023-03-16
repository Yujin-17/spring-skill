package com.example.myselectshop.repository;

import com.example.myselectshop.entity.ApiUseTime;
import com.example.myselectshop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiUseTimeRepository extends JpaRepository<ApiUseTime, Long> {
    Optional<ApiUseTime> findByUser(User user);
}