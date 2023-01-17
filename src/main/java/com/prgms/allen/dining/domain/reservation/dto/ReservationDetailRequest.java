package com.prgms.allen.dining.domain.reservation.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.prgms.allen.dining.domain.reservation.entity.ReservationDetail;

public record ReservationDetailRequest(

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime visitAt,

	@Min(value = 2)
	@Max(value = 8)
	int visitorCount,

	@NotBlank
	String memo
) {

	public ReservationDetail toEntity() {
		return new ReservationDetail(
			visitAt,
			visitorCount,
			memo
		);
	}
}
