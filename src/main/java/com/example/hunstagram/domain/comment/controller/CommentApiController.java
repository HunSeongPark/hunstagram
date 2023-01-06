package com.example.hunstagram.domain.comment.controller;

import com.example.hunstagram.domain.comment.dto.CommentDto;
import com.example.hunstagram.domain.comment.service.CommentService;
import com.example.hunstagram.domain.like.dto.LikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@RequestMapping("/v1/comments")
@RestController
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentDto.Request requestDto) {
        commentService.createComment(requestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<LikeDto.Response> likeComment(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.like(commentId));
    }
}
