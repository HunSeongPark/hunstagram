package com.example.hunstagram.domain.comment.controller;

import com.example.hunstagram.domain.comment.service.CommentService;
import com.example.hunstagram.domain.like.dto.LikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@RequestMapping("/v1/comments")
@RestController
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<LikeDto.Response> commentLike(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.like(commentId));
    }
}
