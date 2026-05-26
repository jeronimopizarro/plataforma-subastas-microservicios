package com.portafolio.bidding;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:mysql://localhost:3307/bidding_db?serverTimezone=UTC&createDatabaseIfNotExist=true"
})
class ServicioBiddingApplicationTests {

	@Test
	void contextLoads() {
	}

}
