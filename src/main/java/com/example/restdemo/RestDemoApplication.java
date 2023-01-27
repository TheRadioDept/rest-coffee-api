package com.example.restdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RestDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestDemoApplication.class, args);
	}
}


class Coffee {
	private final String id;
	private String name;

	public Coffee(String id, String name){
		this.id = id;
		this.name = name;
	}

	public Coffee(String name){
		this(UUID.randomUUID().toString(), name);
	}

	public String getId(){
		return id;
	}

	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
}

@RestController
@RequestMapping("/coffees")
class RestApiDemoController{
	private ArrayList<Coffee> coffees = new ArrayList<>();

	public RestApiDemoController(){
		coffees.addAll(List.of(
			new Coffee("Cafe Cereza"),
			new Coffee("Cafe Latte"),
			new Coffee("Cafe Lareno"),
			new Coffee("Cafe Tres Pontas")
		));
	}

	//Method that returns iterable group of coffees.
	//To the @RequestMappint annotation, we add path specification of /coffees and a method type of RequestMethod.GET,
	//Indicating that  the method will only respond to requests with the path of /coffees and restrict requests to only HTTP GET requests.
	//Retrieval of data is handled by this method, but updates are not.
	@GetMapping()
	Iterable<Coffee> getCoffees(){
		return coffees;
	}

	/*GET-ting  RETRIEVE
	 * Retrieving all coffees is good. But what if we need to retrieve one particular coffee?
	 * To do so, we will add method RetrieveCoffeeById.
	 * the {id} portion of the specified path is a URI variable, and its value is passed to the getCoffeeById method via the id method parameter by annotating it with @PathVariable
	*/
	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id){
		//For every coffee object in coffees
		for (Coffee c: coffees){
			//if coffee id is equals to id
			if(c.getId().equals(id)){
				return Optional.of(c);
			}
		}
		return Optional.empty();
	}

	//@POST-ing  CREATE
	/* To create resources, an HTTP POST method is the preffered option.
	 * Our service receives the specified coffee details as a Coffee object
	 * and adds it to our list of coffees.
	 * It returns the Coffee object to the requesting application or service.
	 */
	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee){
		coffees.add(coffee);
		return coffee;
	}

	/* PUT-ting  UPDATE
	 * Generally speaking, PUT requests are used to update existing resources with known URIs
	 * Following code searches for the coffee with the specified identifier, and if found, update it.
	 * If no such coffee is contained within, creates it.
	 */
	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee) {
		int coffeeIndex = -1;
		for (Coffee c: coffees) {
			if (c.getId().equals(id)) {
				coffeeIndex = coffees.indexOf(c);
				coffees.set(coffeeIndex, coffee);
			}
		}
		return (coffeeIndex == -1) ?
		new ResponseEntity<>(postCoffee(coffee), HttpStatus.CREATED) :
		new ResponseEntity<>(coffee, HttpStatus.OK);
		}

	/* DELET-ing   DELETE
	 * To delete resource, we use an HTTP DELETE request.
	 * We create a method that accepts a coffee's ID as an @PathVariable and removes the applicable coffee from our list
	 * using removeIf Collection method.
	 */
	@DeleteMapping("/{id}")
	void deleteCoffee(@PathVariable String id){
		coffees.removeIf(c -> c.getId().equals(id));
	}
}