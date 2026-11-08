package airline.dto;

import airline.entity.Flight;

public record AdminFlightRow(
    Flight flight,
    long bookedSeats,
    long totalSeats) {

}
