package com.example.hunstagram.domain.follow.controller;

import com.example.hunstagram.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@RequestMapping("/v1/follow")
@RestController
public class FollowApiController {

    private final FollowService followService;
}
