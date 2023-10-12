package api.controllers.v1;

import api.dtos.requests.UserRegistrationRequestDTO;
import api.dtos.responses.ResponseDTO;
import api.models.User;
import api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(path = "/register")
    public ResponseEntity<ResponseDTO<User>> register(@Valid @RequestBody UserRegistrationRequestDTO request) {
        this.userService.validateEmailUniqueness(request.getEmail());
        User createdUser = this.userService.create(request.convert());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDTO<>(createdUser, null));
    }
}
