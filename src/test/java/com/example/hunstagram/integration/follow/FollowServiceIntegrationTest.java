package com.example.hunstagram.integration.follow;

import com.example.hunstagram.domain.follow.entity.FollowRepository;
import com.example.hunstagram.domain.follow.service.FollowService;
import com.example.hunstagram.domain.user.entity.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Hunseong-Park
 * @date : 2022-11-21
 */
@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class FollowServiceIntegrationTest {

    @Autowired
    FollowService followService;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    UserRepository userRepository;

    @DisplayName("follow 추가에 성공한다")
    void follow_add_success() {

        // given


        // when


        // then
    }
}
