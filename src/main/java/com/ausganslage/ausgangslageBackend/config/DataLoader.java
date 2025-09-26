package com.ausganslage.ausgangslageBackend.config;

import com.ausganslage.ausgangslageBackend.model.Person;
import com.ausganslage.ausgangslageBackend.model.Todo;
import com.ausganslage.ausgangslageBackend.repository.PersonRepository;
import com.ausganslage.ausgangslageBackend.repository.TodoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class DataLoader implements CommandLineRunner {

    private final PersonRepository personRepository;
    private final TodoRepository todoRepository;

    public DataLoader(PersonRepository personRepository, TodoRepository todoRepository) {
        this.personRepository = personRepository;
        this.todoRepository = todoRepository;
    }

    @Override
    public void run(String... args) {
        // Create Persons
        Person marco = new Person();
        marco.setName("Marco");
        personRepository.save(marco);

        Person anna = new Person();
        anna.setName("Anna");
        personRepository.save(anna);

        // Add Todos for Marco
        Todo t1 = new Todo();
        t1.setTitle("Finish Spring Boot project");
        t1.setCompleted(false);
        t1.setPerson(marco);
        todoRepository.save(t1);

        Todo t2 = new Todo();
        t2.setTitle("Write documentation");
        t2.setCompleted(true);
        t2.setPerson(marco);
        todoRepository.save(t2);

        // Add Todos for Anna
        Todo t3 = new Todo();
        t3.setTitle("Prepare presentation");
        t3.setCompleted(false);
        t3.setPerson(anna);
        todoRepository.save(t3);

        // Add a Todo without a person (optional)
        Todo t4 = new Todo();
        t4.setTitle("General task (no person)");
        t4.setCompleted(false);
        todoRepository.save(t4);

        System.out.println("Sample data loaded ✅");
    }
}

