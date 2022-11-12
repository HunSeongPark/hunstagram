package com.example.hunstagram.domain.follow.entity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {
}
