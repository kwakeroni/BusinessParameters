package be.kwakeroni.parameters.petshop.model;

import org.json.JSONObject;

public class ContactInformation {

    private final String companyName;
    private final String address;
    private final String phone;

    public ContactInformation(String companyName, String address, String phone) {
        this.companyName = companyName;
        this.address = address;
        this.phone = phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("company", this.companyName)
                .put("address", this.address)
                .put("phone", this.phone);
    }


}
