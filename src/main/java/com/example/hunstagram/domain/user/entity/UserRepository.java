package com.example.hunstagram.domain.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
}
