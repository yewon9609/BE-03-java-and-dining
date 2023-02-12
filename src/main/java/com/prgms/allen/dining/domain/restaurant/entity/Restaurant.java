package com.prgms.allen.dining.domain.restaurant.entity;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.springframework.util.Assert;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.domain.reservation.policy.ReservationPolicy;

@Entity
public class Restaurant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "restaurant_id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "owner_id")
	private Member owner;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "food_type", nullable = false)
	private FoodType foodType;

	@Column(name = "name", length = 30, nullable = false)
	private String name;

	@Column(name = "capacity", nullable = false)
	private int capacity;

	@Embedded
	private BusinessHours businessHours;

	@Column(name = "location", nullable = false)
	private String location;

	@Lob
	@Column(name = "description")
	private String description;

	@Column(name = "phone", length = 11, nullable = false)
	private String phone;

	@ElementCollection
	@CollectionTable(name = "Menu", joinColumns = @JoinColumn(name = "restaurant_id"))
	private List<Menu> menu = new ArrayList<>();

	@ElementCollection
	@CollectionTable(name = "ClosingDay", joinColumns = @JoinColumn(name = "restaurant_id"))
	private List<ClosingDay> closingDays = new ArrayList<>();

	protected Restaurant() {
	}

	public Restaurant(Member owner, FoodType foodType, String name, int capacity, BusinessHours businessHours,
		String location, String description, String phone) {
		this(null, owner, foodType, name, capacity, businessHours, location, description, phone,
			Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}

	public Restaurant(Member owner, FoodType foodType, String name, int capacity, BusinessHours businessHours,
		String location, String description, String phone, List<Menu> menuList,
		List<ClosingDay> closingDays) {
		this(null, owner, foodType, name, capacity, businessHours, location, description, phone,
			menuList, closingDays);
	}

	public Restaurant(Long id, Member owner, FoodType foodType, String name, int capacity, BusinessHours businessHours,
		String location, String description, String phone, List<Menu> menu, List<ClosingDay> closingDays) {
		validate(owner, name, capacity, phone, businessHours, location);
		this.id = id;
		this.owner = owner;
		this.foodType = foodType;
		this.name = name;
		this.capacity = capacity;
		this.businessHours = businessHours;
		this.location = location;
		this.description = description;
		this.phone = phone;
		this.menu = menu;
		this.closingDays = closingDays;
	}

	public Long getId() {
		return id;
	}

	public Member getOwner() {
		return owner;
	}

	public FoodType getFoodType() {
		return foodType;
	}

	public String getName() {
		return name;
	}

	public int getCapacity() {
		return capacity;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

	public String getPhone() {
		return phone;
	}

	public List<Menu> getMenu() {
		return List.copyOf(menu);
	}

	public List<ClosingDay> getClosingDays() {
		return List.copyOf(closingDays);
	}

	public BusinessHours getBusinessHours() {
		return businessHours;
	}

	public List<Menu> getMinorMenu() {
		if (menu.size() < 5) {
			return this.getMenu();
		}
		return this.menu.subList(0, 4);
	}

	public boolean isAvailable(int totalCount, int requestCount) {
		return this.capacity - totalCount >= requestCount;
	}

	public boolean isNotReserveAvailableForDay(long totalCount) {
		long availableTotalCapacity =
			(long)(businessHours.getLastOrderTime().getHour() - businessHours.getOpenTime().getHour() + 1) * capacity;
		return availableTotalCapacity - totalCount < 2;
	}

	public boolean isClosingDay(LocalDate requestDate) {
		return this.closingDays.stream()
			.map(ClosingDay::getDayOfWeek)
			.anyMatch(dayOfWeek -> dayOfWeek.equals(requestDate.getDayOfWeek()));
	}

	public void validate(Member owner, String name, int capacity, String phone, BusinessHours businessHours,
		String location) {
		validateOwnerType(owner);
		validateName(name);
		validateCapacity(capacity);
		validatePhone(phone);
		validateTimes(businessHours);
		validateLocation(location);
	}

	private void validateOwnerType(Member owner) {
		Assert.notNull(owner, "Owner must not be empty");
		Assert.isTrue(MemberType.OWNER.equals(owner.getMemberType()),
			MessageFormat.format("member id: {0} is not owner, actually type is customer", owner.getId()));
	}

	private void validateName(String name) {
		Assert.isTrue(name.length() >= 1, "Length of name must over than 0");
		Assert.isTrue(name.length() <= 30, "Length of name must less than 31");
	}

	private void validateCapacity(int capacity) {
		Assert.isTrue(capacity >= 2, "Capacity must over than 1");
	}

	private void validatePhone(String phone) {
		Assert.hasLength(phone, "Phone must be not empty.");
		Assert.isTrue(phone.length() >= 9 && phone.length() <= 11, "Phone must between 9 and 11");
		Assert.isTrue(Pattern.matches("^[0-9]+$", phone), "Phone is invalid format");
	}

	private void validateTimes(BusinessHours businessHours) {
		Assert.notNull(businessHours.getOpenTime(), "openTime must not be empty");
		Assert.notNull(businessHours.getLastOrderTime(), "lastOrderTime must not by empty");
	}

	private void validateLocation(String location) {
		Assert.hasLength(location, "Location must be not empty.");
	}

	public boolean isAvailableVisitorCount(int totalCount, int requestCount) {
		return this.capacity - totalCount >= requestCount;
	}

	public boolean isAvailableVisitDateTime(LocalDateTime visitDateTime) {
		LocalTime visitTime = visitDateTime.toLocalTime();
		return isAfterOrEqualOpenTime(visitTime) && isBeforeOrEqualLastOrderTime(visitTime);
	}

	private boolean isAfterOrEqualOpenTime(LocalTime visitTime) {
		return visitTime.compareTo(businessHours.getOpenTime()) >= 0;
	}

	private boolean isBeforeOrEqualLastOrderTime(LocalTime visitTime) {
		return visitTime.compareTo(businessHours.getLastOrderTime()) <= 0;
	}

	public List<LocalTime> generateTimeTable() {
		return Stream.iterate(businessHours.getOpenTime(),
				time -> time.plusSeconds(ReservationPolicy.UNIT_SECONDS)
			).limit(getRunningTime())
			.toList();
	}

	private int getRunningTime() {
		return businessHours.getLastOrderTime()
			.minusHours(businessHours.getOpenTime().getHour())
			.getHour() + 1;
	}
}
