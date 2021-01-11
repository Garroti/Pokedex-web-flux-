package com.gabryelmacedo.pokedex.controller;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gabryelmacedo.pokedex.model.Pokemon;
import com.gabryelmacedo.pokedex.model.PokemonEvent;
import com.gabryelmacedo.pokedex.repository.PokedexRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pokemons")
public class PokemonController {

	private PokedexRepository repository;
	
	public PokemonController(PokedexRepository repository) {
		this.repository = repository;
	}
	
	@GetMapping
	public Flux<Pokemon> getAllPokemons() {
		return this.repository.findAll();
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Pokemon>> getPokemon(@PathVariable String id) {
		return repository.findById(id)
				.map(pokemon -> ResponseEntity.ok(pokemon))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Pokemon> savePokemon(@RequestBody Pokemon pokemon) {
		return this.repository.save(pokemon);
	}
	
	@PutMapping("{id}")
	public Mono<ResponseEntity<Pokemon>> updatePokemon(@PathVariable(value="id") String id, @RequestBody Pokemon pokemon) {
		return this.repository.findById(id)
				.flatMap(existingPokemon -> {
					existingPokemon.setNome(pokemon.getNome());
					existingPokemon.setCategoria(pokemon.getCategoria());
					existingPokemon.setHabilidade(pokemon.getHabilidade());
					existingPokemon.setPeso(pokemon.getPeso());
					return repository.save(existingPokemon);
				})
				.map(updatePokemon -> ResponseEntity.ok(updatePokemon))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("{id}")
	public Mono<ResponseEntity<Void>> deletePokemon(@PathVariable(value="id") String id) {
		return this.repository.findById(id)
				.flatMap(existingPokemon -> 
					this.repository.delete(existingPokemon)
						.then(Mono.just(ResponseEntity.ok().<Void>build()))
				)
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping
	public Mono<Void> deleteAllPokemons() {
		return this.repository.deleteAll();
	}
	
	@GetMapping(value="/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<PokemonEvent> getPokemonEvents() {
		return Flux.interval(Duration.ofSeconds(5))
				.map(val -> new PokemonEvent(val, "Evento de Pokemonsss"));
	}
	
}