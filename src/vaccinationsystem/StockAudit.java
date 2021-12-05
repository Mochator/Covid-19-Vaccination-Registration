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

    public int getId();

}

//Adjust actual stock
class ActualStock implements StockAudit, Serializable {

    private int Id;
    private Stock VacStock;
    private int Quantity;
    private MyDateTime CreateDate;
    private User CreatedBy;
    private String Remarks;

    //For stockist
    public ActualStock(Stock VacStock, int Quantity, User CreatedBy, String Remarks) {
        this.VacStock = VacStock;
        this.Quantity = Quantity;
        this.CreatedBy = CreatedBy;
        this.Remarks = Remarks;

        this.Id = this.GenerateId();
        this.CreateDate = StockAudit.CreateDate;
    }


    public ActualStock(int Id, Stock VacStock, int Quantity, MyDateTime CreateDate, User CreatedBy, String Remarks) {
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


    public int GenerateId() {
        
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.stockAuditFileName);

        GenerateId genId = new GenerateId(allObj, "");

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
    protected User CreatedBy;
    private static String Remarks;
    private MyDateTime CreateDate;


    public PendingStock(Stock VacStock, int Quantity, User CreatedBy, String Remarks) {
        this.VacStock = VacStock;
        this.Quantity = Quantity;
        this.CreatedBy = CreatedBy;
        this.Remarks = Remarks;

        this.Id = this.GenerateId();
        this.CreateDate = StockAudit.CreateDate;
    }

    public int getId() {
        return Id;
    }

    public int GenerateId() {
        
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.pendingStockAuditFileName);

        GenerateId genId = new GenerateId(allObj, "");

        return Integer.parseInt(genId.returnId());

    }

    @Override
    public String toString() {
        return Id + "\t" + VacStock + "\t" + Quantity + "\t" + CreateDate + "\t" + CreatedBy;
    }
}
