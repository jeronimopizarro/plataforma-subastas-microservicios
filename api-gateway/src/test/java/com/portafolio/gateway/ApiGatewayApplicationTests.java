package com.portafolio.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Le inyectamos una variable de entorno "de mentira" solo para que el test pueda compilar
@SpringBootTest(properties = {
		"JWT_SECRET=clave_falsa_muy_larga_para_que_el_test_pase_sin_problemas_123456789"
})
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}