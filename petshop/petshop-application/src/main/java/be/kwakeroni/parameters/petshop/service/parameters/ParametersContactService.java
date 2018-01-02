package be.kwakeroni.parameters.petshop.service.parameters;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.client.api.BusinessParameters;
import be.kwakeroni.parameters.client.api.model.Entry;
import be.kwakeroni.parameters.petshop.definitions.ContactDetails;
import be.kwakeroni.parameters.petshop.model.ContactInformation;
import be.kwakeroni.parameters.petshop.service.ContactService;

public class ParametersContactService implements ContactService {

    private final Simple contactDetails;

    public ParametersContactService(BusinessParameters businessParameters) {
        this.contactDetails = ContactDetails.DEFINITION.createGroup(businessParameters);
    }

    @Override
    public ContactInformation getContactInformation() {
        Entry entry = this.contactDetails.getEntry().get();

        String companyName = entry.getValue(ContactDetails.COMPANY_NAME);
        String address = entry.getValue(ContactDetails.ADDRESS);
        String phone = entry.getValue(ContactDetails.PHONE);

        return new ContactInformation(companyName, address, phone);
    }
}
