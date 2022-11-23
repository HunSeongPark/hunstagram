package com.example.hunstagram.domain.post.controller;

import com.example.hunstagram.domain.post.dto.PostDto;
import com.example.hunstagram.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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
            @RequestPart(value = "data", required = false) PostDto.PostRequest requestDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        postService.createPost(requestDto, images);
        return ResponseEntity.ok().build();
    }
}
