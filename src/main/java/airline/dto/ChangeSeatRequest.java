package airline.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeSeatRequest(
  @NotBlank 
  String newSeatNumber
) {}
