package be.kwakeroni.parameters.petshop.service;

import be.kwakeroni.parameters.petshop.model.ContactInformation;

public class ContactService {

    public ContactService() {
    }

    public ContactInformation getContactInformation() {
        return new ContactInformation("Demo Petshop", "Cuckoo Street 70, Melbourne, Australia", "+01 555 6789");
    }
}
