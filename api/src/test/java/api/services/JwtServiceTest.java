package api.services;

import api.dtos.DetailsDTO;
import api.models.User;
import io.jsonwebtoken.impl.DefaultClaims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

@ActiveProfiles(profiles = {"h2"})
@SpringBootTest
class JwtServiceTest {
    private JwtService jwtService;
    private final String TEST_SECRET = "TEST_SECRET_THAT_IS_LONG_ENOUGH_FOR_THIS_DEMO";
    private final int TEST_EXPIRATION = 3600000;
    private final String INVALID_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    @BeforeEach
    void setUp() {
        this.jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiresIn", TEST_EXPIRATION);
    }

    private DetailsDTO buildDetailsDTOPayload(Long id, String email, String password) {
        return new DetailsDTO(User.builder().id(id).email(email).password(password).build());
    }

    @Test
    void generate_token_should_return_not_null_jwt_token() {
        DetailsDTO payload = this.buildDetailsDTOPayload(1L, "payload@payload.com", "@payload");
        String result = this.jwtService.generateToken(payload);
        Assertions.assertNotNull(result);
    }

    @Test
    void get_token_subject_should_return_user_email() {
        DetailsDTO payload = this.buildDetailsDTOPayload(1L, "payload@payload.com", "@payload");
        String payloadToken = this.jwtService.generateToken(payload);
        String result = this.jwtService.getTokenSubject(payloadToken);
        Assertions.assertEquals(payload.getUsername(), result);
    }

    @Test
    void get_token_subject_should_return_null_for_an_invalid_token() {
        Assertions.assertNull(this.jwtService.getTokenSubject(INVALID_TOKEN));
    }

    @Test
    void token_is_expired_should_return_false_for_non_expired_token() {
        DetailsDTO payload = this.buildDetailsDTOPayload(1L, "payload@payload.com", "@payload");
        String result = this.jwtService.generateToken(payload);
        Assertions.assertFalse(this.jwtService.tokenIsExpired(result));
    }

    @Test
    void token_is_valid_should_return_true_for_valid_token() {
        DetailsDTO payload = this.buildDetailsDTOPayload(1L, "payload@payload.com", "@payload");
        String result = this.jwtService.generateToken(payload);
        Assertions.assertTrue(this.jwtService.tokenIsValid(result));
    }

    @Test
    void token_is_valid_should_return_false_for_invalid_token() {
        Assertions.assertFalse(this.jwtService.tokenIsValid(INVALID_TOKEN));
    }
}