package airline.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @Pattern(regexp = ".*\\S.*", message = "must not be blank") 
    String name,
                
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Email
    @Pattern(regexp = ".*\\S.*", message = "must not be blank")
    String email)
) {}