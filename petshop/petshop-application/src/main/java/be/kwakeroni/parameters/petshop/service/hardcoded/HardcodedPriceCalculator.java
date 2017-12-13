package be.kwakeroni.parameters.petshop.service.hardcoded;

import be.kwakeroni.parameters.petshop.model.Animal;
import be.kwakeroni.parameters.petshop.model.AnimalQuote;
import be.kwakeroni.parameters.petshop.service.AbstractPriceCalculator;

import java.util.Optional;

/**
 * Created by kwakeroni on 08/11/17.
 */
public class HardcodedPriceCalculator extends AbstractPriceCalculator {

    @Override
    protected Optional<Integer> getSalesPercentage(String species, int quantity) {
        return (quantity > 3) ? Optional.of(10) : Optional.empty();
    }

    public AnimalQuote getQuote(Animal animal, int quantity) {

        int unitPrice;
        if (quantity <= 3) {
            unitPrice = animal.getUnitPrice();
            int totalPrice = quantity * unitPrice;
            return new AnimalQuote(animal.getSpecies(), animal.getUnitPrice(), quantity, totalPrice);

        } else {
            int percentage = 10;
            double unitPriceD = animal.getUnitPrice();
            unitPriceD = (((double) (100 - percentage)) * unitPriceD / 100d);
            unitPriceD = Math.ceil(unitPriceD);

            unitPrice = (int) unitPriceD;
            int totalPrice = quantity * unitPrice;
            return new AnimalQuote(animal.getSpecies(), animal.getUnitPrice(), quantity, percentage, unitPrice, totalPrice);

        }


    }

}
