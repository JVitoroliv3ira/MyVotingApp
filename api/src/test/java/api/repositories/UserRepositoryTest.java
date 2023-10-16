package api.repositories;

import api.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles(profiles = {"h2"})
@DataJpaTest
class UserRepositoryTest {
    private final UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.userRepository.deleteAll();
    }

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private User buildUserPayload(Long id, String email, String password) {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .build();
    }

    @Test
    void save_should_insert_new_user_in_table() {
        User payload = this.buildUserPayload(null, "payload@payload.com", "@payload");
        User result = this.userRepository.save(payload);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertTrue(result.getId() > 0);
    }

    @Test
    void exists_by_email_should_return_true_when_user_exists() {
        User payload = this.buildUserPayload(null, "payload@payload.com", "@payload");
        this.userRepository.save(payload);
        Boolean result = this.userRepository.existsByEmail(payload.getEmail());
        Assertions.assertTrue(result);
    }

    @Test
    void exists_by_email_should_return_true_when_user_does_not_exists() {
        String payload = "payload@payload.com";
        Boolean result = this.userRepository.existsByEmail(payload);
        Assertions.assertFalse(result);
    }

    @Test
    void find_by_email_should_return_optional_of_existing_user() {
        User payload = this.buildUserPayload(null, "payload@payload.com", "@payload");
        User createdUser = this.userRepository.save(payload);
        Optional<User> expected = Optional.of(createdUser);
        Optional<User> result = this.userRepository.findByEmail(payload.getEmail());
        Assertions.assertEquals(expected, result);
    }

    @Test
    void find_by_email_should_return_empty_optional_when_user_does_not_exists() {
        String payload = "payload@payload.com";
        Optional<User> result = this.userRepository.findByEmail(payload);
        Assertions.assertTrue(result.isEmpty());
    }
}