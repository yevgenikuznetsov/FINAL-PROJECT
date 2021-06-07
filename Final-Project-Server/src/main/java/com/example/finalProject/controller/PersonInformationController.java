package com.example.finalProject.controller;

import com.example.finalProject.object.Person;
import com.example.finalProject.object.Remark;
import com.example.finalProject.service.interfaces.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class PersonInformationController {
    private final PersonService personService;

    @Autowired
    public PersonInformationController(PersonService personService) {
        this.personService = personService;
    }

    // Get all suspicious messages written by the person
    @GetMapping("/getPersonInformation/")
    public Person getPersonInfo(@RequestParam("personName") String name) {
        return personService.getPerson(name);
    }

    // Add a new comment for this person
    @PostMapping("/addRemark/")
    public void addRemark(@RequestParam("name") String name, @RequestBody Remark remark) {
        this.personService.SaveRemarkToDb(remark, name);
    }

    // Get all remarks written for this person
    @GetMapping("/getRemark/")
    public Remark getAllRemark(@RequestParam("name") String name) {
        return this.personService.readRemarkFromDB(name);
    }
}
