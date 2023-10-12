package api.dtos.requests;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserAuthenticationRequestDTO {
    @NotNull(message = "Informe um email.")
    @Size(min = 5, max = 80, message = "O email informado é inválido.")
    @Email(message = "O email informado é inválido.")
    private final String email;

    @NotNull(message = "Informe uma senha.")
    @Size(min = 8, max = 80, message = "A senha informada é inválida.")
    private final String password;
}
