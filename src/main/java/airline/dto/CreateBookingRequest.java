package airline.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBookingRequest(
    @NotBlank 
    String flightNumber,
        
    @NotBlank 
    String seatNumber
) {}
