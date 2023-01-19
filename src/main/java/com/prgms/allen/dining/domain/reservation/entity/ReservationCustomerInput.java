package com.prgms.allen.dining.domain.reservation.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

import org.springframework.util.Assert;

@Embeddable
public class ReservationCustomerInput {

	private static final int MIN_VISITOR_COUNT = 2;
	private static final int MAX_VISITOR_COUNT = 8;
	private static final long MAX_RESERVE_PERIOD = 30L;
	private static final int MINUTE_FORMAT = 0;
	private static final int SECOND_FORMAT = 0;
	private static final int MAX_MEMO_LENGTH = 300;

	@Column(name = "visit_date", nullable = false)
	private LocalDate visitDate;

	@Column(name = "visit_time", nullable = false)
	private LocalTime visitTime;

	@Column(name = "visitor_count", nullable = false)
	private int visitorCount;

	@Lob
	@Column(name = "customer_memo", length = 300)
	private String customerMemo;

	protected ReservationCustomerInput() {
	}

	public ReservationCustomerInput(LocalDateTime visitDateTime, int visitorCount, String customerMemo) {
		validate(visitDateTime, visitorCount, customerMemo);

		this.visitDate = visitDateTime.toLocalDate();
		this.visitTime = visitDateTime.toLocalTime()
			.truncatedTo(ChronoUnit.SECONDS);
		this.visitorCount = visitorCount;
		this.customerMemo = customerMemo;
	}

	public ReservationCustomerInput(LocalDate visitDate, LocalTime visitTime, int visitorCount, String customerMemo) {
		this(
			LocalDateTime.of(visitDate, visitTime),
			visitorCount,
			customerMemo
		);
	}

	private void validate(LocalDateTime visitDateTime, int visitorCount, String customerMemo) {
		validateVisitBoundary(visitDateTime);
		validateTimeFormat(visitDateTime.toLocalTime());
		validateVisitorCount(visitorCount);
		validateMemo(customerMemo);
	}

	private void validateVisitBoundary(LocalDateTime visitDateTime) {
		Assert.notNull(visitDateTime, "Field visitDateTime must not be null");

		LocalDateTime now = LocalDateTime.now();
		Assert.state(
			visitDateTime.isAfter(now.truncatedTo(ChronoUnit.HOURS)),
			"Field visitTime must be after the next hour based on the current date time."
		);

		LocalDate visitDate = visitDateTime.toLocalDate();
		LocalDate nowDate = now.toLocalDate();
		Assert.state(
			visitDate.isBefore(nowDate.plusDays(MAX_RESERVE_PERIOD)),
			String.format("Field visitDate must be within %d days.", MAX_RESERVE_PERIOD)
		);
	}

	private void validateTimeFormat(LocalTime visitTime) {
		Assert.notNull(visitTime, "Field visitTime must not be null");
		Assert.state(
			visitTime.getMinute() == MINUTE_FORMAT && visitTime.getSecond() == SECOND_FORMAT,
			String.format(
				"Field visitTime's minute must be %d and second must be %d.",
				MINUTE_FORMAT,
				SECOND_FORMAT
			)
		);
	}

	private void validateVisitorCount(int visitorCount) {
		Assert.state(
			visitorCount >= MIN_VISITOR_COUNT && visitorCount <= MAX_VISITOR_COUNT,
			String.format("Field visitorCount must be between %d and %d",
				MIN_VISITOR_COUNT,
				MAX_VISITOR_COUNT
			)
		);
	}

	private void validateMemo(String memo) {
		Assert.notNull(memo, "Memo must not be null");
		Assert.state(
			memo.length() <= MAX_MEMO_LENGTH,
			String.format("Memo length must be under %d.", MAX_MEMO_LENGTH)
		);
	}

	public LocalDate getVisitDate() {
		return visitDate;
	}

	public LocalTime getVisitTime() {
		return visitTime;
	}

	public int getVisitorCount() {
		return visitorCount;
	}

	public String getCustomerMemo() {
		return customerMemo;
	}

	public LocalDateTime getVisitDateTime() {
		return LocalDateTime.of(visitDate, visitTime);
	}

	public boolean checkVisitingToday() {
		return visitDate.equals(LocalDate.now());
	}

	@Override
	public String toString() {
		return "ReservationDetail{" +
			"visitDate=" + visitDate +
			", visitTime=" + visitTime +
			", visitorCount=" + visitorCount +
			", customerMemo='" + customerMemo + '\'' +
			'}';
	}
}