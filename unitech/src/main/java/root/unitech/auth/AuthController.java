package root.unitech.auth;

import root.unitech.requests.LoginRequest;
import root.unitech.requests.RegisterUserRequest;
import root.unitech.response.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthRepository authRepository;
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterUserRequest registerUserRequest) throws Exception {
        AuthenticationResponse response = authService.registerUser(registerUserRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity loginUser(@RequestBody LoginRequest loginRequest) {
        AuthenticationResponse response = authService.loginUser(loginRequest);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<User> getAllUsers() {
        return authService.getAllUsers();
    }


}
