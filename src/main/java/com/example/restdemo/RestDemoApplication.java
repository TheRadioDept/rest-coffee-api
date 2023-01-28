package com.example.restdemo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@SpringBootApplication
@ConfigurationPropertiesScan 	// Process @ConfigurationProperties classes and add their properties to the
								// app's Environment.
public class RestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestDemoApplication.class, args);
	}
}

@Component
class DataLoader {
	private final CoffeeRepository coffeeRepository;

	public DataLoader(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	@PostConstruct
	private void loadData() {
		coffeeRepository.saveAll(List.of(new Coffee("Café Cereza"), new Coffee("Café Ganador"),
				new Coffee("Café Lareño"), new Coffee("Café Três Pontas")));
	}
}

@RestController
@RequestMapping("/greeting")
class GreetingController {

	private final Greeting greeting;

	public GreetingController(Greeting greeting) {
		this.greeting = greeting;
	}

	@GetMapping
	String getGreeting() {
		return greeting.getName();
	}

	@GetMapping("/coffee")
	String getNameAndCoffee() {
		return greeting.getCoffee();
	}

	/*
	 * @Value("${greeting-name: Mirage}") //Mirage is a default value (for cases in
	 * which the variable isn't defined in the app Environment). private String
	 * name;
	 * 
	 * @Value("${greeting-coffee: ${greeting-name} is drinking Cafe Cereza}")
	 * private String coffee;
	 * 
	 * @GetMapping String getGreeing(){ return name; }
	 * 
	 * @GetMapping("/coffee") String getNameAndCoffee(){ return coffee; }
	 */
}

@ConfigurationProperties(prefix = "greeting") // Register 'Greeting' to manage configuration properties.
												// This annotation prepares the class for use only with config.
												// properties.
class Greeting {
	private String name;
	private String coffee;

	public String getName() {
		return name;
	}

	public String getCoffee() {
		return coffee;
	}

	public void setCoffee(String coffee) {
		this.coffee = coffee;
	}

	public void setName(String name) {
		this.name = name;
	}
}

@RestController
@RequestMapping("/coffees")
class RestApiDemoController {
	private final CoffeeRepository coffeeRepository;

	public RestApiDemoController(CoffeeRepository coffeeRepository) {
		this.coffeeRepository = coffeeRepository;
	}

	/*
	 * GET-ting - retrieve all elements Retrieves all elements present.
	 */
	@GetMapping
	Iterable<Coffee> getCoffees() {
		return coffeeRepository.findAll();
	}

	/*
	 * GET-ting - retrieve an element by id Retrieves element with match Id.
	 */
	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id) {
		return coffeeRepository.findById(id);
	}

	/*
	 * POST-ting - create an object and add it to the list Services receives
	 * specified object details, and adds it to the list/database.
	 */
	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee) {
		return coffeeRepository.save(coffee);
	}

	/*
	 * PUT-ting - update existing object Uses Id to update object's details. If
	 * object does not exist - creates it.
	 */
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {

		return (coffeeRepository.existsById(id)) ? new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.OK)
				: new ResponseEntity<>(coffeeRepository.save(coffee), HttpStatus.CREATED);
	}

	// DELETE - delete element specified by ID.
	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id) {
		coffeeRepository.deleteById(id);
	}
}

interface CoffeeRepository extends CrudRepository<Coffee, String> {
}

// Indicates that Coffee is a persistable entity.
@Entity
class Coffee {
	@Id // Used to mark value as database table's ID field.
	private String id;
	private String name;

	public Coffee() {
	}

	public Coffee(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Coffee(String name) {
		this(UUID.randomUUID().toString(), name);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}