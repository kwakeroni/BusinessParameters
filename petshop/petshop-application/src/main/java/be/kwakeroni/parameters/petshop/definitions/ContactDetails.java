package be.kwakeroni.parameters.petshop.definitions;

import be.kwakeroni.parameters.basic.client.model.Simple;
import be.kwakeroni.parameters.basic.definition.BasicGroup;
import be.kwakeroni.parameters.client.api.model.Parameter;
import be.kwakeroni.parameters.core.support.client.ParameterSupport;
import be.kwakeroni.parameters.definition.api.ParameterGroupDefinition;

public class ContactDetails {

    public static final Parameter<String> COMPANY_NAME = ParameterSupport.ofString("companyName");
    public static final Parameter<String> ADDRESS = ParameterSupport.ofString("address");
    public static final Parameter<String> PHONE = ParameterSupport.ofString("phone");

    public static final ParameterGroupDefinition<Simple> DEFINITION =
            BasicGroup.group()
                    .withParameter("companyName")
                    .withParameter("address")
                    .withParameter("phone")
                    .build("petshop.contact");
}
