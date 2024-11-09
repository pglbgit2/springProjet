package controllers;

import dtos.RequestDto;
import entities.Person;
import entities.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import services.PersonService;
import services.RelationshipService;

import java.util.List;

@Controller
@RequestMapping("/relationships")
public class RelationshipController {

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    private PersonService personService;

    @GetMapping("/new")
    public String showCreateFriendshipForm(Model model) {

        model.addAttribute("people", personService.findAll());
        model.addAttribute("requestDto", new RequestDto());
        return "relationship/create-relation";
    }

    @PostMapping("/create")
    public String createFriendship(RequestDto requestDto, Model model, RedirectAttributes redirectAttributes) {
        System.out.println("createFriendship endpoint reached");
        if(requestDto.getFromUser() == null || requestDto.getToUser() == null || requestDto.getRequestStr() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please fill all the required fields");
            return "redirect:/relationships/new";
        }
        try {
            System.out.println("creating relationship");
            relationshipService.createRelationShip(requestDto);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while creating the friendship: " + e.getMessage());
            return "redirect:/relationships/new";
        }
        return "redirect:/relationships/" + requestDto.getToUser();
    }

    @GetMapping("/{personId}")
    public String getFriendships(@PathVariable("personId") Long personId, Model model) {
        System.out.println("getFriendShip endpoint reached");
        Person person = personService.findById(personId);
        if (person == null) {
            return "redirect:/persons";
        }
        model.addAttribute("person", person);
        List<Relationship> relationships = person.getAllRelationship();
        if (!relationships.isEmpty()) {
            System.out.println("Liste des relations de " + person.getFirstName() + ": " + relationships);
            model.addAttribute("relations", relationships);
        }
        return "relationship/relationships";
    }
}
