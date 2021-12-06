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
public class Stock implements Serializable {

    private int Id;
    protected Vaccine VacType;
    private int Dose;
    private int Quantity = 0;
    private int PendingQuantity = 0;
    protected VaccineCentre VacCentre;

    //Create new stock
    public Stock(Vaccine VacType, int Dose, VaccineCentre VacCentre) {
        this.VacType = VacType;
        this.Dose = Dose;
        this.VacCentre = VacCentre;
    }

 

    public boolean FindStock() {

        ArrayList<Object> allStocks = FileOperation.DeserializeObject(General.stockFileName);
        ListIterator li = allStocks.listIterator();

        boolean found = false;

        while (li.hasNext() && !found) {
            Stock s = (Stock) li.next();
            if (s.getDose() == this.Dose && s.VacCentre.getVacCode().equals(this.VacCentre.getVacCode()) && s.VacType.getVacCode().equals(this.VacType.getVacCode())) {
                this.Id = s.Id;
                this.setQuantity(s.getQuantity());
                this.setPendingQuantity(s.getPendingQuantity());
                found = true;
            }
        }

        return found;
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

    private void setQuantity(int Quantity) {
        this.Quantity = Quantity;
    }

    private void setPendingQuantity(int PendingQuantity) {
        this.PendingQuantity = PendingQuantity;
    }

    public void GenerateId() {
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.stockFileName);

        GenerateId genId = new GenerateId(allObj);

        this.Id = Integer.parseInt(genId.returnId());
    }

    public boolean AddQty(int i, User currentUser, String auditRemarks) {
        this.Quantity = this.Quantity + i;
        boolean success = true;
        StockAudit sa = new ActualStock(this, i, currentUser, auditRemarks);

        if (!FileOperation.SerializeObject(General.stockAuditFileName, sa)) {
            success = false;
        }

        return success;
    }

    public boolean MinusQty(int i, User currentUser, String auditRemarks) {
        int newBal = this.Quantity - i;
        boolean success = true;

        if (newBal >= 0) {
            this.setQuantity(newBal);
            StockAudit sa = new ActualStock(this, 0 - i, currentUser, auditRemarks);

            if (!FileOperation.SerializeObject(General.stockAuditFileName, sa)) {
                success = false;
            }
        } else {
            success = false;
        }

        return success;
    }

    public boolean AddPendingQty(int i, User currentUser, String auditRemarks) {
        this.PendingQuantity = this.PendingQuantity + i;
        boolean success = true;

        StockAudit sa = new PendingStock(this, i, currentUser, auditRemarks);

        success = FileOperation.SerializeObject(General.pendingStockAuditFileName, sa);

        return success;
    }

    public boolean MinusPendingQty(int i, User currentUser, String auditRemarks) {
        int newBal = this.PendingQuantity - i;
        boolean success = true;

        if (newBal >= 0) {
            this.setQuantity(newBal);
            StockAudit sa = new PendingStock(this, 0 - i, currentUser, auditRemarks);

            if (!FileOperation.SerializeObject(General.pendingStockAuditFileName, sa)) {
                System.out.println("Serialize prob");
                success = false;
            }
        } else {
            System.out.println("Qty short");
            success = false;
        }

        return success;
    }

    @Override
    public String toString() {
        return Id + "\t" + VacType.getVacCode() + "\t" + Dose + "\t" + Quantity + "\t" + PendingQuantity + "\t" + VacCentre.getVacCode();
    }

}
