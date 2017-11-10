package be.kwakeroni.parameters.petshop.model;

import org.json.JSONObject;

/**
 * Created by kwakeroni on 07/11/17.
 */
public class Animal {

    private String species;
    private int unitPrice;

    public Animal(String species, int unitPrice) {
        this.species = species;
        this.unitPrice = unitPrice;
    }

    public String getSpecies() {
        return species;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "species='" + species + '\'' +
                ", unitPrice=" + unitPrice +
                '}';
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("species", this.species)
                .put("unitPrice", this.unitPrice);
    }


}
