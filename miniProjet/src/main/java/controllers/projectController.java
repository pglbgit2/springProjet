package controllers;

import dtos.LoginUserDto;
import dtos.RequestDto;
import dtos.UserDto;
import exceptions.RelationAlreadyExistsException;
import exceptions.RelationWithYourselfException;
import exceptions.UserAllreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import services.Facade;

import java.util.Objects;

@Controller
@SessionAttributes({"courant"})
@RequestMapping("/")
public class projectController {
    @Autowired
    private Facade facade;
    @RequestMapping("")
    public String toLogin(Model model) {
        model.addAttribute(new LoginUserDto());
        return("login");
    }

    @PostMapping("login")
    public String checkLP(LoginUserDto loginUserDto, BindingResult result, Model model){
        if (facade.checkEmailPassword(loginUserDto.getEmail(), loginUserDto.getPassword())){
            getWelcomeFromLoginRegister(loginUserDto, model);
            return "welcome";
        } else{
            result.addError(new ObjectError("user","Les informations saisies ne correspondent pas à un utilisateur connu."));
            return "login";
        }
    }

    @PostMapping("register")
    public String register(LoginUserDto loginUserDto,BindingResult result, Model model){
        try {
            facade.createUser(loginUserDto.getEmail(), loginUserDto.getPassword());
        } catch(UserAllreadyExistsException exception){
            result.addError(new ObjectError("user", "Ce login n'est pas disponible"));
            return "login";
        }
        getWelcomeFromLoginRegister(loginUserDto, model);
        return "welcome";
    }

    /**
     *
     * @param loginUserDto : LoginUserDto
     * @param model : Model
     */
    private void getWelcomeFromLoginRegister(LoginUserDto loginUserDto, Model model) {
        model.addAttribute("username", facade.getUserName(loginUserDto.getEmail()));
        model.addAttribute("courant", loginUserDto.getEmail());
        model.addAttribute("userDto", facade.getUserProfile(loginUserDto.getEmail()));
        model.addAttribute("users",facade.getAllUserNamesExcept(loginUserDto.getEmail()));
        model.addAttribute("requestDto",new RequestDto());
        model.addAttribute("relations", facade.getUserRelations(loginUserDto.getEmail()));
    }

    @PostMapping("profil")
    public String profil(UserDto userDto, @SessionAttribute("courant") String courant, Model model){
        userDto.setEmail(courant);
        facade.updateUser(userDto);
        getWelcome(courant, model);
        return "welcome";
    }

    @RequestMapping("logout")
    public String logout(SessionStatus status,Model model) {
        status.setComplete();
        model.addAttribute("courant",null);
        model.addAttribute("username",null);
        model.addAttribute(new LoginUserDto());
        model.addAttribute("userDto", new UserDto());
        return "login";
    }

    @PostMapping("request")
    public String request(RequestDto requestDto, @SessionAttribute("courant") String courant, BindingResult result, Model model){
        requestDto.setRequest(requestDto.getRequestStr());
        try {
            facade.createRelation(requestDto.getTo(), courant, requestDto.getRequest());
        } catch(RelationAlreadyExistsException exception){
            result.addError(new ObjectError("otherRelationError", "La relation existe deja"));
            model.addAttribute("error","La relation existe deja");

        } catch (RelationWithYourselfException e) {
            result.addError(new ObjectError("selfRelationError", "Vous ne pouvez pas vous lier avec vous meme"));
            model.addAttribute("error","Vous ne pouvez pas vous lier avec vous meme");
        }
        getWelcome(courant, model);
        return "welcome";
    }

    /**
     *
     * @param courant : LoginUserDto
     * @param model : Model
     */
    private void getWelcome(@SessionAttribute("courant") String courant, Model model) {
        model.addAttribute("username", facade.getUserName(courant));
        model.addAttribute("courant", courant);
        model.addAttribute("userDto", facade.getUserProfile(courant));
        model.addAttribute("users",facade.getAllUserNamesExcept(courant));
        model.addAttribute("requestDto", new RequestDto());
        model.addAttribute("relations", facade.getUserRelations(courant));
    }



}
