package com.example.hunstagram.domain.post.controller;

import com.example.hunstagram.domain.post.dto.PostDto;
import com.example.hunstagram.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@RequestMapping("/v1/posts")
@RestController
public class PostApiController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Void> createPost(
            @RequestPart(value = "data", required = false) PostDto.Request requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        postService.createPost(requestDto, images);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @RequestBody PostDto.Request requestDto,
            @PathVariable Long postId
    ) {
        postService.updatePost(requestDto, postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok().build();
    }
}
