package com.example.hunstagram.domain.follow.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("select f from Follow f where f.toUser.id = :userId")
    Page<Follow> findFolloweeList(Pageable pageable, Long userId);

    @Query("select f from Follow f where f.fromUser.id = :userId")
    Page<Follow> findFollowingList(Pageable pageable, Long userId);

    @Query("select count(f) from Follow f where f.toUser.id = :userId")
    Integer countFolloweeByUserId(Long userId);

    @Query("select count(f) from Follow f where f.fromUser.id = :userId")
    Integer countFollowingByUserId(Long userId);

    @Query("select count(f) from Follow f " +
            "where f.fromUser.id = :fromUserId " +
            "and f.toUser.id = :toUserId")
    Integer isFollow(Long fromUserId, Long toUserId);
}
