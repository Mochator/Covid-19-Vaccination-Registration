/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Mocha
 */
public class VaccineCentre {
    
    private String VacCode;
    private String Name;
    protected Address VacAddress;

    public VaccineCentre(String VacCode, String Name, Address VacAddress) {
        this.VacCode = VacCode;
        this.Name = Name;
        this.VacAddress = VacAddress;
    }

    public VaccineCentre(String Name, Address VacAddress) {
        this.Name = Name;
        this.VacAddress = VacAddress;
        this.VacCode = this.GenerateCode();
    }
    
    public VaccineCentre(String VacCode){
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

    @Override
    public String toString() {
        return VacCode + "\t" + Name + "\t" + VacAddress;
    }
    
    private String GenerateCode() {
        //TODO
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.vaccineCentreFileName);
        return FileOperation.GenerateNewId(allObj, General.PrefixVaccineCentre);
    }  
   
}
