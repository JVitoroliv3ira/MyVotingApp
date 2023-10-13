package api.controllers.v1;

import api.dtos.requests.UserAuthenticationRequestDTO;
import api.dtos.requests.UserRegistrationRequestDTO;
import api.dtos.responses.AuthenticatedUserResponseDTO;
import api.dtos.responses.ResponseDTO;
import api.models.User;
import api.services.JwtService;
import api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping(path = "/api/v1/auth")
@RestController
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping(path = "/register")
    public ResponseEntity<ResponseDTO<User>> register(@Valid @RequestBody UserRegistrationRequestDTO request) {
        this.userService.validateEmailUniqueness(request.getEmail());
        User createdUser = this.userService.create(request.convert());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(createdUser, null));
    }

    @PostMapping(path = "/login")
    public ResponseEntity<ResponseDTO<AuthenticatedUserResponseDTO>> login(@Valid @RequestBody UserAuthenticationRequestDTO request) {
        UserDetails details = this.userService.loadUserByUsername(request.getEmail());
        this.userService.validatePasswordMatches(request.getPassword(), details.getPassword());
        String token = this.jwtService.generateToken(details);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDTO<>(new AuthenticatedUserResponseDTO(details, token), null));
    }
}
