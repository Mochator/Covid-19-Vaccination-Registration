/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.io.File;
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

    public VaccineCentre(String VacCode, String Name) {
        this.VacCode = VacCode;
        this.Name = Name;
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
    
    public void GenerateCode() {
        this.VacCode = FileOperation.GenerateRecordId(General.vaccineCentreFileName, General.PrefixVac);
    }  
    
}
