/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Mocha
 */
public class VaccineCentre implements Serializable {

    private String VacCode;
    private String Name;
    protected Address VacAddress;

    public VaccineCentre(String Name, Address VacAddress) {
        this.Name = Name;
        this.VacAddress = VacAddress;
        this.VacCode = this.GenerateCode();
    }

    public VaccineCentre(String VacCode) {
        FileOperation fo = new FileOperation();
    }

    public String getName() {
        return Name;
    }

    public Address getVacAddress() {
        return VacAddress;
    }

    public String getVacCode() {
        return VacCode;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setVacAddress(Address VacAddress) {
        this.VacAddress = VacAddress;
    }

    public String GetCodeName() {
        return VacCode + " - " + Name;
    }

    public void CreateStockOfAllVaccine() {
        //todo
    }

    @Override
    public String toString() {
        return VacCode + "\t" + Name + "\t" + VacAddress;
    }

    private String GenerateCode() {

        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.vaccineCentreFileName);

        GenerateId genId = new GenerateId(allObj, General.PrefixVaccineCentre);

        return genId.returnId();

    }

}
