package com.goti.payment.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goti.constants.PaymentMethod;
import com.goti.constants.PaymentStatus;
import com.goti.constants.PaymentType;
import com.goti.exception.handler.SpringExceptionHandler;
import com.goti.exception.handler.SystemExceptionHandler;
import com.goti.payment.dto.request.PaymentRequest;
import com.goti.payment.dto.response.PaymentResponse;
import com.goti.payment.service.domain.PaymentService;

@SpringBootTest(classes = PaymentControllerTest.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("mock 결제 요청 - POST /api/v1/orders/{orderId}/payments")
class PaymentControllerTest {

	@SpringBootConfiguration
	@EnableAutoConfiguration(exclude = {
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class
	})
	@Import({
		PaymentController.class,
		SystemExceptionHandler.class,
		SpringExceptionHandler.class
	})
	static class TestApplication {
	}

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private PaymentService paymentService;

	@Test
	@WithMockUser
	void mock_결제_요청_성공__200_OK() throws Exception {
		UUID orderId = UUID.randomUUID();
		UUID paymentId = UUID.randomUUID();

		PaymentRequest request = new PaymentRequest(
			PaymentMethod.CARD,
			"payment-idempotency-key"
		);

		PaymentResponse response = PaymentResponse.from(
			paymentId,
			orderId,
			PaymentType.PAYMENT,
			PaymentMethod.CARD,
			24000,
			"MOCK",
			"mock-pg-tid",
			PaymentStatus.SUCCESS,
			LocalDateTime.of(2026, 3, 10, 12, 0),
			null
		);

		given(paymentService.create(
			orderId,
			request.paymentMethod(),
			request.idempotencyKey()
		)).willReturn(response);

		mockMvc.perform(
			post("/api/v1/orders/{orderId}/payments", orderId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		)
			.andDo(print())
			.andExpectAll(
				status().isOk(),
				jsonPath("$.code").value("ok"),
				jsonPath("$.message").value("성공"),
				jsonPath("$.data").exists(),
				jsonPath("$.data.paymentId").value(paymentId.toString()),
				jsonPath("$.data.orderId").value(orderId.toString()),
				jsonPath("$.data.paymentStatus").value("SUCCESS")
			);
	}

	@Test
	@WithMockUser
	void mock_결제_요청_실패_필수값누락__400_BAD_REQUEST() throws Exception {
		UUID orderId = UUID.randomUUID();

		String invalidBody = """
			{
			  "idempotencyKey": "payment-idempotency-key"
			}
			""";

		mockMvc.perform(
			post("/api/v1/orders/{orderId}/payments", orderId)
				.with(csrf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidBody)
		)
			.andDo(print())
			.andExpect(status().isBadRequest());
	}
}
