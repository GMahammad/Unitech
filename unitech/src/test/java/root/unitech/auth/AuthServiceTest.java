package root.unitech.auth;

import root.unitech.requests.LoginRequest;
import root.unitech.requests.RegisterUserRequest;
import root.unitech.response.AuthenticationResponse;
import root.unitech.utils.JwtService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AuthService test cases")
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    private AuthService authService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(authRepository,  jwtService, authenticationManager,passwordEncoder);
    }

    @DisplayName("User COULD register successfully")
    @Test
    void registerUser_Success() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest("123456", "password");
        User user = User.builder().pin("123456").password("encodedPassword").role(Role.USER).build();

        when(authRepository.findByPin(registerUserRequest.getPin())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerUserRequest.getPassword())).thenReturn("encodedPassword");
        when(authRepository.save(user)).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        AuthenticationResponse response = authService.registerUser(registerUserRequest);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getToken()).isEqualTo("jwtToken");
        assertThat(response.getMessage()).isEqualTo("User registered successfully! You can get your token and send your request with an authorized way");
    }

    @DisplayName("User COULD NOT register, because userPin has been already registered!")
    @Test
    void registerUser_UserAlreadyRegistered() throws Exception {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest("123456", "password");
        User user = User.builder().pin("123456").password("encodedPassword").role(Role.USER).build();

        when(authRepository.findByPin(registerUserRequest.getPin())).thenReturn(Optional.of(new User()));
        AuthenticationResponse response = authService.registerUser(registerUserRequest);

        assertEquals("User has already registered!", response.getMessage());

    }


    @DisplayName("User COULD log in successfully")
    @Test
    void loginUser_Success() throws Exception{
        LoginRequest loginRequest = new LoginRequest("123456", "password");
        User user = User.builder().pin("123456").password("password").role(Role.USER).build();
        when(authRepository.findByPin(loginRequest.getPin())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");
        AuthenticationResponse response =authService.loginUser(loginRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.getToken()).isEqualTo("jwtToken");
        assertThat(response.getMessage()).isEqualTo("User logged in successfully! You can get your token and send your request with an authorized way");

    }
    @DisplayName("User COULD NOT Login in")
    @Test
    void loginUser_NotFound() throws Exception {
        LoginRequest loginRequest = new LoginRequest("123456", "password");
        when(authRepository.findByPin(loginRequest.getPin())).thenReturn(Optional.empty());

        AuthenticationResponse response = authService.loginUser(loginRequest);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getToken()).isNull();
        assertThat(response.getMessage()).isEqualTo("User credentials are not correct!");
    }
    @DisplayName("Admin COULD fetch all existed users")
    @Test
    void getAllUsers() {
        List<User> users = Arrays.asList(
                User.builder().pin("123").password("pass1").role(Role.USER).build(),
                User.builder().pin("456").password("pass2").role(Role.USER).build()
        );
        when(authRepository.findAll()).thenReturn(users);
        List<User> result = authService.getAllUsers();

        Assertions.assertThat(result).hasSize(2).containsExactlyElementsOf(users);
        verify(authRepository).findAll();

    }
}