package com.example.hunstagram.domain.hashtag.entity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
}
