package com.ausganslage.ausgangslageBackend.controller;
import com.ausganslage.ausgangslageBackend.model.Person;
import com.ausganslage.ausgangslageBackend.repository.PersonRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    // New endpoint: return persons with todos included
    @GetMapping("/with-todos")
    public List<Person> getAllPersonsWithTodos() {
        return personRepository.findAll(); // todos are fetched automatically
    }

    @PostMapping
    public Person createPerson(@RequestBody Person person) {
        return personRepository.save(person);
    }
}
