package com.example.hunstagram.domain.post.controller;

import com.example.hunstagram.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@RequestMapping("/v1/posts")
@RestController
public class PostApiController {

    private final PostService postService;
}
