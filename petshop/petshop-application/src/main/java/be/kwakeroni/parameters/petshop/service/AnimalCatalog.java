package be.kwakeroni.parameters.petshop.service;

import be.kwakeroni.parameters.petshop.model.Animal;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.stream.Stream;

public class AnimalCatalog {

    private static TreeSet<Animal> ANIMALS = new TreeSet<>(Comparator.comparing(Animal::getSpecies));

    {
        ANIMALS.add(new Animal("Cat", 80));
        ANIMALS.add(new Animal("Dog", 70));
        ANIMALS.add(new Animal("Goldfish", 10));
        ANIMALS.add(new Animal("Gerbil", 35));
        ANIMALS.add(new Animal("Phoenix", 250));
    }

    public Stream<Animal> getAnimals() {
        return ANIMALS.stream();
    }

    public Animal getAnimal(String species) {
        return getAnimals()
                .filter(animal -> species.equals(animal.getSpecies()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No such animal: " + species));
    }

}
