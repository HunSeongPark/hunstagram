package com.example.hunstagram.domain.postimage.entity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
