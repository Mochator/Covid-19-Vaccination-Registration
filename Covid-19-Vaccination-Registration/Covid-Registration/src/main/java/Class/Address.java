/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

/**
 *
 * @author Mocha
 */
public class Address {
    private String No;
    private String Street;
    private String Postcode;
    private String City;
    private String State;
    private String Country;

    public Address(String No, String Street, String Postcode, String City, String State, String Country) {
        this.No = No;
        this.Street = Street;
        this.Postcode = Postcode;
        this.City = City;
        this.State = State;
        this.Country = Country;
    }

    public void setNo(String No) {
        this.No = No;
    }

    public void setStreet(String Street) {
        this.Street = Street;
    }

    public void setPostcode(String Postcode) {
        this.Postcode = Postcode;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public void setState(String State) {
        this.State = State;
    }

    public void setCountry(String Country) {
        this.Country = Country;
    }

    public String getNo() {
        return No;
    }

    public String getStreet() {
        return Street;
    }

    public String getPostcode() {
        return Postcode;
    }

    public String getCity() {
        return City;
    }

    public String getState() {
        return State;
    }

    public String getCountry() {
        return Country;
    }

    @Override
    public String toString() {
        return this.No + ", " + this.Street + ", " + this.City + " " + this.Postcode + ", " + this.State + ", " + this.Country;
    }   
    
}
