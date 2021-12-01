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
    public static String userFileName = pathTxtFiles + "User.ser";
    public static String appointmentFileName = pathTxtFiles + "Apppointment.ser";
    public static String vaccineCentreFileName = pathTxtFiles + "VaccineCentre.ser";
    public static String vaccineFileName = pathTxtFiles + "Vaccine.ser";
    public static String stockFileName = pathTxtFiles + "Stock.ser";
    public static String stockAuditFileName = pathTxtFiles + "StockAudit.ser";
    public static String pendingStockAuditFileName = pathTxtFiles + "PendingStockAudit.ser";

    //Prefix
    public static String PrefixPeople = "PP";
    public static String PrefixPersonnel = "PSN";
    public static String PrefixVaccineCentre = "VC";
    public static String PrefixVaccine = "VAC";
    public static String PrefixAppointment = "APP";

    //UserRole
    public static String UserRolePeople = "People";
    public static String UserRolePersonnel = "Personnel";

    //PersonnelRole 
    public static String PersonnelRoleDoctor = "Doctor";
    public static String PersonnelRoleAdmin = "Admin";
    public static String PersonnelRoleStockist = "Stockist";

    //Malaysia States
    public static String MalaysiaStateJohor = "Johor";
    public static String MalaysiaStatePenang = "Penang";
    public static String MalaysiaStateKualaLumpur = "Kuala Lumpur";
    public static String MalaysiaStateMalacca = "Malacca";
    public static String MalaysiaStateSarawak = "Sarawak";
    public static String MalaysiaStateSabah = "Sabah";
    public static String MalaysiaStateTerrenganu = "Terrenganu";
    public static String MalaysiaStateNegeriSembilan = "Negeri Sembilan";
    public static String MalaysiaStateKelantan = "Kelantan";
    public static String MalaysiaStateKedah = "Kedah";
    public static String MalaysiaStatePerak = "Perak";
    public static String MalaysiaStatePahang = "Pahang";
    public static String MalaysiaStatePerlis = "Perlis";

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

    public static String NationalityMalaysian = "Malaysian";
    public static String NationalityNonMalaysian = "Non-Malaysian";

    public static char GenderMale = 'M';
    public static char GenderFemale = 'F';

    public static String GenderMaleString = "Male";
    public static String GenderFemaleString = "Female";

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
                        FileOperation fo = new FileOperation();
                        fo.ModifyRecord(app, app.getCode(), appointmentFileName);
                    }
                }
            }
        };
        long delay = 1000L;

        Timer timer = new Timer("Timer");
        timer.schedule(task, delay);

    }
}
