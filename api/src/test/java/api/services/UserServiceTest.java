package api.services;

import api.dtos.DetailsDTO;
import api.exceptions.BadRequestException;
import api.exceptions.UnprocessableEntityException;
import api.models.User;
import api.repositories.UserRepository;
import api.utils.CryptographyUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = {"h2"})
@SpringBootTest
class UserServiceTest {
    @Mock
    private final UserRepository userRepository;
    private UserService userService;
    private static final String EMAIL_ALREADY_IN_USE_ERROR_MESSAGE = "O email informado já está sendo utilizado por outro usuário.";
    private static final String USER_NOT_FOUND_ERROR_MESSAGE = "Usuário não encontrado na base de dados.";
    private static final String USER_PASSWORD_INCORRECT_ERROR_MESSAGE = "A senha ou o endereço de e-mail está incorreto.";

    @Autowired
    public UserServiceTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        this.userService = new UserService(this.userRepository);
    }

    private User buildUserPayload(Long id, String email, String password) {
        return User.builder()
                .id(id)
                .email(email)
                .password(password)
                .build();
    }

    @Test
    void create_should_call_save_method_of_user_repository() {
        User payload = this.buildUserPayload(null, "payload@payload.com", "@payload");
        this.userService.create(payload);
        verify(this.userRepository, times(1)).save(payload);
    }

    @Test
    void create_should_set_payload_id_to_null_before_call_save_method_of_user_repository() {
        User payload = this.buildUserPayload(7L, "payload@payload.com", "@payload");
        this.userService.create(payload);
        verify(this.userRepository, times(1))
                .save(this.buildUserPayload(null, "payload@payload.com", "@payload"));
    }

    @Test
    void create_should_return_created_user() {
        User payload = this.buildUserPayload(null, "payload@payload.com", "@payload");
        User expected = this.buildUserPayload(1L, "payload@payload.com", "@payload");
        when(this.userRepository.save(payload)).thenReturn(expected);
        User result = this.userService.create(payload);
        Assertions.assertEquals(expected, result);
    }

    @Test
    void validate_email_uniqueness_should_call_exists_by_email_method_of_user_repository() {
        String payload = "payload@payload.com";
        when(this.userRepository.existsByEmail(payload)).thenReturn(Boolean.FALSE);
        this.userService.validateEmailUniqueness(payload);
        verify(this.userRepository, times(1)).existsByEmail(payload);
    }

    @Test
    void validate_email_uniqueness_should_throw_an_exception_when_email_is_already_in_use() {
        String payload = "payload@payload.com";
        when(this.userRepository.existsByEmail(payload)).thenReturn(Boolean.TRUE);
        assertThatThrownBy(() -> this.userService.validateEmailUniqueness(payload))
                .isInstanceOf(UnprocessableEntityException.class)
                .hasMessage(EMAIL_ALREADY_IN_USE_ERROR_MESSAGE);
    }

    @Test
    void load_user_by_username_should_call_find_by_email_method_of_user_repository() {
        User payload = this.buildUserPayload(1L, "payload@payload.com", "@payload");
        when(this.userRepository.findByEmail(payload.getEmail())).thenReturn(Optional.of(payload));
        this.userService.loadUserByUsername(payload.getEmail());
        verify(this.userRepository, times(1)).findByEmail(payload.getEmail());
    }

    @Test
    void load_user_by_username_should_throw_an_exception_when_user_is_not_found() {
        String payload = "payload@payload.com";
        when(this.userRepository.findByEmail(payload)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> this.userService.loadUserByUsername(payload))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(USER_NOT_FOUND_ERROR_MESSAGE);
    }

    @Test
    void load_user_by_username_should_return_details_of_requested_user() {
        User payload = this.buildUserPayload(1L, "payload@payload.com", "@payload");
        UserDetails expected = new DetailsDTO(payload);
        when(this.userRepository.findByEmail(payload.getEmail())).thenReturn(Optional.of(payload));
        UserDetails result = this.userService.loadUserByUsername(payload.getEmail());
        Assertions.assertEquals(expected, result);
    }

    @Test
    void validate_password_matches_should_call_matches_method_of_cryptography_util() {
        try (MockedStatic<CryptographyUtil> mocked = Mockito.mockStatic(CryptographyUtil.class)) {
            String rawPayload = "rawValue";
            String encodedPayload = "encodedValue";
            mocked.when(() -> CryptographyUtil.valuesMatches(rawPayload, encodedPayload)).thenReturn(Boolean.TRUE);
            this.userService.validatePasswordMatches(rawPayload, encodedPayload);
            mocked.verify(() -> CryptographyUtil.valuesMatches(rawPayload, encodedPayload), times(1));
        }
    }

    @Test
    void validate_password_matches_should_throw_an_exception_when_passwords_does_not_matches() {
        try (MockedStatic<CryptographyUtil> mocked = Mockito.mockStatic(CryptographyUtil.class)) {
            String rawPayload = "rawValue";
            String encodedPayload = "encodedValue";
            mocked.when(() -> CryptographyUtil.valuesMatches(rawPayload, encodedPayload)).thenReturn(Boolean.FALSE);
            assertThatThrownBy(() -> this.userService.validatePasswordMatches(rawPayload, encodedPayload))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(USER_PASSWORD_INCORRECT_ERROR_MESSAGE);
        }
    }
}