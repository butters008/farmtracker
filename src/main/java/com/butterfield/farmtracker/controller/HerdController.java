package com.butterfield.farmtracker.controller;

import com.butterfield.farmtracker.database.dao.*;
import com.butterfield.farmtracker.database.entity.Animal;
import com.butterfield.farmtracker.database.entity.ParentCalf;
import com.butterfield.farmtracker.database.entity.User;
import com.butterfield.farmtracker.database.entity.UserAnimal;
import com.butterfield.farmtracker.formBean.HerdFormBean;
import com.butterfield.farmtracker.security.SecurityService;
import com.butterfield.farmtracker.service.HerdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
public class HerdController {

    @Autowired
    private HerdDAO herdDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserAnimalDAO userAnimalDAO;

    @Autowired
    private CalfDAO calfDAO;

    @Autowired
    private ParentCalfDAO parentCalfDAO;

    @Autowired
    private SecurityService securityService = new SecurityService();

    @Autowired
    private HerdService herdService = new HerdService();


    @RequestMapping(value = "/herd/list", method = RequestMethod.GET)
    public ModelAndView listAllCows() throws Exception {
        ModelAndView response = new ModelAndView();

        User userLoggedIn = securityService.getLoggedInUser();
        List<UserAnimal> userAnimals =  userAnimalDAO.findByUserId(userLoggedIn);
        response.addObject("herd", userAnimals);

        return response;
    }

    //For right now I am going to hard code in list of cows
    @RequestMapping(value = "/herd/herdinfo", method = RequestMethod.GET)
    public ModelAndView getCowsById1(@RequestParam("cowId") String cowId) throws Exception {
        ModelAndView response = new ModelAndView();

        Animal animal = herdDAO.findByAnimalId1(cowId);
        List<ParentCalf> parentCalves = parentCalfDAO.findAllByCowId(animal.getId());
        log.info(parentCalves.toString());

        response.addObject("calves", parentCalves);

        response.setViewName("herd/herdinfo");
        response.addObject("herd", animal);
        return response;

    }

    //The initial get for addAnimal jsp page
    @RequestMapping(value = "/herd/addAnimal", method = RequestMethod.GET)
    public ModelAndView addAnimalInital() throws Exception {
        ModelAndView response = new ModelAndView();

        response.setViewName("herd/addAnimal");
        return response;
    }

    @RequestMapping(value = "/herd/submitAnimal", method = RequestMethod.POST)
    public ModelAndView submitAnimal(
            @Valid HerdFormBean form,
            @RequestParam("dateOfBirth") String dob,
            @RequestParam("dateOfDeath") String dod,
            @RequestParam("boughtDate") String bDate,
            BindingResult bindingResult) throws Exception {
        ModelAndView response = new ModelAndView();

        //Getting the info of the user that logged in
        User userLoggedIn = securityService.getLoggedInUser();

        //And extra check to make sure no one is bypassing login
        if (userLoggedIn == null) {
            response.setViewName("redirect:/index");
        }
        else{
            //Creating the animal object
            Animal animal = new Animal();

            animal =animalObjectInfo(form, dob, dod, bDate, animal, herdService);

            //Saving the animal to the DB
            herdDAO.save(animal);

            //Creating a new userAnimal and submitting this to DB
            UserAnimal userAnimal = new UserAnimal();

            userAnimal.setUserId(userLoggedIn);
            userAnimal.setAnimalId(animal);

            userAnimalDAO.save(userAnimal);
            response.setViewName("herd/addAnimal");

        }
        return response;
    }

    @RequestMapping(value = "/herd/updateAnimal/{aID}", method = {RequestMethod.POST, RequestMethod.GET})
    public ModelAndView updateAnimal( @PathVariable("aID") Integer aID,
            @Valid HerdFormBean form, BindingResult bindingResult,
            @RequestParam("dateOfBirth") String dob, @RequestParam("dateOfDeath") String dod,
            @RequestParam("boughtDate") String bDate) throws Exception {
        log.info("Before response");
        ModelAndView response = new ModelAndView();
        log.info("Form bean brought in" + form.toString());

        if(bindingResult.hasErrors()){
            log.info("We are inside the funtion");
            List<String> errorMessages = new ArrayList<>();

            for (ObjectError error : bindingResult.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
                log.info(((FieldError) error).getField() + " " + error.getDefaultMessage());
            }

            log.info(errorMessages.toString());
            log.info("Form Bean" + form.toString());
            response.addObject("bindingResult", bindingResult);
            response.addObject("herd", form);
//            response.setViewName("redirect:/herd/herdinfo?cowId= " + form.getAnimalId1() + "");
//            response.
            response.setViewName("herd/herdinfo");
            return response;
        }//End of error handing

        Animal animal = herdDAO.findById(aID);
        animal = animalObjectInfo(form, dob, dod, bDate, animal, herdService);
        herdDAO.save(animal);
        response.setViewName("redirect:/herd/list");
        return response;
    }

    private static Animal animalObjectInfo(@Valid HerdFormBean form, @RequestParam("dateOfBirth") String dob, @RequestParam("dateOfDeath") String dod, @RequestParam("boughtDate") String bDate, Animal animal, HerdService herdService) {
        animal.setAnimalId1(form.getAnimalId1());
        animal.setAnimalId2(form.getAnimalId2());
        animal.setAnimalType(form.getAnimalType());
        animal.setBreed(form.getBreed());
        animal.setHerdStatus(form.getHerdStatus());
        animal.setBoughtFrom(form.getBoughtFrom());
        animal.setDateOfBirth(herdService.processDates(dob));
        animal.setDateOfDeath(herdService.processDates(dod));
        animal.setBoughtDate(herdService.processDates(bDate));
        return animal;
    }


    @RequestMapping(value = "/herd/delete/{aID}", method = RequestMethod.GET)
    public ModelAndView deleteAnimal(@PathVariable("aID") Integer aID) throws Exception {
        ModelAndView response = new ModelAndView();

        Animal animalBegone = herdDAO.findById(aID);
        UserAnimal userAnimalBegone = userAnimalDAO.findByAnimalId(animalBegone);
        userAnimalDAO.delete(userAnimalBegone);
        herdDAO.delete(animalBegone);

        response.setViewName("redirect:/index");
        return response;
    }


}
