package com.example.hunstagram.domain.like.entity;

import com.example.hunstagram.domain.BaseTimeEntity;
import com.example.hunstagram.domain.comment.entity.Comment;
import com.example.hunstagram.domain.post.entity.Post;
import com.example.hunstagram.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "likes")
@Entity
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;
}
