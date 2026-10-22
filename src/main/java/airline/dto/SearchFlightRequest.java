package airline.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;

public record SearchFlightRequest(
  @NotBlank 
    String origin,
          
  @NotBlank
    String destination,
          
  @NotBlank
  LocalDate start,
          
  @NotBlank 
  LocalDate end
) {
  
}
