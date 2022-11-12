package com.example.hunstagram.domain.post.entity;

import com.example.hunstagram.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "thumbnail_image", nullable = false)
    private String thumbnailImage;

    @Column(name = "content")
    private String content;
}
