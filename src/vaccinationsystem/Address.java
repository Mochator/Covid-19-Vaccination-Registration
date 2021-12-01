/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import java.io.Serializable;

/**
 *
 * @author Mocha
 */
public class Address implements Serializable {
    private String No;
    private String Street;
    private String Postcode;
    private String City;
    private String State;

    public Address(String No, String Street, String Postcode, String City, String State) {
        this.No = No;
        this.Street = Street;
        this.Postcode = Postcode;
        this.City = City;
        this.State = State;
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

    @Override
    public String toString() {
        return this.No + "\t" + this.Street + "\t" + this.City + "\t" + this.Postcode + "\t" + this.State;
    }   
    
    public String getFullAddress(){
        return this.No + ", " + this.Street + ", " + this.City + " " + this.Postcode + ", " + this.State;
    }
    
}
