package com.goti.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Map;

public record StadiumCreateRequest(

	@NotBlank(message = "구장명은 필수 항목입니다.")
	String stadium_name,

	@NotBlank(message = "구장 지역명은 필수 항목입니다.")
	String location,

	@NotBlank(message = "시/도 값은 필수 항목입니다.")
	String city,

	@NotBlank(message = "시/군/구 값은 필수 항목입니다.")
	String district,

	@NotBlank(message = "도로명주소는 필수 항목입니다.")
	String roadAddress,

	@NotNull(message = "구장의 위도 값은 필수 항목입니다.")
	@DecimalMin(value = "-90.0", message = "위도 값은 -90에서 90 사이여야 합니다")
	@DecimalMax(value = "90.0", message = "위도 값은 -90에서 90 사이여야 합니다")
	BigDecimal latitude,

	@NotNull(message = "구장의 경도 값은 필수 항목입니다.")
	@DecimalMin(value = "-180.0", message = "경도 값은 -180에서 180 사이여야 합니다.")
	@DecimalMax(value = "180.0", message = "경도 값은 -180에서 180 사이여야 합니다.")
	BigDecimal longitude,

	@NotNull(message = "구장 좌석수는 필수 항목입니다.")
	@Positive(message = "구장 좌석수는 0보다 커야 합니다.")
	Integer totalSeats,

	@NotNull(message = "구장 좌석 배치 설정 정보는 필수 항목입니다.")
	Map<String, Object> seatMapConfig

) {
}
