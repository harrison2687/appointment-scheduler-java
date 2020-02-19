package SchedulingApp.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Customer {

    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final IntegerProperty active;
    private final IntegerProperty addressId;
    private final StringProperty address;
    private final StringProperty address2;
    private final StringProperty postalCode;
    private final StringProperty phone;
    private final IntegerProperty cityId;
    private final StringProperty city;
    private final IntegerProperty countryId;
    private final StringProperty country;

    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();

    /**
     * main constructor for customer
     */
    public Customer() {
        customerId = new SimpleIntegerProperty();
        customerName = new SimpleStringProperty();
        active = new SimpleIntegerProperty();
        addressId = new SimpleIntegerProperty();
        address = new SimpleStringProperty();
        address2 = new SimpleStringProperty();
        postalCode = new SimpleStringProperty();
        phone = new SimpleStringProperty();
        cityId = new SimpleIntegerProperty();
        city = new SimpleStringProperty();
        countryId = new SimpleIntegerProperty();
        country = new SimpleStringProperty();
    }

    /**
     * ObservableList for customer array
     */
    public static ObservableList<Customer> getCustomerList() {
        return customerList;
    }

    /**
     * getters and setters
     */
    public int getCustomerId() {
        return customerId.get();
    }

    public IntegerProperty customerIdProperty() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId.set(customerId);
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public StringProperty customerNameProperty() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }

    public int getActive() {
        return active.get();
    }

    public IntegerProperty activeProperty() {
        return active;
    }

    public void setActive(int active) {
        this.active.set(active);
    }

    public int getAddressId() {
        return addressId.get();
    }

    public IntegerProperty addressIdProperty() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId.set(addressId);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getAddress2() {
        return address2.get();
    }

    public StringProperty address2Property() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2.set(address2);
    }

    public String getPostalCode() {
        return postalCode.get();
    }

    public StringProperty postalCodeProperty() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public int getCityId() {
        return cityId.get();
    }

    public IntegerProperty cityIdProperty() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId.set(cityId);
    }

    public String getCity() {
        return city.get();
    }

    public StringProperty cityProperty() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public int getCountryId() {
        return countryId.get();
    }

    public IntegerProperty countryIdProperty() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId.set(countryId);
    }

    public String getCountry() {
        return country.get();
    }

    public StringProperty countryProperty() {
        return country;
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    /**
     * Validation checks for customer operations
     */
    public static String customerValid(String customerName, String address, String city, String country,
                                       String postalCode, String phone) {
        String errorMessage = "";
        if (customerName.length() == 0) {
            errorMessage = "Customer name is required for this record.";
        }
        if (address.length() == 0) {
            errorMessage = "Please provide a Street Address.";
        }
        if (city.length() == 0) {
            errorMessage = "Please provide a City.";
        }
        if (country.length() == 0) {
            errorMessage = "Please provide a Country.";
        }
        if (postalCode.length() == 0) {
            errorMessage = "Please provide a Postal Code.";
        }
        if (phone.length() == 0) {
            errorMessage = "Please provide a valid Phone Number";
        }
        return errorMessage;
    }
}
