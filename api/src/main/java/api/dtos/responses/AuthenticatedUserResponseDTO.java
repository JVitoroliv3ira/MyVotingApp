package api.dtos.responses;

import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

@Data
public class AuthenticatedUserResponseDTO {
    private final UserDetails details;
    private final String token;
}
