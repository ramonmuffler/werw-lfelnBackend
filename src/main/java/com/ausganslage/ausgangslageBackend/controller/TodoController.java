package com.ausganslage.ausgangslageBackend.controller;

import com.ausganslage.ausgangslageBackend.model.Todo;
import com.ausganslage.ausgangslageBackend.repository.PersonRepository;
import com.ausganslage.ausgangslageBackend.repository.TodoRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoRepository repository;
    private final PersonRepository personRepository;

    public TodoController(TodoRepository repository, PersonRepository personRepository) {
        this.repository = repository;
        this.personRepository = personRepository;
    }

    @GetMapping
    public List<Todo> getAllTodos() {
        return repository.findAll();
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        return repository.save(todo);
    }

    @PutMapping("/{id}")
    public Todo updateTodo(@PathVariable Long id, @RequestBody Todo updatedTodo) {
        return repository.findById(id)
                .map(todo -> {
                    todo.setTitle(updatedTodo.getTitle());
                    todo.setCompleted(updatedTodo.isCompleted());
                    return repository.save(todo);
                })
                .orElseThrow(() -> new RuntimeException("Todo not found"));
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @GetMapping("/search")
    public List<Todo> searchTodos(@RequestParam String q) {

        return repository.findByTitleContainingIgnoreCase(q);
    }

    @GetMapping("/person/{personId}")
    public List<Todo> getTodosByPerson(@PathVariable Long personId) {
        return repository.findByPersonId(personId);
    }

    @PostMapping("/person/{personId}")
    public Todo addTodoToPerson(@PathVariable Long personId, @RequestBody Todo todo) {
        return personRepository.findById(personId).map(person -> {
            todo.setPerson(person);
            return repository.save(todo);
        }).orElseThrow(() -> new RuntimeException("Person not found"));
    }

}
