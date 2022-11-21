package com.example.hunstagram.domain.follow.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query("select f from Follow f " +
            "where f.fromUser.id = :fromUserId " +
            "and f.toUser.id = :toUserId")
    Optional<Follow> findByFromAndToUserId(Long fromUserId, Long toUserId);
}
