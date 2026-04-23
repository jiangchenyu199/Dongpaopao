package com.dongpaopao.backend.api;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

	@GetMapping("/health")
	public Map<String, Object> health() {
		return Map.of(
			"service", "dongpaopao-backend",
			"status", "UP",
			"timestamp", Instant.now().toString()
		);
	}
}
