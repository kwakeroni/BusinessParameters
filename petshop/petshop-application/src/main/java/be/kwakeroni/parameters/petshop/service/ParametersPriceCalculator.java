package be.kwakeroni.parameters.petshop.service;

import be.kwakeroni.parameters.basic.client.model.Mapped;
import be.kwakeroni.parameters.basic.client.model.Ranged;
import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.client.query.MappedQuery;
import be.kwakeroni.parameters.basic.client.query.RangedQuery;
import be.kwakeroni.parameters.basic.client.query.ValueQuery;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.client.api.model.ParameterGroup;
import be.kwakeroni.parameters.client.api.query.Query;
import be.kwakeroni.parameters.petshop.model.Animal;
import be.kwakeroni.parameters.petshop.model.AnimalQuote;
import be.kwakeroni.parameters.types.support.ParameterTypes;

import java.util.Optional;

/**
 * Created by kwakeroni on 07/11/17.
 */
public class ParametersPriceCalculator implements PriceCalculator {

    private BusinessParameters businessParameters;

    public ParametersPriceCalculator(BusinessParameters businessParameters) {
        this.businessParameters = businessParameters;
    }

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

    private Optional<Integer> getSalesPercentage(String species, int quantity) {
        Optional<Integer> pct = getParameter(species, quantity);
        if (!pct.isPresent()) {
            pct = getParameter("any", quantity);
        }
        return pct;
    }

    private Optional<Integer> getParameter(String species, int quantity) {
        return businessParameters.get(SALES, getQuery(species, quantity));
    }

    private int getSalesPrice(int price, int percentage) {
        double unitPriceD = price;
        unitPriceD = (((double) (100 - percentage)) * unitPriceD / 100d);
        unitPriceD = Math.ceil(unitPriceD);
        return (int) unitPriceD;
    }

    private Query<Mapped<String, Ranged<Integer, Simple>>, Integer> getQuery(String species, int quantity) {
        return new MappedQuery<>(species, ParameterTypes.STRING,
                new RangedQuery<>(quantity, ParameterTypes.INT,
                        new ValueQuery<>(PERCENTAGE)
                )
        );
    }

    private static ParameterGroup<Mapped<String, Ranged<Integer, Simple>>> SALES = new ParameterGroup<Mapped<String, Ranged<Integer, Simple>>>() {
        @Override
        public String getName() {
            return "petshop.sales";
        }
    };

    private static final Parameter<Integer> PERCENTAGE = new Parameter<Integer>() {
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
