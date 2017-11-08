package be.kwakeroni.parameters.petshop.rest;

import be.kwakeroni.parameters.petshop.model.Animal;
import be.kwakeroni.parameters.petshop.service.PriceCalculator;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.*;
import java.util.Comparator;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collector;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

/**
 * Created by kwakeroni on 07/11/17.
 */
@Path("/petshop")
@Consumes(APPLICATION_JSON)
@Produces({APPLICATION_JSON, TEXT_PLAIN})
public class PetshopRestService {

    private PriceCalculator priceCalculator;

    public PetshopRestService(PriceCalculator priceCalculator) {
        this.priceCalculator = priceCalculator;
    }

    private static TreeSet<Animal> ANIMALS = new TreeSet<>(Comparator.comparing(Animal::getSpecies));

    {
        ANIMALS.add(new Animal("Cat", 80));
        ANIMALS.add(new Animal("Dog", 70));
        ANIMALS.add(new Animal("Goldfish", 10));
        ANIMALS.add(new Animal("Gerbil", 35));
        ANIMALS.add(new Animal("Phoenix", 250));
    }

    @Path("/animals")
    @GET
    @Produces({APPLICATION_JSON})
    public String getAnimals() {
        return ANIMALS.stream()
                .map(Animal::toJson)
                .collect(toJsonArray())
                .toString();
    }

    @Path("/animals/{species}/price")
    @GET
    @Produces({APPLICATION_JSON})
    public String getPrice(@PathParam("species") String species, @QueryParam("quantity") Integer quantity) {
        Objects.requireNonNull(species, "species is mandatory");
        int qty = (quantity == null) ? 1 : quantity;
        if (qty < 0) {
            throw new IllegalArgumentException("quantity must be bigger than zero");
        }
        Animal animal = ANIMALS.stream()
                .filter(a -> species.equalsIgnoreCase(a.getSpecies()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("No such animal: " + species));

        return priceCalculator.getQuote(animal, qty).toJson().toString();
    }


    private Collector<JSONObject, ?, JSONArray> toJsonArray() {
        return Collector.of(JSONArray::new, JSONArray::put, this::combine);
    }

    private JSONArray combine(JSONArray array1, JSONArray array2) {
        array2.forEach(array1::put);
        return array1;
    }

}
