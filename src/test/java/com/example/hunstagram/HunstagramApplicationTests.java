package com.example.hunstagram;

import com.example.hunstagram.config.AwsS3MockConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(AwsS3MockConfig.class)
@SpringBootTest
class HunstagramApplicationTests {

	@Test
	void contextLoads() {
	}

}
