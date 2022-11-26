package com.example.hunstagram.domain.post.entity;

import com.example.hunstagram.domain.BaseTimeEntity;
import com.example.hunstagram.domain.hashtag.entity.Hashtag;
import com.example.hunstagram.domain.post.dto.PostDto;
import com.example.hunstagram.domain.postimage.entity.PostImage;
import com.example.hunstagram.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.List;

import static javax.persistence.CascadeType.ALL;
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
public class Post extends BaseTimeEntity {

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

    @OneToMany(fetch = LAZY, mappedBy = "post", cascade = ALL)
    private List<Hashtag> hashtags;

    @OneToMany(fetch = LAZY, mappedBy = "post", cascade = ALL)
    private List<PostImage> postImages;

    public void update(PostDto.Request requestDto) {
        this.content = requestDto.getContent();
        this.hashtags = requestDto.getHashtags()
                .stream()
                .map(h-> new Hashtag(h, this))
                .toList();
    }
}
