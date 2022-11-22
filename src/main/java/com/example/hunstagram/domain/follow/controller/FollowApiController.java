package com.example.hunstagram.domain.follow.controller;

import com.example.hunstagram.domain.follow.dto.FollowDto;
import com.example.hunstagram.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-08
 */
@RequiredArgsConstructor
@RequestMapping("/v1/follow")
@RestController
public class FollowApiController {

    private final FollowService followService;

    @PostMapping("/{toUserId}")
    public ResponseEntity<FollowDto.FollowResponse> follow(@PathVariable Long toUserId) {
        return ResponseEntity.ok(followService.follow(toUserId));
    }

    @GetMapping("/followee/{userId}")
    public ResponseEntity<Page<FollowDto.FollowListResponse>> getFolloweeList(
            Pageable pageable, @PathVariable Long userId
    ) {
        return ResponseEntity.ok(followService.getFolloweeList(pageable, userId));
    }
}
