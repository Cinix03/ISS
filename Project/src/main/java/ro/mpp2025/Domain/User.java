package ro.mpp2025.Domain;
import lombok.Data;

@Data
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private boolean activated;
}
