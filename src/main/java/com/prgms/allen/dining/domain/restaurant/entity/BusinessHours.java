package com.prgms.allen.dining.domain.restaurant.entity;

import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BusinessHours {

	@Column(name = "open_time", nullable = false)
	private LocalTime openTime;

	@Column(name = "last_order_time", nullable = false)
	private LocalTime lastOrderTime;

	public BusinessHours(LocalTime openTime, LocalTime lastOrderTime) {
		this.openTime = openTime;
		this.lastOrderTime = lastOrderTime;
	}

	protected BusinessHours() {
	}

	public LocalTime getOpenTime() {
		return openTime;
	}

	public LocalTime getLastOrderTime() {
		return lastOrderTime;
	}

}
