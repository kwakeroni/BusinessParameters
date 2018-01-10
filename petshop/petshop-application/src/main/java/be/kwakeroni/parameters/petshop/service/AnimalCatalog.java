package be.kwakeroni.parameters.petshop.service;

import be.kwakeroni.parameters.petshop.model.Animal;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Stream;

public class AnimalCatalog {

    private static Collection<String> ANIMALS = new TreeSet<>(Arrays.asList("Cat", "Dog", "Goldfish", "Gerbil", "Phoenix"));
    private static Map<String, Integer> PRICES = new HashMap<>();

    {
        PRICES.put("Cat", 80);
        PRICES.put("Dog", 70);
        PRICES.put("Goldfish", 10);
        PRICES.put("Gerbil", 35);
        PRICES.put("Phoenix", 250);
    }

    public Stream<Animal> getAnimals() {
        return ANIMALS.stream()
                .map(this::getAnimal);
    }

    public Animal getAnimal(String species) {
        Integer price = PRICES.computeIfAbsent(species,
                s -> {
                    throw new IllegalArgumentException("No such animal: " + s);
                });

        return new Animal(species, price);
    }

}
