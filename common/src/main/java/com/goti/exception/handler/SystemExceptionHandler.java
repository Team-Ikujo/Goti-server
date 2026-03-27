package com.goti.exception.handler;

import java.util.Map;
import java.util.stream.Collectors;

import com.goti.global.api.ApiErrorResponse;
import com.goti.constants.messages.ErrorCode;
import com.goti.exception.CustomException;
import com.goti.exception.FieldValidationException;
import com.goti.exception.handler.base.BaseExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;

import io.jsonwebtoken.JwtException;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class SystemExceptionHandler extends BaseExceptionHandler {

	/**
	 * 비즈니스/도메인 로직에서 발생하는 모든 예외
	 */
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiErrorResponse> handleCustomException(CustomException ex) {
		ErrorCode error = ex.error();
		String contextLog = formatContext(ex.context());
		if (error.isSystemError()) {
			if (contextLog.isEmpty()) {
				log.error("[System Error] code={} message={}", error.name(), ex.getMessage(), ex);
			} else {
				log.error("{} reason={} message={}", contextLog, error.name(), ex.getMessage(), ex);
			}
		} else {
			if (contextLog.isEmpty()) {
				log.warn("[Business Error] code={} message={}", error.name(), ex.getMessage());
			} else {
				log.warn("{} reason={} message={}", contextLog, error.name(), ex.getMessage());
			}
		}
		return toResponse(ex);
	}

	/**
	 * 필드 검증 예외 처리
	 */
	@ExceptionHandler(FieldValidationException.class)
	public ResponseEntity<ApiErrorResponse> handleFieldValidation(FieldValidationException ex) {
		log.warn("[Field Validation] code={}, message={}",
			ex.error().name(),
			ex.error().getMessage()
		);
		return toResponse(ex);
	}

	/**
	 * JWT 만료 예외 처리
	 */
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<ApiErrorResponse> handleExpiredJwt(ExpiredJwtException ex) {
		log.warn("[Auth Error] Token Expired - message={}", ex.getMessage());
		return toResponse(ErrorCode.AUTH_ACCESS_EXPIRED);
	}

	/**
	 * 기타 모든 JWT 관련 예외 처리
	 */
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ApiErrorResponse> handleJwtException(JwtException ex) {
		log.warn(
			"[Auth Error] Invalid Token: {}, message={}",
			ex.getClass().getSimpleName(),
			ex.getMessage()
		);
		return toResponse(ErrorCode.AUTH_INVALID);
	}

	private String formatContext(Map<String, Object> context) {
		if (context == null || context.isEmpty()) {
			return "";
		}
		return context.entrySet().stream()
			.map(entry -> entry.getKey() + "=" + entry.getValue())
			.collect(Collectors.joining(" "));
	}

}
