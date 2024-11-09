package entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Relationship {
    @Id @GeneratedValue
    private int id;
    @Setter
    private String relationType;
    @ManyToOne
    @Setter
    private Person fromUser;
    @Setter
    @ManyToOne
    private Person toUser;

    public Relationship(){

    }

    public Relationship(String relationType, Person fromUser, Person toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.relationType = relationType;
    }

    public Person getOtherPerson(Long id){
        if(this.fromUser.getId().equals(id)){
            return this.toUser;
        } else {
            return this.fromUser;
        }
    }


}
