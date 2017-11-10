package be.kwakeroni.parameters.petshop.service;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.petshop.model.Animal;
import be.kwakeroni.parameters.petshop.model.AnimalQuote;
import be.kwakeroni.parameters.types.support.ParameterTypes;

import java.util.Optional;

/**
 * Created by kwakeroni on 09/11/17.
 */
public abstract class AbstractPriceCalculator implements PriceCalculator {

    protected abstract Optional<Integer> getSalesPercentage(String species, int quantity);

    public AnimalQuote getQuote(Animal animal, int quantity) {

        Optional<Integer> percentage = getSalesPercentage(animal.getSpecies(), quantity);

        if (percentage.isPresent()) {
            int unitPrice = getSalesPrice(animal.getUnitPrice(), percentage.get());
            int totalPrice = quantity * unitPrice;
            return new AnimalQuote(animal.getSpecies(), animal.getUnitPrice(), quantity, percentage.get(), unitPrice, totalPrice);
        } else {
            int totalPrice = quantity * animal.getUnitPrice();
            return new AnimalQuote(animal.getSpecies(), animal.getUnitPrice(), quantity, totalPrice);
        }
    }

    protected int getSalesPrice(int price, int percentage) {
        double unitPriceD = price;
        unitPriceD = (((double) (100 - percentage)) * unitPriceD / 100d);
        unitPriceD = Math.ceil(unitPriceD);
        return (int) unitPriceD;
    }


    protected static ParameterGroup<Mapped<String, Ranged<Integer, Simple>>> SALES = new ParameterGroup<Mapped<String, Ranged<Integer, Simple>>>() {
        @Override
        public String getName() {
            return "petshop.sales";
        }
    };

    protected static final Parameter<Integer> PERCENTAGE = new Parameter<Integer>() {
        @Override
        public String getName() {
            return "percentage";
        }

        @Override
        public Integer fromString(String value) {
            return ParameterTypes.INT.fromString(value);
        }

        @Override
        public String toString(Integer value) {
            return ParameterTypes.INT.toString(value);
        }
    };


}
