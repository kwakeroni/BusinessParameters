package be.kwakeroni.parameters.petshop.service;

import be.kwakeroni.parameters.petshop.model.Animal;
import be.kwakeroni.parameters.petshop.model.AnimalQuote;

/**
 * Created by kwakeroni on 08/11/17.
 */
public interface PriceCalculator {
    public AnimalQuote getQuote(Animal animal, int quantity);
}
