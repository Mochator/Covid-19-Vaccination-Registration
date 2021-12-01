/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import java.util.ArrayList;

/**
 *
 * @author Mocha
 */
public class Stock {
    
    private int Id;
    protected Vaccine VacType;
    private int Dose;
    private static int Quantity = 0;
    private static int PendingQuantity = 0;
    protected VaccineCentre VacCentre;
    
    //Create new stock
    public Stock(Vaccine VacType, int Dose, VaccineCentre VacCentre) {
        this.VacType = VacType;
        this.Dose = Dose;
        this.VacCentre = VacCentre;
        this.Id = this.GenerateId();
    }
    
    public Stock(int Id, Vaccine VacType, int Quantity, int PendingQuantity, int Dose, VaccineCentre VacCentre) {
        this.VacType = VacType;
        this.Quantity = Quantity;
        this.PendingQuantity = PendingQuantity;
        this.Dose = Dose;
        this.VacCentre = VacCentre;
        this.Id = Id;
    }
    
    public Stock(int Id){
        //TODO:: Read from file then input the object
        
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
    
    private int GenerateId(){
        GenerateId genId = new GenerateId(General.stockFileName);
        
        return Integer.parseInt(genId.returnId());
    }

    @Override
    public String toString() {
        return Id + "\t" + VacType.getVacCode() + "\t" + Dose + "\t" + Quantity + "\t" + PendingQuantity + "\t" + VacCentre.getVacCode();
    }
    
    
}
