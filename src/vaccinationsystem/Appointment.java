package vaccinationsystem;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author Mocha
 */
public class Appointment implements Serializable {

    private String Code;
    protected People Ppl;
    protected Vaccine Vacc;
    private AppointmentStatus Status;
    protected Admin HandledBy;
    protected MyDateTime RegisterDate;
    protected MyDateTime VaccinationDate;
    protected VaccineCentre Location;
    private StockAudit stockAudit; //composition
    private Doctor VaccinatedBy;
    private String RejectReason;
    private String Remarks;

    //Create appointment
    public Appointment(People Ppl) {
        this.Ppl = Ppl;
        this.Status = AppointmentStatus.Pending;
        this.RegisterDate = new MyDateTime();
        
        this.Code = this.GenerateCode();
    }

    

    public String getCode() {
        return Code;
    }

    public AppointmentStatus getStatus() {
        return Status;
    }

    public StockAudit getStockAudit() {
        return stockAudit;
    }

    public Doctor getVaccinatedBy() {
        return VaccinatedBy;
    }

    public String getRejectReason() {
        return RejectReason;
    }

    public String getRemarks() {
        return Remarks;
    }

    public void setVacc(Vaccine Vacc) {
        this.Vacc = Vacc;
    }

    public void setStatus(AppointmentStatus Status) {
        this.Status = Status;
    }

    public void setHandledBy(Admin HandledBy) {
        this.HandledBy = HandledBy;
    }

    public void setRegisterDate(MyDateTime RegisterDate) {
        this.RegisterDate = RegisterDate;
    }

    public void setVaccinationDate(MyDateTime VaccinationDate) {
        this.VaccinationDate = VaccinationDate;
    }

    public void setLocation(VaccineCentre Location) {
        this.Location = Location;
    }

    public void setVaccinatedBy(Doctor VaccinatedBy) {
        this.VaccinatedBy = VaccinatedBy;
    }

    public void setRejectReason(String RejectReason) {
        this.RejectReason = RejectReason;
    }

    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }
    
    private String GenerateCode(){
        
        GenerateId genId = new GenerateId(General.appointmentFileName, General.PrefixAppointment);
        
        return genId.returnId();
        
    }
    
    public void ReadFromFile(){
        
    }

    @Override
    public String toString() {
        return Code + "\t" + Ppl + "\t" + Vacc + "\t" + Status + "\t" + HandledBy + "\t" + RegisterDate + "\t" + VaccinationDate + "\t" + Location + "\t" + stockAudit + "\t" + VaccinatedBy + "\t" + RejectReason + "\t" + Remarks;
    }
    
    enum AppointmentStatus {
        Pending, //once submitted
        Approved, //admin approved
        Accepted, //patient accepted
        Declined, //patient rejected
        Completed, //vaccinated
        Cancelled //admin cancel
    }

}
