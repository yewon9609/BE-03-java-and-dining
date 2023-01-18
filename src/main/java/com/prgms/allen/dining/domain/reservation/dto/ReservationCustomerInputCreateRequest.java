package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prgms.allen.dining.domain.reservation.entity.ReservationCustomerInput;

public record ReservationCustomerInputCreateRequest(

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime visitAt,

	int visitorCount,

	@NotBlank
	String memo
) {

	public ReservationCustomerInput toEntity() {
		return new ReservationCustomerInput(
			visitAt,
			visitorCount,
			memo
		);
	}
}
