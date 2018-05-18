package be.kwakeroni.parameters.petshop.service;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.petshop.definitions.AnimalPrice;
import be.kwakeroni.parameters.petshop.model.Animal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Stream;

public class AnimalCatalog {

    private static Collection<String> ANIMALS = new TreeSet<>(Arrays.asList("Cat", "Dog", "Goldfish", "Gerbil", "Phoenix"));

    private final Mapped<String, Simple> prices;

    public AnimalCatalog(BusinessParameters businessParameters) {
        this.prices = AnimalPrice.DEFINITION.createGroup(businessParameters);
    }

    private Optional<Animal> getOptionalAnimal(String species) {
        return prices.forKey(species)
                .getValue(AnimalPrice.PRICE)
                .map(price -> new Animal(species, price));
    }

    public Stream<Animal> getAnimals() {
        return ANIMALS.stream()
                .map(this::getOptionalAnimal)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Animal getAnimal(String species) {
        return getOptionalAnimal(species)
                .orElseThrow(() -> new IllegalArgumentException("No such animal: " + species));
    }
}
