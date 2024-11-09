package services;

import dtos.RequestDto;
import entities.Person;
import entities.Relationship;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RelationshipService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    PersonService personService;

    @Transactional
    public void createRelationShip(RequestDto requestDto) {
        System.out.println("calling method createRelationShip");
        Long person1Id = requestDto.getFromUser();
        Long person2Id = requestDto.getToUser();
        if (person1Id.equals(person2Id)) {
            throw new IllegalArgumentException("Impossible de créer une amitié entre une personne et elle-même.");
        }

        Person person1 = personService.findById(person1Id);
        Person person2 = personService.findById(person2Id);

        List<Relationship> relationships = person1.getAllRelationship();
        for (Relationship relationship : relationships) {
            if (Objects.equals(relationship.getRelationType(), requestDto.getRequestStr()) && (relationship.getFromUser().getId().equals(person2Id) || relationship.getToUser().getId().equals(person2Id))) {
                throw new IllegalArgumentException("Ces personnes sont déjà reliées.");
            }
        }

        Relationship relationship = new Relationship(requestDto.getRequestStr(), person1, person2);
        System.out.println("creating relationship");
        em.persist(relationship);
    }
}
