package com.example.hunstagram.domain.post.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p " +
            "join fetch p.hashtags h " +
            "join fetch p.user u " +
            "where p.id = :postId")
    Optional<Post> findByIdWithHashtagAndUser(Long postId);
}
