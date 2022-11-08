package com.example.hunstagram.domain.comment.entity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
