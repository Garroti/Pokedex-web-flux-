package com.gabryelmacedo.pokedex.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.gabryelmacedo.pokedex.model.Pokemon;

public interface PokedexRepository extends ReactiveMongoRepository <Pokemon, String> {

}
