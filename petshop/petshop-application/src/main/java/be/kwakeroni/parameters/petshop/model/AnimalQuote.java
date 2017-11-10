package be.kwakeroni.parameters.petshop.model;

import org.json.JSONObject;

/**
 * Created by kwakeroni on 07/11/17.
 */
public class AnimalQuote {
    private String species;
    private int unitPrice;
    private int quantity;
    private int totalPrice;
    private Integer salePercentage;
    private Integer salePrice;

    public AnimalQuote(String species, int unitPrice, int quantity, int totalPrice) {
        this.species = species;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.salePercentage = null;
        this.salePrice = null;
    }


    public AnimalQuote(String species, int unitPrice, int quantity, int salePercentage, int salePrice, int totalPrice) {
        this.species = species;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.salePercentage = salePercentage;
        this.salePrice = salePrice;
    }


    public String getSpecies() {
        return species;
    }

    public int getUnitPrice() {
        return this.unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public int gettotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return "AnimalQuote{" +
                "species='" + species + '\'' +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                '}';
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject()
                .put("species", this.species)
                .put("unitPrice", this.unitPrice)
                .put("quantity", this.quantity)
                .put("totalPrice", this.totalPrice);

        if (this.salePercentage != null && this.salePrice != null) {
            json.put("salePercentage", this.salePercentage.intValue())
                    .put("salePrice", this.salePrice.intValue());
        }

        return json;
    }

}
