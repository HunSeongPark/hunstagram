package com.example.hunstagram.domain.postimage.entity;

import com.example.hunstagram.domain.post.entity.Post;
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
public class PostImage {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_image_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private String imageUrl;
}
