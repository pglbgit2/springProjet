package entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.h2.engine.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
public class Relation {
    @Id @GeneratedValue
    private int id;
    private String relationType;
   @ManyToOne
   private UserEntity fromUser;
   @ManyToOne
   private UserEntity toUser;

    public Relation(){
        //this.relationsTypes = new HashSet<>();
    }

    public Relation(String relationType, UserEntity fromUser, UserEntity toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.relationType = relationType;
    }

    public String getOtherUser(String email){
        if(this.fromUser.getEmail().equals(email)){
            return this.toUser.getEmail();
        } else {
            return this.fromUser.getEmail();
        }
    }


}
