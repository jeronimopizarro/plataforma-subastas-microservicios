package com.portafolio.subastas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:mysql://localhost:3307/subastas_db?serverTimezone=UTC&createDatabaseIfNotExist=true"})
class ServicioSubastasApplicationTests {

	@Test
	void contextLoads() {
	}

}
