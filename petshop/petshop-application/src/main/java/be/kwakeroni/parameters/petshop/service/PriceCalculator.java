package be.kwakeroni.parameters.petshop.service;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.petshop.definitions.BulkDiscount;
import be.kwakeroni.parameters.petshop.model.Animal;
import be.kwakeroni.parameters.petshop.model.AnimalQuote;

import java.util.Optional;

/**
 * Created by kwakeroni on 08/11/17.
 */
public class PriceCalculator {

    private final Mapped<String, Ranged<Integer, Simple>> bulkDiscount;

    public PriceCalculator(BusinessParameters parameters) {
        this.bulkDiscount = BulkDiscount.DEFINITION.createGroup(parameters);
    }

    private Optional<Integer> getDiscountPercentage(String animal, int quantity) {
        return bulkDiscount.forKey(animal).at(quantity).getValue(BulkDiscount.DISCOUNT);
    }

    public AnimalQuote getQuote(Animal animal, int quantity) {
        return getDiscountPercentage(animal.getSpecies(), quantity)
                .map(percentage -> getSalesQuote(animal, quantity, percentage))
                .orElseGet(() -> getStandardQuote(animal, quantity));
    }

    private AnimalQuote getStandardQuote(Animal animal, int quantity) {
        int totalPrice = quantity * animal.getUnitPrice();
        return new AnimalQuote(animal.getSpecies(), animal.getUnitPrice(), quantity, totalPrice);
    }

    private AnimalQuote getSalesQuote(Animal animal, int quantity, int percentage) {
        int unitPrice = getSalesUnitPrice(animal, percentage);
        int totalPrice = quantity * unitPrice;
        return new AnimalQuote(animal.getSpecies(), animal.getUnitPrice(), quantity, percentage, unitPrice, totalPrice);
    }

    private int getSalesUnitPrice(Animal animal, int percentage) {
        double unitPriceD = animal.getUnitPrice();
        unitPriceD = (((double) (100 - percentage)) * unitPriceD / 100d);
        unitPriceD = Math.ceil(unitPriceD);
        return (int) unitPriceD;
    }
}
