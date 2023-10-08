package api.models;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(schema = "voting_app", name = "tb_users")
@Entity
public class User {
    @Id
    @SequenceGenerator(name = "sq_users", schema = "voting_app", sequenceName = "sq_users", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_users")
    @Column(name = "id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password);
    }
}
