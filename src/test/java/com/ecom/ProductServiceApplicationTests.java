package com.ecom;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class ProductServiceApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	public void testMdcLogging() {
		MDC.put("traceId", "test-trace-123");

		try {
			log.info("Testing MDC output");
		} finally {
			MDC.remove("traceId");
		}
	}
}
