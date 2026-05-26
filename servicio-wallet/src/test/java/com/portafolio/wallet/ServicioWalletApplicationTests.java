package com.portafolio.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:mysql://localhost:3307/wallet_db?serverTimezone=UTC&createDatabaseIfNotExist=true"})
class ServicioWalletApplicationTests {

	@Test
	void contextLoads() {
	}

}
