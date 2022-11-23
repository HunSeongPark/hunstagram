package com.example.hunstagram;

import com.example.hunstagram.config.AwsS3MockConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(AwsS3MockConfig.class)
@ActiveProfiles("test")
@SpringBootTest
class HunstagramApplicationTests {

    @Test
    void contextLoads() {
    }

}
