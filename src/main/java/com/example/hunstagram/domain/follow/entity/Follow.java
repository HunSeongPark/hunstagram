package com.example.hunstagram.domain.follow.entity;

import com.example.hunstagram.domain.BaseTimeEntity;
import com.example.hunstagram.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Follow extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "follow_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;
}
