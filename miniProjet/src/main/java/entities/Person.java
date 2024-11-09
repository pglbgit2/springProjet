package entities;

import dtos.PersonDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    private String firstName;
    @Setter
    private String lastName;
    @Basic @Setter
    private LocalDate birthDate;
    @OneToMany(mappedBy = "fromUser", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Relationship> askedRelationships;

    @OneToMany(mappedBy = "toUser", fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<Relationship> requestedRelationships;


    public Person(String surname, String familyName, LocalDate birthDate) {
        this.firstName = surname;
        this.lastName = familyName;
        this.birthDate = birthDate;
    }

    public Person(PersonDto personDto) {
        this.firstName = personDto.getFirstName();
        this.lastName = personDto.getLastName();
        this.birthDate = personDto.getBirthDate();
    }

    public List<Relationship> getAllRelationship() {
        List<Relationship> allRelationship = new ArrayList<>();
        if (askedRelationships != null) allRelationship.addAll(askedRelationships);
        if (requestedRelationships != null) allRelationship.addAll(requestedRelationships);
        return allRelationship;
    }


}
