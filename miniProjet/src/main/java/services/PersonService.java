package services;

import dtos.PersonDto;
import entities.Person;
import exceptions.UserAllreadyExistsException;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class PersonService {
    @PersistenceContext
    private EntityManager em;


    @Transactional
    public void createUser(PersonDto personDto) throws UserAllreadyExistsException {
            Person user = new Person(personDto);
            em.persist(user);
    }


    public List<PersonDto> findAll() {
        return (List<PersonDto>) em.createQuery("select u from Person u").getResultList();
    }

    public Person findById(Long personId) {
        return em.find(Person.class, personId);
    }
}
