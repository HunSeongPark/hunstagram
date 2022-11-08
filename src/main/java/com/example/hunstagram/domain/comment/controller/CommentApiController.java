package com.example.hunstagram.domain.comment.controller;

import com.example.hunstagram.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
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
}
