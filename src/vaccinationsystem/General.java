package vaccinationsystem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;
import vaccinationsystem.Appointment.AppointmentStatus;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mocha
 */
public class General {

    public static String pathTxtFiles = "";

    //Text file names
    public static final String userFileName = pathTxtFiles + "User.ser";
    public static final String appointmentFileName = pathTxtFiles + "Apppointment.ser";
    public static final String vaccineCentreFileName = pathTxtFiles + "VaccineCentre.ser";
    public static final String vaccineFileName = pathTxtFiles + "Vaccine.ser";
    public static final String stockFileName = pathTxtFiles + "Stock.ser";
    public static final String stockAuditFileName = pathTxtFiles + "StockAudit.ser";
    public static final String pendingStockAuditFileName = pathTxtFiles + "PendingStockAudit.ser";

    //Prefix
    public static final String PrefixPeople = "PP";
    public static final String PrefixPersonnel = "PSN";
    public static final String PrefixVaccineCentre = "VC";
    public static final String PrefixVaccine = "VAC";
    public static final String PrefixAppointment = "APP";

    //UserRole
    public static final String UserRolePeople = "People";
    public static final String UserRolePersonnel = "Personnel";

    //PersonnelRole 
    public static final String PersonnelRoleDoctor = "Doctor";
    public static final String PersonnelRoleAdmin = "Admin";
    public static final String PersonnelRoleStockist = "Stockist";
    
    public static ArrayList<String> PersonnelRoles() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(PersonnelRoleDoctor);
        result.add(PersonnelRoleAdmin);
        result.add(PersonnelRoleStockist);

        Collections.sort(result);
        return result;
    }

    //Malaysia States
    public static final String MalaysiaStateJohor = "Johor";
    public static final String MalaysiaStatePenang = "Penang";
    public static final String MalaysiaStateKualaLumpur = "Kuala Lumpur";
    public static final String MalaysiaStateMalacca = "Malacca";
    public static final String MalaysiaStateSarawak = "Sarawak";
    public static final String MalaysiaStateSabah = "Sabah";
    public static final String MalaysiaStateTerrenganu = "Terrenganu";
    public static final String MalaysiaStateNegeriSembilan = "Negeri Sembilan";
    public static final String MalaysiaStateKelantan = "Kelantan";
    public static final String MalaysiaStateKedah = "Kedah";
    public static final String MalaysiaStatePerak = "Perak";
    public static final String MalaysiaStatePahang = "Pahang";
    public static final String MalaysiaStatePerlis = "Perlis";

    public static ArrayList<String> MalaysiaStates() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(MalaysiaStatePerak);
        result.add(MalaysiaStatePahang);
        result.add(MalaysiaStateKedah);
        result.add(MalaysiaStateKelantan);
        result.add(MalaysiaStateNegeriSembilan);
        result.add(MalaysiaStateTerrenganu);
        result.add(MalaysiaStateJohor);
        result.add(MalaysiaStatePenang);
        result.add(MalaysiaStateKualaLumpur);
        result.add(MalaysiaStateMalacca);
        result.add(MalaysiaStateSabah);
        result.add(MalaysiaStateSarawak);

        Collections.sort(result);
        return result;
    }

    public static final String NationalityMalaysian = "Malaysian";
    public static final String NationalityNonMalaysian = "Non-Malaysian";
    
    public static ArrayList<String> Nationalities() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(NationalityMalaysian);
        result.add(NationalityNonMalaysian);

        Collections.sort(result);
        return result;
    }
    

    public static final char GenderMale = 'M';
    public static final char GenderFemale = 'F';

    public static final String GenderMaleString = "Male";
    public static String GenderFemaleString = "Female";
    
    public static ArrayList<String> GenderString() {
        ArrayList<String> result = new ArrayList<String>();
        result.add(GenderMaleString);
        result.add(GenderFemaleString);

        Collections.sort(result);
        return result;
    }

    //JOption Methods
    public static void AlertMsgInfo(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void AlertMsgError(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void AlertMsgPlain(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
    }

    public static void AlertMsgQuestion(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
    }

    public static int AlertQuestionYesNo(String message, String title) {
        return JOptionPane.showOptionDialog(null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
    }

    //Interval Timer for schedule task - appointment overdue auto cancel
    public static void scheduleAtFixedRate() {
        TimerTask task = new TimerTask() {
            public void run() {
                ArrayList<Object> al = FileOperation.DeserializeObject(General.appointmentFileName);
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                for (Object x : al) {
                    Appointment app = (Appointment) x;

                    System.out.println("run");

                    if (app.VaccinationDate == null) {
                        continue;
                    }

                    Calendar tomorrow = Calendar.getInstance(); // today
                    tomorrow.add(Calendar.DATE, 1);

                    if (app.VaccinationDate.getCal().compareTo(tomorrow) < 0 && (app.getStatus() != AppointmentStatus.Completed || app.getStatus() != AppointmentStatus.Declined || app.getStatus() != AppointmentStatus.Cancelled)) {
                        app.setStatus(AppointmentStatus.Cancelled);
                        FileOperation fo = new FileOperation(app.getCode(), General.appointmentFileName);
                        fo.ReadFile();
                        fo.ModifyRecord(app);
                    }
                }
            }
        };
        long delay = 1000L;

        Timer timer = new Timer("Timer");
        timer.schedule(task, delay);

    }
    
}
