/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Mocha
 */
public interface StockAudit {

    MyDateTime CreateDate = new MyDateTime();

    public int GenerateId();

    public void AddQuantity();

    public void MinusQuantity();

    public int getId();

}

//Adjust actual stock
class ActualStock implements StockAudit, Serializable {

    private int Id;
    private Stock VacStock;
    private int Quantity;
    private MyDateTime CreateDate;
    private Personnel CreatedBy;
    private String Remarks;

    //For stockist
    public ActualStock(Stock VacStock, int Quantity, Stockist CreatedBy, String Remarks) {
        this.VacStock = VacStock;
        this.Quantity = Quantity;
        this.CreatedBy = CreatedBy;
        this.Remarks = Remarks;

        this.Id = this.GenerateId();
        this.CreateDate = StockAudit.CreateDate;
    }
    

    //For doctor after vaccination
    public ActualStock(Stock VacStock, int Quantity, Doctor CreatedBy, Appointment Appointment) {
        this.VacStock = VacStock;
        this.Quantity = Quantity;
        this.CreatedBy = CreatedBy;
        this.Remarks = "Vaccinatation - " + Appointment.getCode();

        this.Id = this.GenerateId();
        this.CreateDate = StockAudit.CreateDate;
    }

    public ActualStock(int Id, Stock VacStock, int Quantity, MyDateTime CreateDate, Personnel CreatedBy, String Remarks) {
        this.Id = Id;
        this.VacStock = VacStock;
        this.Quantity = Quantity;
        this.CreateDate = CreateDate;
        this.CreatedBy = CreatedBy;
        this.Remarks = Remarks;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }

    public void AddQuantity() {
        ///todo

    }

    public void MinusQuantity() {
        //todo
    }

    public int GenerateId() {
        GenerateId genId = new GenerateId(General.stockAuditFileName);

        return Integer.parseInt(genId.returnId());
    }

    public int getId() {
        return Id;
    }

    @Override
    public String toString() {
        return Id + "\t" + VacStock + "\t" + Quantity + "\t" + CreateDate + "\t" + CreatedBy + "\t";
    }
}

//Adjust pending stock
class PendingStock implements StockAudit, Serializable {

    private int Id;
    private Stock VacStock;
    private int Quantity;
    protected Personnel CreatedBy;
    private static String Remarks = "Vaccinatation - ";
    private MyDateTime CreateDate;

    //For admin after assign vaccine
    public PendingStock(Stock VacStock, int Quantity, Admin CreatedBy, Appointment Appointment) {
        this.VacStock = VacStock;
        this.Quantity = Quantity;
        this.CreatedBy = CreatedBy;
        this.Remarks += Appointment.getCode();

        this.Id = this.GenerateId();
        this.CreateDate = StockAudit.CreateDate;
    }

    //For doctor after vaccination
    public PendingStock(Stock VacStock, int Quantity, Doctor CreatedBy, Appointment Appointment) {
        this.VacStock = VacStock;
        this.Quantity = Quantity;
        this.CreatedBy = CreatedBy;
        this.Remarks = Appointment.getCode();

        this.Id = this.GenerateId();
        this.CreateDate = StockAudit.CreateDate;
    }

    @Override
    public void AddQuantity() {
        int newQty = this.VacStock.getQuantity() + this.Quantity;
        this.VacStock.setQuantity(newQty);
    }

    @Override
    public void MinusQuantity() {

    }

    public int getId() {
        return Id;
    }

    public int GenerateId() {

        GenerateId genId = new GenerateId(General.pendingStockAuditFileName);

        return Integer.parseInt(genId.returnId());

    }

    @Override
    public String toString() {
        return Id + "\t" + VacStock + "\t" + Quantity + "\t" + CreateDate + "\t" + CreatedBy;
    }
}
