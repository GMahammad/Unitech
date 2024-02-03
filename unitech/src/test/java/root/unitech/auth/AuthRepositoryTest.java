package root.unitech.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("AuthRepository test cases:")
class AuthRepositoryTest {

    @Autowired
    private AuthRepository authRepository;

    @AfterEach
    void tearDown() {
        authRepository.deleteAll();
    }

    @DisplayName("Repository COULD select user by given pin")
    @Test
    void itShouldCheckUserByPin() {
        User user = User.builder()
                .pin("2001")
                .password("test123")
                .role(Role.USER)
                .build();
        authRepository.save(user);
        User expectedUser = authRepository.findByPin(user.getPin()).orElse(null);

        assertThat(expectedUser).isNotNull();
        assertThat(expectedUser.getPin()).isEqualTo(user.getPin());
    }

    @DisplayName("Repository COULD NOT select user by given wrong pin")
    @Test
    void itShouldCheckUnregisteredUserByPin() {

        String wrongUserPin = "11111";

        User expectedUser = authRepository.findByPin(wrongUserPin).orElse(null);
        assertThat(expectedUser).isNull();
    }
}
