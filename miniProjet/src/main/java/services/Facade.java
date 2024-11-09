package services;

import dtos.UserDto;
import entities.Relation;
import entities.UserEntity;
import exceptions.RelationAlreadyExistsException;
import exceptions.RelationWithYourselfException;
import exceptions.UserAllreadyExistsException;
import jakarta.persistence.*;
import org.h2.engine.User;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class Facade {
    // Injection de l'entity manager, pour accès à la BD
    @PersistenceContext
    private EntityManager em;

    public boolean checkEmailPassword(String email,String password) {
        try {
            UserEntity user = this.getUserByExactEmail(email);
            return Objects.equals(user.getPassword(), password);
        } catch(NoResultException nre){
            return false;
        } catch(NonUniqueResultException nure){
            System.out.println("Erreur serveur");
            return false;
        }
    }

    @Transactional
    public void createUser(String email, String password) throws UserAllreadyExistsException {
       try {
           UserEntity user = this.getUserByExactEmail(email);
           throw new UserAllreadyExistsException();
       } catch(NoResultException nre) {
            UserEntity user = new UserEntity(email, password);
            em.persist(user);
        } catch(NonUniqueResultException nure){
            System.out.println("Erreur Serveur");

        }
    }


    public UserEntity getUserByExactEmail(String email){
        //System.out.println("Searching for User with email:"+email);
        String queryStr = "Select u FROM UserEntity u WHERE u.email = :email";
        Query query = em.createQuery(queryStr);
        query.setParameter("email", email);
        return (UserEntity) query.getSingleResult();
    }

    public UserEntity getUserByApproximateEmail(String email) {
        String queryStr = "Select u FROM UserEntity u WHERE lower(u.email) LIKE :email";
        Query query = em.createQuery(queryStr);
        query.setParameter("email", "%"+email.toLowerCase()+"%");
        return (UserEntity) query.getSingleResult();
    }

    public List<UserEntity> getUsersFromName(String name) {
        String queryStr = "SELECT u FROM UserEntity u WHERE (lower(concat(u.surname,' ',u.familyName)) LIKE :name) OR (lower(concat(u.familyName,' ',u.surname)) LIKE :name)";
        Query q = em.createQuery(queryStr);
        q.setParameter("name", "%"+name.toLowerCase()+"%");
        return (List<UserEntity>) q.getResultList();
    }


    public List<Relation> getRelationsByTwoEmail(String firstEmail, String secondEmail) {
        String queryStr = "SELECT r FROM Relation r where (r.fromUser.email = :firstEmail AND r.toUser.email = :secondEmail) OR (r.fromUser.email = :secondEmail AND r.toUser.email = :firstEmail)";
        Query q = em.createQuery(queryStr);
        q.setParameter("firstEmail",  firstEmail);
        q.setParameter("secondEmail", secondEmail);
        return q.getResultList();
    }


    public Relation getRelationByTwoEmailAndType(String firstEmail, String secondEmail, String type) {
        String queryStr = "SELECT r FROM Relation r where (r.fromUser.email = :firstEmail AND r.toUser.email = :secondEmail AND r.relationType =:type) OR (r.fromUser.email = :secondEmail AND r.toUser.email = :firstEmail AND r.relationType =:type)";
        Query q = em.createQuery(queryStr);
        q.setParameter("firstEmail",  firstEmail);
        q.setParameter("secondEmail", secondEmail);
        q.setParameter("type", type);
        return (Relation) q.getSingleResult();
    }

    @Transactional
    public void createRelation(String fromEmail, String toEmail, List<String> relations) throws RelationAlreadyExistsException, RelationWithYourselfException {
        System.out.println("finding users");
        if(Objects.equals(fromEmail, toEmail)){
            throw new RelationWithYourselfException();
        }
        try {
            UserEntity fromUser = this.getUserByExactEmail(fromEmail);
            UserEntity toUser = this.getUserByExactEmail(toEmail);
            for (String relation : relations) {
                try {
                    Relation rel = this.getRelationByTwoEmailAndType(fromUser.getEmail(), toUser.getEmail(), relation);
                    System.out.println("Relation already exists");
                    throw new RelationAlreadyExistsException();
                } catch(NoResultException nure) {
                    System.out.println("creating new relations");
                    Relation rel = new Relation(relation, fromUser, toUser);
                    em.persist(rel);
                } catch(NonUniqueResultException nure){
                    System.out.println("Erreur Serveur");
                }
            }
        } catch(NoResultException | NonUniqueResultException nure){
            System.out.println("Erreur Serveur");
            return;
        }



    }

    @Transactional
    public void deleteUser(String email) {
        try {
            UserEntity user = this.getUserByExactEmail(email);

            for (Relation rel : this.getUserRelations(email)) {
                em.remove(rel);
            }
            em.remove(user);

        } catch(NoResultException nre) {
            return;
        } catch( NonUniqueResultException nure){
            System.out.println("Erreur Serveur");
            return;
        }
    }

    public String getUserName(String email) {
        try {
            UserEntity user = this.getUserByExactEmail(email);

            String username = user.getSurname();
            if (username == null || username.isEmpty()) {
                username = email.substring(0, email.indexOf('@'));
                if(username.contains(".")){
                    username = username.replace('.', ' ');
                }
                return username;
            } else {
                return username;
            }
        } catch(NoResultException nre){
            return null;
        } catch(NonUniqueResultException nure){
            System.out.println("Erreur Serveur");
            return null;
        }
    }

    public UserDto getUserProfile(String email) {
        try {
        UserEntity user = this.getUserByExactEmail(email);
        return new UserDto(user.getEmail(), user.getSurname(), user.getFamilyName(), user.getBirthDate());
        }  catch(NoResultException nre){
            return new UserDto();
        } catch(NonUniqueResultException nure){
            System.out.println("Erreur Serveur");
            return null;
        }
    }

    @Transactional
    public void updateUser(UserDto userDto) {
        try {
            //System.out.println("Updating user:"+userDto.getEmail());
            UserEntity user = this.getUserByExactEmail(userDto.getEmail());
            if(userDto.getSurname() != null) {
                user.setSurname(userDto.getSurname());
            }
            if(userDto.getFamilyName() != null) {
                user.setFamilyName(userDto.getFamilyName());
            }
            if(userDto.getBirthDate() != null) {
                user.setBirthDate(userDto.getBirthDate());
            }
           // System.out.println("updating user");
        }  catch(NoResultException nre){
            System.out.println("Erreur Serveur: Utilisateur introuvable");
            return;
        } catch(NonUniqueResultException nure){
            System.out.println("Erreur Serveur: Plusieurs utilisateurs ont le même email");
            return;
        }
    }

    public List<String> getAllUserNamesExcept(String email) {
        return (List<String>) em.createQuery("select u.email from UserEntity u where u.email<>:n").setParameter("n",email).getResultList();
    }

    public List<Relation> getUserRelations(String email) {
        String queryStr = "SELECT r FROM Relation r where r.fromUser.email = :email OR r.toUser.email = :email";
        Query q = em.createQuery(queryStr);
        q.setParameter("email",  email);
        return q.getResultList();
    }
}
