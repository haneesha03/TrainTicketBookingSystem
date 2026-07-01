package com.train.model;

import java.time.LocalDate;

public record Booking(int bookingId,int trainId,int passengerId,LocalDate bookingDate) {
	

}
