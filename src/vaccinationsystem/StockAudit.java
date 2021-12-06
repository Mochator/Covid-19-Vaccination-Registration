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

    public ActualStock(Stock VacStock, int Quantity, User CreatedBy, String Remarks) {
        this.VacStock = VacStock;
        this.Quantity = Quantity;
        this.CreatedBy = CreatedBy;
        this.Remarks = Remarks;

        this.Id = this.GenerateId();
        this.CreateDate = StockAudit.CreateDate;
    }
    public String getRemarks() {
        return Remarks;
    }
    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }
    public Stock getVacStock() {
        return VacStock;
    }
    public int getQuantity() {
        return Quantity;
    }
    public MyDateTime getCreateDate() {
        return CreateDate;
    }
    public User getCreatedBy() {
        return CreatedBy;
    }
    public int GenerateId() {
        
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.stockAuditFileName);

        GenerateId genId = new GenerateId(allObj);

        return Integer.parseInt(genId.returnId());
    }
    public int getId() {
        return Id;
    }
    public String toString() {
        return Id + "\t" + VacStock + "\t" + Quantity + "\t" + CreateDate + "\t" + CreatedBy + "\t";
    }
}

//Adjust pending stock
class PendingStock implements StockAudit, Serializable {
    private int Id;
    private Stock VacStock;
    private int Quantity;
    private User CreatedBy;
    private String Remarks;
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

        GenerateId genId = new GenerateId(allObj);

        return Integer.parseInt(genId.returnId());

    }
    public Stock getVacStock() {
        return VacStock;
    }
    public int getQuantity() {
        return Quantity;
    }
    public User getCreatedBy() {
        return CreatedBy;
    }
    public String getRemarks() {
        return Remarks;
    }
    public MyDateTime getCreateDate() {
        return CreateDate;
    }
    public String toString() {
        return Id + "\t" + VacStock + "\t" + Quantity + "\t" + CreateDate + "\t" + CreatedBy;
    }
}
