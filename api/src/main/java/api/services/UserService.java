package api.services;

import api.models.User;
import api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;
    
    public User create(User user) {
        user.setId(null);
        return this.repository.save(user);
    }

    public Boolean isEmailAlreadyInUse(String email) {
        return this.repository.existsByEmail(email);
    }
}
