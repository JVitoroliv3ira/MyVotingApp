package api.services;

import api.exceptions.UnprocessableEntityException;
import api.models.User;
import api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private static final String EMAIL_ALREADY_IN_USE_ERROR_MESSAGE = "O email informado já está sendo utilizado por outro usuário.";
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
}
