/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author Mocha
 */
public class Vaccine implements Serializable {

    private String VacCode;
    private String Name;
    private int DoseCount;
    private int Interval;

    //New Vaccine
    public Vaccine(String Name, int DoseCount, int Interval) {
        this.Name = Name;
        this.DoseCount = DoseCount;
        this.Interval = Interval;
        this.VacCode = this.GenerateCode();
    }

    public Vaccine(String VacCode, String Name, int DoseCount, int Interval) {
        this.VacCode = VacCode;
        this.Name = Name;
        this.DoseCount = DoseCount;
        this.Interval = Interval;
    }

    public String getVacCode() {
        return VacCode;
    }

    public String getName() {
        return Name;
    }

    public int getDoseCount() {
        return DoseCount;
    }

    public int getInterval() {
        return Interval;
    }

    public String GetCodeName() {
        return VacCode + " - " + Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setDoseCount(int DoseCount) {
        this.DoseCount = DoseCount;
    }

    public void setInterval(int Interval) {
        this.Interval = Interval;
    }

    

    @Override
    public String toString() {
        return VacCode + "\t" + Name + "\t" + DoseCount + "\t" + Interval;
    }

    private String GenerateCode() {

        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.vaccineFileName);

        GenerateId genId = new GenerateId(allObj, General.PrefixVaccine);

        return genId.returnId();
    }

}
