package be.kwakeroni.parameters.petshop.service;

import be.kwakeroni.parameters.petshop.model.Animal;
import be.kwakeroni.parameters.petshop.model.AnimalQuote;

/**
 * Created by kwakeroni on 08/11/17.
 */
public class HardcodedPriceCalculator implements PriceCalculator {

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
