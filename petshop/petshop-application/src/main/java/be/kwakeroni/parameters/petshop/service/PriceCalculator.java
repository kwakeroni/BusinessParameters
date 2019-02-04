package be.kwakeroni.parameters.petshop.service;

import be.kwakeroni.parameters.basic.client.model.Historicized;
import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.petshop.definitions.BulkDiscount;
import be.kwakeroni.parameters.petshop.definitions.SalesDiscount;
import be.kwakeroni.parameters.petshop.model.Animal;
import be.kwakeroni.parameters.petshop.model.AnimalQuote;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Created by kwakeroni on 08/11/17.
 */
public class PriceCalculator {

    private final Mapped<String, Ranged<Integer, Simple>> bulkDiscount;
    private final Mapped<String, Historicized<Simple>> salesDiscount;

    public PriceCalculator(BusinessParameters parameters) {
        this.bulkDiscount = BulkDiscount.DEFINITION.createGroup(parameters);
        this.salesDiscount = SalesDiscount.DEFINITION.createGroup(parameters);
    }

    private Optional<Integer> getDiscountPercentage(String animal, int quantity) {
        Optional<Integer> bulk = bulkDiscount.forKey(animal).at(quantity).getValue(BulkDiscount.DISCOUNT);
        Optional<Integer> sales = salesDiscount.forKey(animal).at(LocalDate.now()).getValue(SalesDiscount.DISCOUNT);

        int totalDiscount = bulk.orElse(0) + sales.orElse(0);
        return (totalDiscount > 0) ? Optional.of(totalDiscount) : Optional.empty();
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
