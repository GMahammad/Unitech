package root.unitech.requests;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest  {
    private String pin;
    private String password;
}
