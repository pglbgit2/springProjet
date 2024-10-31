package entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class UserEntity {
    @Id
    private String email;
    @Setter
    private String surname;
    @Setter
    private String familyName;
    @Setter
    private String password;
    @Basic @Setter
    private LocalDate birthDate;



    public UserEntity(String email, String surname, String familyName, String password, LocalDate birthDate) {
        this.email = email;
        this.surname = surname;
        this.familyName = familyName;
        this.password = password;
        this.birthDate = birthDate;
    }

    public UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
        this.birthDate = null;
        this.surname = null;
        this.familyName = null;
    }
}
