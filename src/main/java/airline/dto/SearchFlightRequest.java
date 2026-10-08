package airline.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

public record SearchFlightRequest(
  @NotBlank 
    String origin,
          
  @NotBlank
    String destination,
          
  @NotBlank
  LocalDateTime start,
          
  @NotBlank 
  LocalDateTime end
) {
  
}
