package com.example.hunstagram.domain.user.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);
}
