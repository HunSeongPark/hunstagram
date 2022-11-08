package com.example.hunstagram.domain.post.entity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
public interface PostRepository extends JpaRepository<Post, Long> {
}
