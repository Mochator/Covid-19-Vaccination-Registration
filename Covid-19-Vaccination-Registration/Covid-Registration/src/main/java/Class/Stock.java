/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.util.ArrayList;

/**
 *
 * @author Mocha
 */
public class Stock {
    
    private int Id;
    protected Vaccine VacType;
    private int Dose;
    private int Quantity;
    private int PendingQuantity;
    protected VaccineCentre VacCentre;
    
    

    public Stock(Vaccine VacType, int Dose, int Quantity, int PendingQuantity, VaccineCentre VacCentre) {
        this.VacType = VacType;
        this.Dose = Dose;
        this.Quantity = Quantity;
        this.PendingQuantity = PendingQuantity;
        this.VacCentre = VacCentre;
        this.Id = this.GenerateId();
    }

    public int getId() {
        return Id;
    }

    public int getDose() {
        return Dose;
    }

    public int getQuantity() {
        return Quantity;
    }

    public int getPendingQuantity() {
        return PendingQuantity;
    }

    public void setVacType(Vaccine VacType) {
        this.VacType = VacType;
    }

    public void setQuantity(int Quantity) {
        this.Quantity = Quantity;
    }

    public void setPendingQuantity(int PendingQuantity) {
        this.PendingQuantity = PendingQuantity;
    }

    public void setVacCentre(VaccineCentre VacCentre) {
        this.VacCentre = VacCentre;
    }
    
    public int GenerateId(){
        //TODO
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.stockFileName);    
        return Integer.parseInt(FileOperation.GenerateNewId(allObj, ""));
    }

    @Override
    public String toString() {
        return Id + "\t" + VacType + "\t" + Dose + "\t" + Quantity + "\t" + PendingQuantity + "\t" + VacCentre;
    }
    
    
}
