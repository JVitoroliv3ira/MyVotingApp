package api.dtos.requests;

import api.models.User;
import api.utils.CryptographyUtil;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class UserRegistrationRequestDTO {
    @NotNull(message = "Informe um email.")
    @Size(min = 5, max = 80, message = "O email deve conter entre {min} e {max} caracteres.")
    @Email(message = "O email informado é inválido.")
    private final String email;

    @NotNull(message = "Informe uma senha.")
    @Size(min = 8, max = 80, message = "A senha deve conter entre {min} e {max} caracteres.")
    private final String password;

    public User convert() {
        return User
                .builder()
                .email(this.getEmail())
                .password(CryptographyUtil.encodeValue(this.getPassword()))
                .build();
    }
}
