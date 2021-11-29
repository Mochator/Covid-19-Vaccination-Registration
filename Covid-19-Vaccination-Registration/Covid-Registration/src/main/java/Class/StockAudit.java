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
public class StockAudit {
    private int Id;
    protected Stock VacStock;
    private int Quatity;
    protected MyDateTime CreateDate;
    protected Stockist CreatedBy;
    private String Remarks;

    //Add stock or adjust stock
    public StockAudit(Stock VacStock, int Quatity, MyDateTime CreateDate, Stockist CreatedBy, String Remarks) {
        this.VacStock = VacStock;
        this.Quatity = Quatity;
        this.CreateDate = CreateDate;
        this.CreatedBy = CreatedBy;
        this.Remarks = Remarks;
        this.Id = this.GenerateId();
    }
    
    
    //Vaccinated deduct stock
    public StockAudit(Stock VacStock, int Quatity, MyDateTime CreateDate) {
        this.VacStock = VacStock;
        this.Quatity = Quatity;
        this.CreateDate = CreateDate;
    }

    public int getId() {
        return Id;
    }

    public int getQuatity() {
        return Quatity;
    }

    public String getRemarks() {
        return Remarks;
    }
    
    private int GenerateId(){
        //todo
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.stockAuditFileName);       
        return Integer.parseInt(FileOperation.GenerateNewId(allObj, ""));
    }

    @Override
    public String toString() {
        return Id + "\t" + VacStock + "\t" + Quatity + "\t" + CreateDate + "\t" + CreatedBy + "\t" + Remarks;
    }  
    
}
