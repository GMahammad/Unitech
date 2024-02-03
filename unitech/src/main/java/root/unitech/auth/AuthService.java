package root.unitech.auth;

import root.unitech.requests.LoginRequest;
import root.unitech.requests.RegisterUserRequest;
import root.unitech.response.AuthenticationResponse;
import root.unitech.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    //Register user taking registerUserRequest from controller.
    public AuthenticationResponse registerUser(RegisterUserRequest registerUserRequest) {
        try {
            checkPinPasswordValidity(registerUserRequest.getPin(), registerUserRequest.getPassword());

            if (authRepository.findByPin(registerUserRequest.getPin()).isPresent()) {
                throw new Exception("User has already registered!");
            }

            User user = createUserHelper(registerUserRequest);
            String jwtToken = jwtService.generateToken(user);

            return authenticationResponseBuilder(jwtToken, HttpStatus.OK, "User registered successfully! You can get your token and send your request with an authorized way");
        } catch (Exception e) {
            return authenticationResponseBuilder(null, HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

    //Login user taking loginUserRequest from controller.
    public AuthenticationResponse loginUser(LoginRequest loginRequest) {
        try {
            checkPinPasswordValidity(loginRequest.getPin(), loginRequest.getPassword());

            User user = getValidatedUser(loginRequest);
            String jwtToken = jwtService.generateToken(user);

            return authenticationResponseBuilder(jwtToken, HttpStatus.OK, "User logged in successfully! You can get your token and send your request with an authorized way");
        } catch (Exception e) {
            return authenticationResponseBuilder(null, HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }


    //ADMIN-> Select all users.
    public List<User> getAllUsers() {
        return authRepository.findAll();
    }


    //__Helper Methods__Start__
    private User getValidatedUser(LoginRequest loginRequest) throws Exception {
        try {
            User user = authRepository
                    .findByPin(loginRequest.getPin())
                    .orElseThrow();

            authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getPin(),
                                    loginRequest.getPassword()
                            )
                    );
            return user;
        } catch (Exception e) {
            throw new Exception("User credentials are not correct!");
        }
    }

    private void checkPinPasswordValidity(String pin, String password) throws Exception {
        if (pin == null || password == null) {
            throw new Exception("Please fill all fields!");
        }

        if (pin.isBlank() || password.isBlank()) {
            throw new Exception("Please do not enter empty value!");
        }

        if (pin.contains(" ") || password.contains(" ")) {
            pin.replaceAll(" ", "");
            password.replaceAll(" ", "");
        }

        if (password.length() < 5) {
            throw new Exception("Password length must be longer than 5 characters");
        }


    }

    public AuthenticationResponse authenticationResponseBuilder(String jwtToken, HttpStatus httpStatus, String message) {
        return AuthenticationResponse.
                builder()
                .token(jwtToken)
                .status(httpStatus)
                .message(message)
                .build();
    }

    public User createUserHelper(RegisterUserRequest registerUserRequest) {
        User user = User.builder()
                .pin(registerUserRequest.getPin())
                .password(passwordEncoder.encode(registerUserRequest.getPassword()))
                .role(Role.USER)
                .build();
        authRepository.save(user);
        return user;
    }
    //__Helper_Methods__End__

}
