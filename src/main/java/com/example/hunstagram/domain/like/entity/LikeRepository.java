package com.example.hunstagram.domain.like.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("select l from Like l " +
            "where l.post.id = :postId " +
            "and l.user.id = :userId")
    Optional<Like> findByPostAndUserId(Long postId, Long userId);
}
