package vaccinationsystem;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.ListIterator;

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
    
    public int CheckDoseFromAppointment() {
        boolean result = true;
        int dose = 1;

        ArrayList<Object> allAppointments = FileOperation.DeserializeObject(General.appointmentFileName);

        ListIterator li = allAppointments.listIterator();

        while (li.hasNext() && result) {
            Appointment row = (Appointment) li.next();

            if (!row.Ppl.Username.equals(this.Ppl.Username)) {
                continue;
            }

            if (!row.Vacc.equals(this.Vacc)) {
                continue;
            }

            if (!row.getStatus().equals(AppointmentStatus.Completed)) {
                continue;
            }

            dose++;

        }

        return dose;
    }

    private String GenerateCode() {

        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.appointmentFileName);

        GenerateId genId = new GenerateId(allObj, General.PrefixAppointment);

        return genId.returnId();

    }

    @Override
    public String toString() {
        return Code + "\t" + Ppl + "\t" + Vacc + "\t" + Status + "\t" + HandledBy + "\t" + RegisterDate + "\t" + VaccinationDate + "\t" + Location + "\t" + stockAudit + "\t" + VaccinatedBy + "\t" + RejectReason + "\t" + Remarks;
    }

}

enum AppointmentStatus {
    Pending, //once submitted
    Approved, //admin approved
    Accepted, //patient accepted
    Declined, //patient rejected
    Rescheduled, //reschedule the app
    Completed, //vaccinated
    Cancelled //admin cancel
}
