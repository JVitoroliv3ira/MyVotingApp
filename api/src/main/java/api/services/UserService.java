package api.services;

import api.dtos.DetailsDTO;
import api.exceptions.BadRequestException;
import api.exceptions.UnprocessableEntityException;
import api.models.User;
import api.repositories.UserRepository;
import api.utils.CryptographyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private static final String EMAIL_ALREADY_IN_USE_ERROR_MESSAGE = "O email informado já está sendo utilizado por outro usuário.";
    private static final String USER_NOT_FOUND_ERROR_MESSAGE = "Usuário não encontrado na base de dados.";
    private static final String USER_PASSWORD_INCORRECT_ERROR_MESSAGE = "A senha ou o endereço de e-mail está incorreto.";
    private final UserRepository repository;

    public User create(User user) {
        user.setId(null);
        return this.repository.save(user);
    }

    public void validateEmailUniqueness(String email) throws UnprocessableEntityException {
        if (Boolean.TRUE.equals(this.isEmailAlreadyInUse(email))) {
            throw new UnprocessableEntityException(EMAIL_ALREADY_IN_USE_ERROR_MESSAGE);
        }
    }

    private Boolean isEmailAlreadyInUse(String email) {
        return this.repository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws BadRequestException {
        User user = this.repository
                .findByEmail(email)
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND_ERROR_MESSAGE));
        return new DetailsDTO(user);
    }

    public void validatePasswordMatches(String rawPassword, String encodedPassword) {
        if (Boolean.FALSE.equals(CryptographyUtil.valuesMatches(rawPassword, encodedPassword))) {
            throw new BadRequestException(USER_PASSWORD_INCORRECT_ERROR_MESSAGE);
        }
    }
}
