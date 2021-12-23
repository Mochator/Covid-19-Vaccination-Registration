/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ljk99
 */
public class AdminLoadingPage extends javax.swing.JFrame {

    private static Admin currentUser;
    private Hashtable<String, Object> htVac;
    private Hashtable<String, Object> htVacCentre;
    private Hashtable<String, Object> htAppointment;
    private Hashtable<String, Object> htUser;

    /**
     * Creates new form AdminLoadingPage
     */
    public AdminLoadingPage() {
        initComponents();
        ComponentReset();

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        this.setSize(d);
        this.setMaximumSize(d);

    }

    private void ComponentReset() {
        btnAdPSave.setVisible(false);
        pnlCredential.setVisible(false);
        lblPwNoMatch.setVisible(false);

        btnMaApprove.setEnabled(false);
        btnMaDecline.setEnabled(false);
        pnlApprovedAppointment.setVisible(false);

        txtMaRemarks.setText("");
        calMaVacDate.setCalendar(null);
        cboMaVac.setSelectedItem(-1);
        cboMaVacCentre.setSelectedItem(-1);
        txtMaVacAdd.setText("");

        btnMaSubmit.setText("Submit");

        pnlComVacCentre.setVisible(true);
        txtComCfmPw.setEnabled(false);
        txtComCfmPw.setText("");
        txtComNewPw.setText("");

        txtPpCfmPw.setEnabled(false);
        txtPpCfmPw.setText("");
        txtPpNewPw.setText("");
    }

    private void editProfile(boolean b) {
        btnAdPSave.setVisible(b);
        btnAdPInfoEdit.setVisible(!b);
        pnlCredential.setVisible(b);

        Color col = b ? Color.WHITE : Color.GRAY;

        for (Component x : jPanel9.getComponents()) {

            if (x instanceof JTextField) {
                JTextField j = (JTextField) x;
                j.setEnabled(j.isEditable() && b);
                j.setBackground(col);
            } else if (x instanceof JPasswordField) {
                JPasswordField j = (JPasswordField) x;
                j.setEnabled(b);
                j.setText("");
                j.setBackground(col);
            }
        }
    }

    public void StartUp() {
        InitGlobalData();
        InitComboData();
        PopulateUserData();
        InitTableRecords();

    }

    private void InitGlobalData() {
        ArrayList<Object> allAppointments = FileOperation.DeserializeObject(General.appointmentFileName);
        htAppointment = FileOperation.ConvertToHashTable(allAppointments);

        ArrayList<Object> allVaccines = FileOperation.DeserializeObject(General.vaccineFileName);
        htVac = FileOperation.ConvertToHashTable(allVaccines);

        ArrayList<Object> allVacCentre = FileOperation.DeserializeObject(General.vaccineCentreFileName);
        htVacCentre = FileOperation.ConvertToHashTable(allVacCentre);

        ArrayList<Object> allUsers = FileOperation.DeserializeObject(General.userFileName);
        htUser = FileOperation.ConvertToHashTable(allUsers);

    }

    private void InitVacCombo() {
        
        //Init Vaccine Types (VM)
        cboMaVac.removeAllItems();
        for (Object x : htVac.values()) {
            Vaccine v = (Vaccine) x;
            cboMaVac.addItem(v.getVacCode() + " - " + v.getName());
        }
        
        //Init Vaccine Types (V)
        cboVSearchVac.removeAllItems();
        cboVSearchVac.insertItemAt("All Vaccine", 0);
        for (Object x : htVac.values()) {
            Vaccine v = (Vaccine) x;
            cboVSearchVac.addItem(v.getVacCode() + " - " + v.getName());
        }
        cboVSearchVac.setSelectedIndex(0);

        //Init Vaccine Centre (V)
        cboVSearchVacCentre.removeAllItems();
        cboVSearchVacCentre.insertItemAt("All Vaccine Centre", 0);
        for (Object x : htVacCentre.values()) {
            VaccineCentre v = (VaccineCentre) x;
            cboVSearchVacCentre.addItem(v.getVacCode() + " - " + v.getName());
        }
        cboVSearchVacCentre.setSelectedIndex(0);

        //Init Vaccine Types (RA)
        cboRaVac.removeAllItems();
        for (Object x : htVac.values()) {
            Vaccine v = (Vaccine) x;
            cboRaVac.addItem(v.getVacCode() + " - " + v.getName());
        }

        cboSVacCentre.removeAllItems();
        cboSVacCentre.insertItemAt("Select", 0);
        for (Object x : htVacCentre.values()) {
            VaccineCentre v = (VaccineCentre) x;
            cboSVacCentre.addItem(v.getVacCode() + " - " + v.getName());
        }
        cboSVacCentre.setSelectedIndex(0);

        cboMCSearchVacCentre.removeAllItems();
        cboMCSearchVacCentre.insertItemAt("All Vac. Centre", 0);
        for (Object x : htVacCentre.values()) {
            VaccineCentre v = (VaccineCentre) x;
            cboMCSearchVacCentre.addItem(v.getVacCode() + " - " + v.getName());
        }
        cboMCSearchVacCentre.setSelectedIndex(0);

        cboComVacCentre.removeAllItems();
        cboComVacCentre.insertItemAt("Select", 0);
        for (Object x : htVacCentre.values()) {
            VaccineCentre v = (VaccineCentre) x;
            cboComVacCentre.addItem(v.getVacCode() + " - " + v.getName());
        }
        cboComVacCentre.setSelectedIndex(0);
    }

    private void InitComboData() {
        //---Profile Tab---
        //Init Gender ComboBox
        General.GenderString().forEach(d -> cboAdPGender.addItem(d));

        //---Vaccination Appointment---
        

        //Init Nationality (V)
        General.Nationalities().forEach(d -> cboVSearchNat.addItem(d));

        //---Vaccine Centre Management---
        General.MalaysiaStates().forEach(d -> cboCMStateSearch.addItem(d));
        General.MalaysiaStates().forEach(d -> cboCncState.addItem(d));
        General.MalaysiaStates().forEach(d -> cboCncState1.addItem(d));

        //---Committee Management---
        //New Committee
        General.PersonnelRoles().forEach(d -> cboSRole.addItem(d));

        //View all Committees
        General.PersonnelRoles().forEach(d -> cboMCSearchRole.addItem(d));
        for (PersonnelStatus x : PersonnelStatus.values()) {
            cboMCSearchStatus.addItem(String.valueOf(x));
        }
        General.PersonnelRoles().forEach(d -> cboComRole.addItem(d));
        for (PersonnelStatus x : PersonnelStatus.values()) {
            cboComStatus.addItem(String.valueOf(x));
        }
        //---People Management---
        General.MalaysiaStates().forEach(d -> cboPpSearchState.addItem(d));
        for (VaccinationStatus x : VaccinationStatus.values()) {
            cboPpSearchStatus.addItem(String.valueOf(x));
        }
        General.MalaysiaStates().forEach(d -> cboPpAddState.addItem(d));

        InitVacCombo();
    }

    private void PopulateUserData() {

        lblWbUsername.setText(currentUser.getFirst_Name());

        //---Profile Tab---
        txtAdPName.setText(currentUser.getFullName());

        String Gender = currentUser.getGender() == General.GenderMale ? General.GenderMaleString : General.GenderFemaleString;
        cboAdPGender.setSelectedItem(Gender);

        jDob.setCalendar(currentUser.Dob.getCal());

        txtAdPNo.setText(currentUser.getContact());
        txtPEmail.setText(currentUser.getEmail());
        txtAdPUser.setText(currentUser.Username);

        txtAdPRole.setText(General.PersonnelRoleAdmin);
        calAdPHiredDate.setCalendar(currentUser.HiredDate.getCal());

        txtPNewPw.setText("");
        txtPCfmPw.setText("");

    }

    private void InitTableRecords() {
        //----------Schedule Appointment----------
        DefaultTableModel dtmMA = (DefaultTableModel) tblMA.getModel();
        dtmMA.setRowCount(0);

        for (Object x : htAppointment.values()) {
            Appointment a = (Appointment) x;

            if (!(a.getStatus() == AppointmentStatus.Approved || a.getStatus() == AppointmentStatus.Pending)) {
                continue;
            }

            String IcPassport = "";
            if (a.Ppl.getIsCitizen()) {
                Citizen c = (Citizen) a.Ppl;
                IcPassport = c.getIcNo() + " (" + General.NationalityCitizen + ")";
            } else {
                NonCitizen c = (NonCitizen) a.Ppl;
                IcPassport = c.getPassport() + " (" + General.NationalityNonCitizen + ")";
            }

            Object[] dtmObj = new Object[]{
                a.getCode(),
                a.Ppl.getFullName() + " (" + a.Ppl.Username + ")",
                IcPassport,
                a.getRegisterDate().GetShortDateTime(),
                a.getStatus()
            };

            dtmMA.addRow(dtmObj);

        }

        tblMA.setModel(dtmMA);

        //----------All Appointments----------
        DefaultTableModel dtmV = (DefaultTableModel) tblV.getModel();
        dtmV.setRowCount(0);
        int vacVCount = 0;

        for (Object x : htAppointment.keySet()) {
            Appointment v = (Appointment) htAppointment.get(x);

            String IcPassport = "";
            if (v.Ppl.getIsCitizen()) {
                Citizen c = (Citizen) v.Ppl;
                IcPassport = c.getIcNo() + " (" + General.NationalityCitizen + ")";
            } else {
                NonCitizen c = (NonCitizen) v.Ppl;
                IcPassport = c.getPassport() + " (" + General.NationalityNonCitizen + ")";
            }

            Object[] dtmObj = new Object[]{
                ++vacVCount,
                v.getCode(),
                v.Ppl.getFullName() + " (" + v.Ppl.Username + ")",
                IcPassport,
                v.getRegisterDate().GetShortDate(),
                v.Vacc == null ? "-" : v.Vacc.GetCodeName(),
                v.Location == null ? "-" : v.Location.GetCodeName(),
                v.VaccinationDate == null ? "-" : v.VaccinationDate.GetShortDate(),
                v.getRemarks(),
                v.getHandledBy() == null ? "-" : v.getHandledBy().Username,
                v.getVaccinatedBy() == null ? "-" : (v.getVaccinatedBy().Username),
                v.getStatus()
            };

            dtmV.addRow(dtmObj);

        }

        tblV.setModel(dtmV);

        //----------Reschedule Appointment----------
        DefaultTableModel dtmRA = (DefaultTableModel) tblRA.getModel();
        dtmRA.setRowCount(0);

        for (Object x : htAppointment.keySet()) {
            Appointment a = (Appointment) htAppointment.get(x);

            int i = 0;

            if (a.getStatus() != AppointmentStatus.Declined) {
                continue;
            }

            String IcPassport = "";
            if (a.Ppl.getIsCitizen()) {
                Citizen c = (Citizen) a.Ppl;
                IcPassport = c.getIcNo() + " (" + General.NationalityCitizen + ")";
            } else {
                NonCitizen c = (NonCitizen) a.Ppl;
                IcPassport = c.getPassport() + " (" + General.NationalityNonCitizen + ")";
            }

            Object[] dtmObj = new Object[]{
                ++i,
                a.getCode(),
                a.Ppl.getFullName() + " (" + a.Ppl.Username + ")",
                a.getRegisterDate().GetShortDateTime(),
                a.getStatus()
            };

            dtmRA.addRow(dtmObj);

        }

        tblRA.setModel(dtmRA);

        //----------Vaccine----------
        DefaultTableModel dtmVac = (DefaultTableModel) tblVM.getModel();

        dtmVac.setRowCount(
                0);
        int vacCount = 0;

        for (Object x
                : htVac.keySet()) {
            Vaccine v = (Vaccine) htVac.get(x);

            Object[] dtmObj = new Object[]{
                ++vacCount,
                v.getVacCode(),
                v.getName(),
                v.getDoseCount(),
                v.getInterval()
            };

            dtmVac.addRow(dtmObj);

        }

        tblVM.setModel(dtmVac);

        //----------Vaccine Centre----------
        DefaultTableModel dtmVacCentre = (DefaultTableModel) tblCM.getModel();

        dtmVacCentre.setRowCount(0);
        int vacCentreCount = 0;

        for (Object x : htVacCentre.keySet()) {
            VaccineCentre v = (VaccineCentre) htVacCentre.get(x);

            Object[] dtmObj = new Object[]{
                ++vacCentreCount,
                v.getVacCode(),
                v.getName(),
                v.getVacAddress().getNo(),
                v.getVacAddress().getStreet(),
                v.getVacAddress().getPostcode(),
                v.getVacAddress().getCity(),
                v.getVacAddress().getState()
            };

            dtmVacCentre.addRow(dtmObj);

        }

        tblCM.setModel(dtmVacCentre);

        //----------Committee----------
        DefaultTableModel dtmCommittee = (DefaultTableModel) tblMC.getModel();

        dtmCommittee.setRowCount(0);
        int mcCount = 0;

        for (Object x : htUser.keySet()) {
            User v = (User) htUser.get(x);

            if (v.getUserRole().equals(General.UserRolePeople)) {
                continue;
            }

            Personnel p = (Personnel) v;

            Object[] dtmObj = null;

            if (p.getPersonnelRole().equals(General.PersonnelRoleAdmin)) {

                Admin u = (Admin) v;

                dtmObj = new Object[]{
                    ++mcCount,
                    u.Username,
                    u.getFullName(),
                    u.getGender(),
                    General.PersonnelRoleAdmin,
                    "-",
                    u.getHiredDate(),
                    u.getStatus()
                };

            } else if (p.getPersonnelRole().equals(General.PersonnelRoleDoctor)) {

                Doctor u = (Doctor) v;

                dtmObj = new Object[]{
                    ++mcCount,
                    u.Username,
                    u.getFullName(),
                    u.getGender(),
                    General.PersonnelRoleDoctor,
                    u.VacCentre.GetCodeName(),
                    u.getHiredDate(),
                    u.getStatus()
                };

            } else if (p.getPersonnelRole().equals(General.PersonnelRoleStockist)) {
                Stockist u = (Stockist) v;

                dtmObj = new Object[]{
                    ++mcCount,
                    u.Username,
                    u.getFullName(),
                    u.getGender(),
                    General.PersonnelRoleStockist,
                    u.VacCentre.GetCodeName(),
                    u.getHiredDate(),
                    u.getStatus()
                };

            } else {
                continue;
            }

            if (dtmObj != null) {
                dtmCommittee.addRow(dtmObj);
            }

        }

        tblMC.setModel(dtmCommittee);

        //----------People----------
        DefaultTableModel dtmPp = (DefaultTableModel) tblPp.getModel();

        dtmPp.setRowCount(0);
        int ppCount = 0;

        for (Object x : htUser.keySet()) {
            User v = (User) htUser.get(x);

            if (v.getUserRole().equals(General.UserRolePersonnel)) {
                continue;
            }

            People p = (People) v;

            Object[] dtmObj = null;

            if (p.getIsCitizen()) {

                Citizen u = (Citizen) v;

                String gender = u.getGender() == General.GenderMale ? General.GenderMaleString : General.GenderFemaleString;

                dtmObj = new Object[]{
                    ++ppCount,
                    u.Username,
                    u.getFullName(),
                    u.getIcNo() + " (" + General.NationalityCitizen + ")",
                    gender,
                    u.Dob.GetShortDate(),
                    u.getContact(),
                    u.Address.getFullAddress(),
                    u.getEmail(),
                    u.RegistrationDate.GetShortDateTime(),
                    u.getVacStatus() + " Vaccinated"
                };

            } else if (!p.getIsCitizen()) {

                NonCitizen u = (NonCitizen) v;

                String gender = u.getGender() == General.GenderMale ? General.GenderMaleString : General.GenderFemaleString;
                dtmObj = new Object[]{
                    ++ppCount,
                    u.Username,
                    u.getFullName(),
                    u.getPassport() + " (" + General.NationalityNonCitizen + ")",
                    gender,
                    u.Dob.GetShortDate(),
                    u.getContact(),
                    u.Address.getFullAddress(),
                    u.getEmail(),
                    u.RegistrationDate.GetShortDateTime(),
                    u.getVacStatus() + " Vaccinated"
                };

            } else {
                continue;
            }

            if (dtmObj != null) {
                dtmPp.addRow(dtmObj);
            }

        }

        tblPp.setModel(dtmPp);

    }

    public void setCurrentUser(Admin user) {
        this.currentUser = user;
        InitGlobalData();
    }

    private void AnvClear() {
        txtAnvName.setText("");
        txtAnvDose.setText("");
        txtAnvInterval.setText("");
    }

    private void CncClear() {
        txtCncName.setText("");
        txtCncNo.setText("");
        txtCncStreet.setText("");
        txtCncCity.setText("");
        txtCncPost.setText("");

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        lblWbUsername = new javax.swing.JLabel();
        lblVSName2 = new javax.swing.JLabel();
        lblVSName3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane8 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        txtAdPUser = new javax.swing.JTextField();
        txtAdPName = new javax.swing.JTextField();
        txtAdPNo = new javax.swing.JTextField();
        btnAdPInfoEdit = new javax.swing.JButton();
        btnAdPSave = new javax.swing.JButton();
        cboAdPGender = new javax.swing.JComboBox<>();
        jDob = new com.toedter.calendar.JDateChooser();
        txtAdPRole = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        calAdPHiredDate = new com.toedter.calendar.JDateChooser();
        pnlCredential = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        txtPEmail = new javax.swing.JTextField();
        txtPNewPw = new javax.swing.JPasswordField();
        txtPCfmPw = new javax.swing.JPasswordField();
        jLabel99 = new javax.swing.JLabel();
        lblPwNoMatch = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblPp = new javax.swing.JTable();
        txtPpSearch = new javax.swing.JTextField();
        cboPpSearchStatus = new javax.swing.JComboBox<>();
        btnPpSearch = new javax.swing.JButton();
        cboPpSearchState = new javax.swing.JComboBox<>();
        jSplitPane4 = new javax.swing.JSplitPane();
        jPanel8 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        txtPpCode = new javax.swing.JTextField();
        txtPpIC = new javax.swing.JTextField();
        txtPpLName = new javax.swing.JTextField();
        txtPpFName = new javax.swing.JTextField();
        jLabel152 = new javax.swing.JLabel();
        jLabel153 = new javax.swing.JLabel();
        jLabel154 = new javax.swing.JLabel();
        calPpDob = new com.toedter.calendar.JDateChooser();
        rbPpFemale = new javax.swing.JRadioButton();
        rbPpMale = new javax.swing.JRadioButton();
        jLabel19 = new javax.swing.JLabel();
        txtPpNat = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        txtPpContact = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        txtPpAddNo = new javax.swing.JTextField();
        txtPpAddStreet = new javax.swing.JTextField();
        txtPpAddPost = new javax.swing.JTextField();
        txtPpAddCity = new javax.swing.JTextField();
        cboPpAddState = new javax.swing.JComboBox<>();
        jPanel11 = new javax.swing.JPanel();
        jLabel155 = new javax.swing.JLabel();
        jLabel156 = new javax.swing.JLabel();
        jLabel157 = new javax.swing.JLabel();
        txtPpEmail = new javax.swing.JTextField();
        btnSRegister1 = new javax.swing.JButton();
        txtPpNewPw = new javax.swing.JPasswordField();
        jLabel158 = new javax.swing.JLabel();
        txtPpCfmPw = new javax.swing.JPasswordField();
        jLabel159 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        rbSFemale = new javax.swing.JRadioButton();
        jLabel126 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        rbSMale = new javax.swing.JRadioButton();
        cboSVacCentre = new javax.swing.JComboBox<>();
        jLabel127 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        calSDob = new com.toedter.calendar.JDateChooser();
        jLabel128 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtSFName = new javax.swing.JTextField();
        jLabel129 = new javax.swing.JLabel();
        cboSRole = new javax.swing.JComboBox<>();
        txtSLName = new javax.swing.JTextField();
        txtSContact = new javax.swing.JTextField();
        jLabel130 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel131 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel132 = new javax.swing.JLabel();
        jLabel133 = new javax.swing.JLabel();
        txtSEmail = new javax.swing.JTextField();
        btnSRegister = new javax.swing.JButton();
        btnSClear = new javax.swing.JButton();
        txtSPass = new javax.swing.JPasswordField();
        txtSCfmPass = new javax.swing.JPasswordField();
        jScrollPane10 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane12 = new javax.swing.JScrollPane();
        tblMC = new javax.swing.JTable();
        txtMCSearch = new javax.swing.JTextField();
        btnMCSearch = new javax.swing.JButton();
        cboMCSearchRole = new javax.swing.JComboBox<>();
        cboMCSearchStatus = new javax.swing.JComboBox<>();
        cboMCSearchVacCentre = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jLabel113 = new javax.swing.JLabel();
        jLabel114 = new javax.swing.JLabel();
        jLabel115 = new javax.swing.JLabel();
        jLabel116 = new javax.swing.JLabel();
        jLabel125 = new javax.swing.JLabel();
        txtComCode = new javax.swing.JTextField();
        txtComFName = new javax.swing.JTextField();
        txtComContact = new javax.swing.JTextField();
        btnComSave = new javax.swing.JButton();
        calComDob = new com.toedter.calendar.JDateChooser();
        jLabel135 = new javax.swing.JLabel();
        jLabel136 = new javax.swing.JLabel();
        calComHiredDate = new com.toedter.calendar.JDateChooser();
        jLabel137 = new javax.swing.JLabel();
        txtComEmail = new javax.swing.JTextField();
        jLabel139 = new javax.swing.JLabel();
        jLabel138 = new javax.swing.JLabel();
        txtComNewPw = new javax.swing.JPasswordField();
        jLabel140 = new javax.swing.JLabel();
        txtComCfmPw = new javax.swing.JPasswordField();
        cboComRole = new javax.swing.JComboBox<>();
        txtComLName = new javax.swing.JTextField();
        jLabel148 = new javax.swing.JLabel();
        rbComMale = new javax.swing.JRadioButton();
        rbComFemale = new javax.swing.JRadioButton();
        pnlComVacCentre = new javax.swing.JPanel();
        cboComVacCentre = new javax.swing.JComboBox<>();
        jLabel149 = new javax.swing.JLabel();
        jLabel150 = new javax.swing.JLabel();
        cboComStatus = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel15 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblVM = new javax.swing.JTable();
        jPanel17 = new javax.swing.JPanel();
        jLabel95 = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        txtAnvDose = new javax.swing.JTextField();
        txtAnvName = new javax.swing.JTextField();
        btnAnvAdd = new javax.swing.JButton();
        jLabel97 = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        txtAnvInterval = new javax.swing.JTextField();
        txtVMSearch = new javax.swing.JTextField();
        btnVMSearch = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblCM = new javax.swing.JTable();
        txtCMSearch = new javax.swing.JTextField();
        btnCMSearch = new javax.swing.JButton();
        cboCMStateSearch = new javax.swing.JComboBox<>();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel24 = new javax.swing.JPanel();
        jLabel117 = new javax.swing.JLabel();
        jLabel118 = new javax.swing.JLabel();
        jLabel119 = new javax.swing.JLabel();
        txtCncStreet = new javax.swing.JTextField();
        txtCncNo = new javax.swing.JTextField();
        txtCncName = new javax.swing.JTextField();
        btnCncAdd = new javax.swing.JButton();
        jLabel120 = new javax.swing.JLabel();
        jLabel121 = new javax.swing.JLabel();
        jLabel122 = new javax.swing.JLabel();
        txtCncPost = new javax.swing.JTextField();
        txtCncCity = new javax.swing.JTextField();
        jLabel123 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        cboCncState = new javax.swing.JComboBox<>();
        jLabel124 = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel134 = new javax.swing.JLabel();
        jLabel141 = new javax.swing.JLabel();
        jLabel142 = new javax.swing.JLabel();
        txtCncStreet1 = new javax.swing.JTextField();
        txtCncNo1 = new javax.swing.JTextField();
        txtCncName1 = new javax.swing.JTextField();
        btnCncAdd1 = new javax.swing.JButton();
        jLabel143 = new javax.swing.JLabel();
        jLabel144 = new javax.swing.JLabel();
        jLabel145 = new javax.swing.JLabel();
        txtCncPost1 = new javax.swing.JTextField();
        txtCncCity1 = new javax.swing.JTextField();
        jLabel146 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        cboCncState1 = new javax.swing.JComboBox<>();
        jLabel147 = new javax.swing.JLabel();
        txtCncCode = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMA = new javax.swing.JTable();
        pnlApprovedAppointment = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        calMaVacDate = new com.toedter.calendar.JDateChooser();
        jLabel62 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        txtMaRemarks = new javax.swing.JTextField();
        btnMaSubmit = new javax.swing.JButton();
        jLabel65 = new javax.swing.JLabel();
        cboMaVac = new javax.swing.JComboBox<>();
        cboMaVacCentre = new javax.swing.JComboBox<>();
        txtMaVacAdd = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtMaAppCode = new javax.swing.JTextField();
        txtMaIC = new javax.swing.JTextField();
        txtMaFullname = new javax.swing.JTextField();
        txtMaUsername = new javax.swing.JTextField();
        btnMaApprove = new javax.swing.JButton();
        btnMaDecline = new javax.swing.JButton();
        jLabel64 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        txtMaGender = new javax.swing.JTextField();
        jLabel86 = new javax.swing.JLabel();
        txtMaNat = new javax.swing.JTextField();
        calMaDob = new com.toedter.calendar.JDateChooser();
        jLabel89 = new javax.swing.JLabel();
        txtMaAddress = new javax.swing.JTextField();
        txtMaSearch = new javax.swing.JTextField();
        btnMASearch = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        txtVSearch = new javax.swing.JTextField();
        btnVSearch = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblV = new javax.swing.JTable();
        cboVSearchNat = new javax.swing.JComboBox<>();
        cboVSearchVac = new javax.swing.JComboBox<>();
        cboVSearchStatus = new javax.swing.JComboBox<>();
        cboVSearchVacCentre = new javax.swing.JComboBox<>();
        calVSearchVacDate = new com.toedter.calendar.JDateChooser();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblRA = new javax.swing.JTable();
        txtRaSearch = new javax.swing.JTextField();
        btnRASearch = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jPanel19 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        txtRaAppCode = new javax.swing.JTextField();
        txtRaIC = new javax.swing.JTextField();
        txtRaFullname = new javax.swing.JTextField();
        txtRaUsername = new javax.swing.JTextField();
        jLabel87 = new javax.swing.JLabel();
        txtRaGender = new javax.swing.JTextField();
        jLabel90 = new javax.swing.JLabel();
        txtRaNat = new javax.swing.JTextField();
        calRaDob = new com.toedter.calendar.JDateChooser();
        jLabel91 = new javax.swing.JLabel();
        txtRaAddress = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlApprovedAppointment2 = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        calMaVacDate2 = new com.toedter.calendar.JDateChooser();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        txtMaRemarks2 = new javax.swing.JTextField();
        btnMaSubmit3 = new javax.swing.JButton();
        jLabel76 = new javax.swing.JLabel();
        cboMaVac2 = new javax.swing.JComboBox<>();
        cboMaVacCentre2 = new javax.swing.JComboBox<>();
        txtMaVacAdd2 = new javax.swing.JTextField();
        pnlApprovedAppointment4 = new javax.swing.JPanel();
        jLabel102 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        calRaVacDate = new com.toedter.calendar.JDateChooser();
        jLabel104 = new javax.swing.JLabel();
        jLabel105 = new javax.swing.JLabel();
        txtRaRemarks = new javax.swing.JTextField();
        jLabel106 = new javax.swing.JLabel();
        txtRaVacAdd = new javax.swing.JTextField();
        txtRaVac = new javax.swing.JTextField();
        txtRaVacCentre = new javax.swing.JTextField();
        txtRaReason = new javax.swing.JTextField();
        jLabel112 = new javax.swing.JLabel();
        pnlApprovedAppointment5 = new javax.swing.JPanel();
        btnRaSubmit = new javax.swing.JButton();
        jLabel111 = new javax.swing.JLabel();
        jLabel107 = new javax.swing.JLabel();
        jLabel108 = new javax.swing.JLabel();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        txtRaRemarks2 = new javax.swing.JTextField();
        calRaVacDate2 = new com.toedter.calendar.JDateChooser();
        cboRaVac = new javax.swing.JComboBox<>();
        cboRaVacCentre = new javax.swing.JComboBox<>();
        txtRaVacAdd2 = new javax.swing.JTextField();
        btnRaCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Admin Panel");

        jPanel1.setBackground(new java.awt.Color(204, 0, 0));
        jPanel1.setForeground(new java.awt.Color(0, 0, 0));
        jPanel1.setMinimumSize(new java.awt.Dimension(1366, 768));

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));
        jPanel2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/covid-19.png"))); // NOI18N

        btnLogout.setBackground(new java.awt.Color(51, 51, 51));
        btnLogout.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setText("Logout");
        btnLogout.setBorder(null);
        btnLogout.setOpaque(true);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        jPanel16.setBackground(new java.awt.Color(102, 0, 0));

        lblWbUsername.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 20)); // NOI18N
        lblWbUsername.setForeground(new java.awt.Color(0, 0, 0));
        lblWbUsername.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWbUsername.setText("Username");

        lblVSName2.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 20)); // NOI18N
        lblVSName2.setForeground(new java.awt.Color(0, 0, 0));
        lblVSName2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVSName2.setText("Admin Panel");

        lblVSName3.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 20)); // NOI18N
        lblVSName3.setForeground(new java.awt.Color(0, 0, 0));
        lblVSName3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVSName3.setText("Welcome Back");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblWbUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblVSName2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblVSName3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(lblVSName3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblWbUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addComponent(lblVSName2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jPanel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(78, 78, 78)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.setBackground(new java.awt.Color(0, 0, 0));
        jTabbedPane1.setForeground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 12)); // NOI18N
        jTabbedPane1.setOpaque(true);

        jScrollPane8.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));

        jLabel50.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel50.setForeground(new java.awt.Color(255, 255, 255));
        jLabel50.setText("User Profile");

        jLabel51.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel51.setForeground(new java.awt.Color(255, 255, 255));
        jLabel51.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel51.setText("Full Name:");

        jLabel52.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel52.setForeground(new java.awt.Color(255, 255, 255));
        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel52.setText("Gender:");

        jLabel54.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(255, 255, 255));
        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel54.setText("Date of Birth:");

        jLabel56.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(255, 255, 255));
        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel56.setText("Contact No:");

        jLabel58.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(255, 255, 255));
        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel58.setText("Committee Code");

        txtAdPUser.setEditable(false);
        txtAdPUser.setBackground(new java.awt.Color(204, 204, 204));
        txtAdPUser.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdPUser.setForeground(new java.awt.Color(0, 0, 0));
        txtAdPUser.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdPUser.setEnabled(false);

        txtAdPName.setEditable(false);
        txtAdPName.setBackground(new java.awt.Color(204, 204, 204));
        txtAdPName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdPName.setForeground(new java.awt.Color(0, 0, 0));
        txtAdPName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdPName.setEnabled(false);
        txtAdPName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAdPNameActionPerformed(evt);
            }
        });

        txtAdPNo.setEditable(false);
        txtAdPNo.setBackground(new java.awt.Color(204, 204, 204));
        txtAdPNo.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdPNo.setForeground(new java.awt.Color(0, 0, 0));
        txtAdPNo.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdPNo.setEnabled(false);

        btnAdPInfoEdit.setBackground(new java.awt.Color(102, 255, 102));
        btnAdPInfoEdit.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnAdPInfoEdit.setForeground(new java.awt.Color(0, 0, 0));
        btnAdPInfoEdit.setText("Edit Profile");
        btnAdPInfoEdit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnAdPInfoEdit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdPInfoEdit.setOpaque(true);
        btnAdPInfoEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdPInfoEditActionPerformed(evt);
            }
        });

        btnAdPSave.setBackground(new java.awt.Color(51, 102, 255));
        btnAdPSave.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnAdPSave.setForeground(new java.awt.Color(0, 0, 0));
        btnAdPSave.setText("Save");
        btnAdPSave.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnAdPSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdPSave.setOpaque(true);
        btnAdPSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdPSaveActionPerformed(evt);
            }
        });

        cboAdPGender.setBackground(new java.awt.Color(204, 204, 204));
        cboAdPGender.setForeground(new java.awt.Color(0, 0, 0));
        cboAdPGender.setMaximumRowCount(2);
        cboAdPGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Male", "Female" }));
        cboAdPGender.setEnabled(false);
        cboAdPGender.setOpaque(true);

        jDob.setBackground(new java.awt.Color(255, 255, 255));
        jDob.setForeground(new java.awt.Color(0, 0, 0));
        jDob.setEnabled(false);

        txtAdPRole.setEditable(false);
        txtAdPRole.setBackground(new java.awt.Color(204, 204, 204));
        txtAdPRole.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdPRole.setForeground(new java.awt.Color(0, 0, 0));
        txtAdPRole.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdPRole.setEnabled(false);

        jLabel78.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel78.setForeground(new java.awt.Color(255, 255, 255));
        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel78.setText("Role");

        jLabel88.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel88.setForeground(new java.awt.Color(255, 255, 255));
        jLabel88.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel88.setText("Hired Date");

        calAdPHiredDate.setBackground(new java.awt.Color(255, 255, 255));
        calAdPHiredDate.setForeground(new java.awt.Color(0, 0, 0));
        calAdPHiredDate.setEnabled(false);

        pnlCredential.setBackground(new java.awt.Color(57, 57, 57));

        jLabel53.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel53.setForeground(new java.awt.Color(255, 255, 255));
        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel53.setText("Email:");

        jLabel55.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(255, 255, 255));
        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel55.setText("New Password:");

        txtPEmail.setBackground(new java.awt.Color(204, 204, 204));
        txtPEmail.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtPEmail.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPNewPw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPNewPwKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPNewPwKeyTyped(evt);
            }
        });

        txtPCfmPw.setEnabled(false);
        txtPCfmPw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPCfmPwKeyReleased(evt);
            }
        });

        jLabel99.setFont(new java.awt.Font("Bell MT", 0, 10)); // NOI18N
        jLabel99.setForeground(new java.awt.Color(204, 204, 204));
        jLabel99.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel99.setText("Leave this field blank to retain current password.");

        lblPwNoMatch.setFont(new java.awt.Font("Bell MT", 0, 10)); // NOI18N
        lblPwNoMatch.setForeground(new java.awt.Color(204, 0, 0));
        lblPwNoMatch.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPwNoMatch.setText("Password doesn't match!");

        jLabel151.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel151.setForeground(new java.awt.Color(255, 255, 255));
        jLabel151.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel151.setText("Confirm Password:");

        javax.swing.GroupLayout pnlCredentialLayout = new javax.swing.GroupLayout(pnlCredential);
        pnlCredential.setLayout(pnlCredentialLayout);
        pnlCredentialLayout.setHorizontalGroup(
            pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCredentialLayout.createSequentialGroup()
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCredentialLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCredentialLayout.createSequentialGroup()
                        .addContainerGap(33, Short.MAX_VALUE)
                        .addComponent(jLabel151, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)))
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPwNoMatch, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtPCfmPw)
                        .addComponent(txtPNewPw, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtPEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(2445, Short.MAX_VALUE))
        );
        pnlCredentialLayout.setVerticalGroup(
            pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCredentialLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCredentialLayout.createSequentialGroup()
                        .addComponent(txtPEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtPNewPw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel99)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPCfmPw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel151))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblPwNoMatch))
                    .addGroup(pnlCredentialLayout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel55)))
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(699, 699, 699))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtAdPName, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel52, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 125, Short.MAX_VALUE)
                                        .addComponent(cboAdPGender, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(123, 123, 123)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jDob, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtAdPNo, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)))
                                    .addComponent(jLabel58, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtAdPUser, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtAdPRole, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(122, 122, 122)
                                        .addComponent(calAdPHiredDate, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(jLabel51, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(381, 381, 381)
                        .addComponent(btnAdPSave, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAdPInfoEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(pnlCredential, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 41, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel50, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel58)
                    .addComponent(txtAdPUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel78)
                    .addComponent(txtAdPRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel88)
                    .addComponent(calAdPHiredDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel51)
                    .addComponent(txtAdPName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(cboAdPGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel54)
                    .addComponent(jDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(txtAdPNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addComponent(pnlCredential, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAdPInfoEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdPSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(838, Short.MAX_VALUE))
        );

        jScrollPane8.setViewportView(jPanel4);

        jTabbedPane1.addTab("Profile", jScrollPane8);

        jPanel9.setBackground(new java.awt.Color(51, 51, 51));

        tblPp.setBackground(new java.awt.Color(255, 255, 255));
        tblPp.setForeground(new java.awt.Color(0, 0, 0));
        tblPp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Username", "Full Name", "IC/Passport", "Gender", "DOB", "Contact", "Address", "Email", "Reg. Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPp.setSelectionBackground(new java.awt.Color(51, 51, 51));
        tblPp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPpMouseClicked(evt);
            }
        });
        tblPp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblPpKeyPressed(evt);
            }
        });
        jScrollPane5.setViewportView(tblPp);
        if (tblPp.getColumnModel().getColumnCount() > 0) {
            tblPp.getColumnModel().getColumn(0).setResizable(false);
            tblPp.getColumnModel().getColumn(1).setResizable(false);
            tblPp.getColumnModel().getColumn(2).setResizable(false);
            tblPp.getColumnModel().getColumn(2).setHeaderValue("IC/Passport");
            tblPp.getColumnModel().getColumn(3).setResizable(false);
            tblPp.getColumnModel().getColumn(3).setHeaderValue("Nationality");
            tblPp.getColumnModel().getColumn(4).setResizable(false);
            tblPp.getColumnModel().getColumn(5).setResizable(false);
            tblPp.getColumnModel().getColumn(5).setHeaderValue("Status");
        }

        txtPpSearch.setBackground(new java.awt.Color(255, 255, 255));
        txtPpSearch.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        txtPpSearch.setForeground(new java.awt.Color(0, 0, 0));
        txtPpSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtPpSearch.setSelectionColor(new java.awt.Color(255, 255, 51));

        cboPpSearchStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Status" }));
        cboPpSearchStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPpSearchStatusActionPerformed(evt);
            }
        });

        btnPpSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/search.png"))); // NOI18N
        btnPpSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnPpSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPpSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPpSearchActionPerformed(evt);
            }
        });

        cboPpSearchState.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All States" }));
        cboPpSearchState.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboPpSearchStateActionPerformed(evt);
            }
        });

        jPanel8.setBackground(new java.awt.Color(51, 153, 255));

        jLabel14.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("First Name");

        jLabel16.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Last Name");

        jLabel18.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("IC/Passport:");

        txtPpCode.setEditable(false);
        txtPpCode.setBackground(new java.awt.Color(204, 204, 204));
        txtPpCode.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpCode.setForeground(new java.awt.Color(0, 0, 0));
        txtPpCode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpCode.setEnabled(false);
        txtPpCode.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPpIC.setEditable(false);
        txtPpIC.setBackground(new java.awt.Color(204, 204, 204));
        txtPpIC.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpIC.setForeground(new java.awt.Color(0, 0, 0));
        txtPpIC.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpIC.setEnabled(false);
        txtPpIC.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPpLName.setBackground(new java.awt.Color(204, 204, 204));
        txtPpLName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpLName.setForeground(new java.awt.Color(0, 0, 0));
        txtPpLName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpLName.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPpFName.setBackground(new java.awt.Color(204, 204, 204));
        txtPpFName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpFName.setForeground(new java.awt.Color(0, 0, 0));
        txtPpFName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpFName.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel152.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel152.setForeground(new java.awt.Color(0, 0, 0));
        jLabel152.setText("People Info.");

        jLabel153.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel153.setForeground(new java.awt.Color(0, 0, 0));
        jLabel153.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel153.setText("Gender:");

        jLabel154.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel154.setForeground(new java.awt.Color(0, 0, 0));
        jLabel154.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel154.setText("Date of Birth:");

        calPpDob.setBackground(new java.awt.Color(255, 255, 255));
        calPpDob.setForeground(new java.awt.Color(0, 0, 0));

        buttonGroup3.add(rbPpFemale);
        rbPpFemale.setText("Female");

        buttonGroup3.add(rbPpMale);
        rbPpMale.setSelected(true);
        rbPpMale.setText("Male");
        rbPpMale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPpMaleActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("Nationality");

        txtPpNat.setEditable(false);
        txtPpNat.setBackground(new java.awt.Color(204, 204, 204));
        txtPpNat.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpNat.setForeground(new java.awt.Color(0, 0, 0));
        txtPpNat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpNat.setEnabled(false);
        txtPpNat.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel24.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 0, 0));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Contact");

        txtPpContact.setBackground(new java.awt.Color(204, 204, 204));
        txtPpContact.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpContact.setForeground(new java.awt.Color(0, 0, 0));
        txtPpContact.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpContact.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel25.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 0, 0));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Address");

        txtPpAddNo.setBackground(new java.awt.Color(204, 204, 204));
        txtPpAddNo.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpAddNo.setForeground(new java.awt.Color(0, 0, 0));
        txtPpAddNo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpAddNo.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPpAddStreet.setBackground(new java.awt.Color(204, 204, 204));
        txtPpAddStreet.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpAddStreet.setForeground(new java.awt.Color(0, 0, 0));
        txtPpAddStreet.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpAddStreet.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPpAddPost.setBackground(new java.awt.Color(204, 204, 204));
        txtPpAddPost.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpAddPost.setForeground(new java.awt.Color(0, 0, 0));
        txtPpAddPost.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpAddPost.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPpAddCity.setBackground(new java.awt.Color(204, 204, 204));
        txtPpAddCity.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpAddCity.setForeground(new java.awt.Color(0, 0, 0));
        txtPpAddCity.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpAddCity.setSelectionColor(new java.awt.Color(255, 255, 51));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cboPpAddState, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)
                                .addComponent(txtPpContact))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)
                                .addComponent(txtPpNat))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48)
                                .addComponent(txtPpIC))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel154, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(calPpDob, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel152)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtPpCode, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(txtPpAddPost, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel153, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(48, 48, 48)
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtPpLName, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtPpFName, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel8Layout.createSequentialGroup()
                                                .addComponent(rbPpMale)
                                                .addGap(88, 88, 88)
                                                .addComponent(rbPpFemale))))
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(48, 48, 48)
                                        .addComponent(txtPpAddNo, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPpAddCity, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPpAddStreet, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel152, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPpCode, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtPpFName, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtPpLName, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel153)
                    .addComponent(rbPpMale)
                    .addComponent(rbPpFemale))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(calPpDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel154))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtPpIC, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(txtPpNat, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPpContact)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPpAddNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPpAddStreet)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPpAddPost)
                    .addComponent(txtPpAddCity))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cboPpAddState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane4.setLeftComponent(jPanel8);

        jPanel11.setBackground(new java.awt.Color(51, 102, 255));

        jLabel155.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel155.setForeground(new java.awt.Color(0, 0, 0));
        jLabel155.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel155.setText("E-mail");

        jLabel156.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel156.setForeground(new java.awt.Color(0, 0, 0));
        jLabel156.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel156.setText("New Password:");

        jLabel157.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel157.setForeground(new java.awt.Color(0, 0, 0));
        jLabel157.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel157.setText("Confirm Password:");

        txtPpEmail.setBackground(new java.awt.Color(255, 255, 255));
        txtPpEmail.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtPpEmail.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtPpEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtPpEmail.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnSRegister1.setBackground(new java.awt.Color(102, 255, 102));
        btnSRegister1.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnSRegister1.setForeground(new java.awt.Color(0, 0, 0));
        btnSRegister1.setText("Save");
        btnSRegister1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnSRegister1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSRegister1.setOpaque(true);
        btnSRegister1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSRegister1ActionPerformed(evt);
            }
        });

        txtPpNewPw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPpNewPwKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPpNewPwKeyTyped(evt);
            }
        });

        jLabel158.setBackground(new java.awt.Color(51, 51, 51));
        jLabel158.setFont(new java.awt.Font("Bell MT", 0, 10)); // NOI18N
        jLabel158.setForeground(new java.awt.Color(204, 204, 204));
        jLabel158.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel158.setText("Leave this field blank to retain current password.");

        txtPpCfmPw.setEnabled(false);
        txtPpCfmPw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPpCfmPwKeyReleased(evt);
            }
        });

        jLabel159.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel159.setForeground(new java.awt.Color(0, 0, 0));
        jLabel159.setText("Account Info.");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(338, 338, 338)
                .addComponent(btnSRegister1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel159)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel155, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel156, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel157, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel158, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPpCfmPw, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtPpNewPw, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtPpEmail, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)))))
                .addGap(64, 64, 64))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel159, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel155, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPpEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel156)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(txtPpNewPw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel158)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPpCfmPw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel157))))
                .addGap(34, 34, 34)
                .addComponent(btnSRegister1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
        );

        jSplitPane4.setRightComponent(jPanel11);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(txtPpSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPpSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboPpSearchStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboPpSearchState, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSplitPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 891, Short.MAX_VALUE)
                    .addComponent(jScrollPane5))
                .addGap(0, 28393, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboPpSearchStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboPpSearchState, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnPpSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPpSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSplitPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(741, Short.MAX_VALUE))
        );

        jScrollPane9.setViewportView(jPanel9);

        jTabbedPane1.addTab("People Management", jScrollPane9);

        jTabbedPane2.setBackground(new java.awt.Color(51, 51, 51));
        jTabbedPane2.setOpaque(true);

        jPanel5.setBackground(new java.awt.Color(255, 102, 102));
        jPanel5.setMinimumSize(new java.awt.Dimension(500, 100));

        buttonGroup1.add(rbSFemale);
        rbSFemale.setText("Female");

        jLabel126.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel126.setForeground(new java.awt.Color(0, 0, 0));
        jLabel126.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel126.setText("Vaccination Centre:");

        jLabel20.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Gender:");

        buttonGroup1.add(rbSMale);
        rbSMale.setSelected(true);
        rbSMale.setText("Male");
        rbSMale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbSMaleActionPerformed(evt);
            }
        });

        cboSVacCentre.setEnabled(false);

        jLabel127.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel127.setForeground(new java.awt.Color(0, 0, 0));
        jLabel127.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel127.setText("Last Name:");

        jLabel21.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 0, 0));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("First Name:");

        jLabel128.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel128.setForeground(new java.awt.Color(0, 0, 0));
        jLabel128.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel128.setText("Commitee Role:");

        jLabel17.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Basic Information");

        txtSFName.setBackground(new java.awt.Color(255, 255, 255));
        txtSFName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSFName.setForeground(new java.awt.Color(0, 0, 0));
        txtSFName.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSFName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSFName.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel129.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel129.setForeground(new java.awt.Color(0, 0, 0));
        jLabel129.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel129.setText("Date of Birth:");

        cboSRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSRoleActionPerformed(evt);
            }
        });

        txtSLName.setBackground(new java.awt.Color(255, 255, 255));
        txtSLName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSLName.setForeground(new java.awt.Color(0, 0, 0));
        txtSLName.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSLName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSLName.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtSContact.setBackground(new java.awt.Color(255, 255, 255));
        txtSContact.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSContact.setForeground(new java.awt.Color(0, 0, 0));
        txtSContact.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSContact.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSContact.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel130.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel130.setForeground(new java.awt.Color(0, 0, 0));
        jLabel130.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel130.setText("Contact No.:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel127, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel126, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel128, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel130, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel129, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                .addComponent(rbSMale)
                                .addGap(88, 88, 88)
                                .addComponent(rbSFemale)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(cboSVacCentre, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(1427, 1427, 1427))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(cboSRole, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtSContact, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(calSDob, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                            .addComponent(txtSLName, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtSFName, javax.swing.GroupLayout.Alignment.LEADING))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtSFName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSLName, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
                    .addComponent(jLabel127))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(rbSMale)
                    .addComponent(rbSFemale))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel129)
                    .addComponent(calSDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel130)
                    .addComponent(txtSContact, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel128)
                    .addComponent(cboSRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel126)
                    .addComponent(cboSVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jSplitPane3.setLeftComponent(jPanel5);

        jPanel10.setBackground(new java.awt.Color(51, 102, 255));

        jLabel131.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel131.setForeground(new java.awt.Color(0, 0, 0));
        jLabel131.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel131.setText("E-mail");

        jLabel22.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Account Info.");

        jLabel132.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel132.setForeground(new java.awt.Color(0, 0, 0));
        jLabel132.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel132.setText("Password:");

        jLabel133.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel133.setForeground(new java.awt.Color(0, 0, 0));
        jLabel133.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel133.setText("Confirm Password:");

        txtSEmail.setBackground(new java.awt.Color(255, 255, 255));
        txtSEmail.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtSEmail.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSEmail.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnSRegister.setBackground(new java.awt.Color(102, 255, 102));
        btnSRegister.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnSRegister.setForeground(new java.awt.Color(0, 0, 0));
        btnSRegister.setText("Register");
        btnSRegister.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnSRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSRegister.setOpaque(true);
        btnSRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSRegisterActionPerformed(evt);
            }
        });

        btnSClear.setBackground(new java.awt.Color(204, 204, 204));
        btnSClear.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnSClear.setForeground(new java.awt.Color(0, 0, 0));
        btnSClear.setText("Clear");
        btnSClear.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnSClear.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSClear.setOpaque(true);
        btnSClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(btnSClear, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel131, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel132, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel133, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtSEmail)
                            .addComponent(txtSPass)
                            .addComponent(txtSCfmPass, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))))
                .addContainerGap(388, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel131, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtSEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel132)
                    .addComponent(txtSPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel133)
                    .addComponent(txtSCfmPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSClear, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34))
        );

        jSplitPane3.setRightComponent(jPanel10);

        jTabbedPane2.addTab("Create New Committee", jSplitPane3);

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));

        tblMC.setBackground(new java.awt.Color(255, 255, 255));
        tblMC.setForeground(new java.awt.Color(0, 0, 0));
        tblMC.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Code", "Full Name", "Gender", "Role", "Vac. Centre", "Hired Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMCMouseClicked(evt);
            }
        });
        jScrollPane12.setViewportView(tblMC);

        txtMCSearch.setBackground(new java.awt.Color(255, 255, 255));
        txtMCSearch.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        txtMCSearch.setForeground(new java.awt.Color(0, 0, 0));
        txtMCSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtMCSearch.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnMCSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/search.png"))); // NOI18N
        btnMCSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnMCSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMCSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMCSearchActionPerformed(evt);
            }
        });

        cboMCSearchRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Roles" }));
        cboMCSearchRole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMCSearchRoleActionPerformed(evt);
            }
        });

        cboMCSearchStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Status" }));
        cboMCSearchStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMCSearchStatusActionPerformed(evt);
            }
        });

        cboMCSearchVacCentre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMCSearchVacCentreActionPerformed(evt);
            }
        });

        jPanel7.setBackground(new java.awt.Color(204, 153, 0));

        jLabel113.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel113.setForeground(new java.awt.Color(255, 255, 255));
        jLabel113.setText("Edit Committee Info");

        jLabel114.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel114.setForeground(new java.awt.Color(255, 255, 255));
        jLabel114.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel114.setText("First Name");

        jLabel115.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel115.setForeground(new java.awt.Color(255, 255, 255));
        jLabel115.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel115.setText("Gender:");

        jLabel116.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel116.setForeground(new java.awt.Color(255, 255, 255));
        jLabel116.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel116.setText("Date of Birth:");

        jLabel125.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel125.setForeground(new java.awt.Color(255, 255, 255));
        jLabel125.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel125.setText("Contact No:");

        txtComCode.setEditable(false);
        txtComCode.setBackground(new java.awt.Color(204, 204, 204));
        txtComCode.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtComCode.setForeground(new java.awt.Color(0, 0, 0));
        txtComCode.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtComCode.setEnabled(false);

        txtComFName.setBackground(new java.awt.Color(204, 204, 204));
        txtComFName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtComFName.setForeground(new java.awt.Color(0, 0, 0));
        txtComFName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtComFName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtComFNameActionPerformed(evt);
            }
        });

        txtComContact.setEditable(false);
        txtComContact.setBackground(new java.awt.Color(204, 204, 204));
        txtComContact.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtComContact.setForeground(new java.awt.Color(0, 0, 0));
        txtComContact.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        btnComSave.setBackground(new java.awt.Color(51, 102, 255));
        btnComSave.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnComSave.setForeground(new java.awt.Color(0, 0, 0));
        btnComSave.setText("Save");
        btnComSave.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnComSave.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnComSave.setOpaque(true);
        btnComSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnComSaveActionPerformed(evt);
            }
        });

        calComDob.setBackground(new java.awt.Color(255, 255, 255));
        calComDob.setForeground(new java.awt.Color(0, 0, 0));

        jLabel135.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel135.setForeground(new java.awt.Color(255, 255, 255));
        jLabel135.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel135.setText("Role");

        jLabel136.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel136.setForeground(new java.awt.Color(255, 255, 255));
        jLabel136.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel136.setText("Hired Date");

        calComHiredDate.setBackground(new java.awt.Color(255, 255, 255));
        calComHiredDate.setForeground(new java.awt.Color(0, 0, 0));
        calComHiredDate.setEnabled(false);

        jLabel137.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel137.setForeground(new java.awt.Color(255, 255, 255));
        jLabel137.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel137.setText("Email:");

        txtComEmail.setBackground(new java.awt.Color(204, 204, 204));
        txtComEmail.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtComEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtComEmail.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel139.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel139.setForeground(new java.awt.Color(255, 255, 255));
        jLabel139.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel139.setText("Confirm Password:");

        jLabel138.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel138.setForeground(new java.awt.Color(255, 255, 255));
        jLabel138.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel138.setText("New Password:");

        txtComNewPw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtComNewPwKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtComNewPwKeyTyped(evt);
            }
        });

        jLabel140.setBackground(new java.awt.Color(51, 51, 51));
        jLabel140.setFont(new java.awt.Font("Bell MT", 0, 10)); // NOI18N
        jLabel140.setForeground(new java.awt.Color(204, 204, 204));
        jLabel140.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel140.setText("Leave this field blank to retain current password.");

        txtComCfmPw.setEnabled(false);
        txtComCfmPw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtComCfmPwKeyReleased(evt);
            }
        });

        cboComRole.setEnabled(false);

        txtComLName.setBackground(new java.awt.Color(204, 204, 204));
        txtComLName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtComLName.setForeground(new java.awt.Color(0, 0, 0));
        txtComLName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtComLName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtComLNameActionPerformed(evt);
            }
        });

        jLabel148.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel148.setForeground(new java.awt.Color(255, 255, 255));
        jLabel148.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel148.setText("Last Name");

        buttonGroup2.add(rbComMale);
        rbComMale.setSelected(true);
        rbComMale.setText("Male");
        rbComMale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbComMaleActionPerformed(evt);
            }
        });

        buttonGroup2.add(rbComFemale);
        rbComFemale.setText("Female");

        pnlComVacCentre.setBackground(new java.awt.Color(204, 153, 0));

        javax.swing.GroupLayout pnlComVacCentreLayout = new javax.swing.GroupLayout(pnlComVacCentre);
        pnlComVacCentre.setLayout(pnlComVacCentreLayout);
        pnlComVacCentreLayout.setHorizontalGroup(
            pnlComVacCentreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlComVacCentreLayout.createSequentialGroup()
                .addGap(0, 75, Short.MAX_VALUE)
                .addComponent(cboComVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnlComVacCentreLayout.setVerticalGroup(
            pnlComVacCentreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlComVacCentreLayout.createSequentialGroup()
                .addComponent(cboComVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel149.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel149.setForeground(new java.awt.Color(255, 255, 255));
        jLabel149.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel149.setText("Vaccination Centre:");

        jLabel150.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel150.setForeground(new java.awt.Color(255, 255, 255));
        jLabel150.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel150.setText("Status:");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel113, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtComCode, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel114, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(347, 347, 347))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel149, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnlComVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel138, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel139, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtComFName, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnComSave, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel140, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtComNewPw, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(txtComCfmPw, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel125, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel116, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel137, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel115, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel148, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel150, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(30, 30, 30)
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtComLName)
                                        .addGroup(jPanel7Layout.createSequentialGroup()
                                            .addComponent(rbComMale)
                                            .addGap(88, 88, 88)
                                            .addComponent(rbComFemale))
                                        .addComponent(calComDob, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                                        .addComponent(txtComContact)
                                        .addComponent(txtComEmail)
                                        .addComponent(cboComStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel135, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel136, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(122, 122, 122)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(calComHiredDate, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                                    .addComponent(cboComRole, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addContainerGap(40, Short.MAX_VALUE))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel113, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtComCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel135)
                    .addComponent(cboComRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel149, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlComVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel136)
                    .addComponent(calComHiredDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel114)
                    .addComponent(txtComFName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtComLName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel148))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel115)
                    .addComponent(rbComMale)
                    .addComponent(rbComFemale))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel116)
                    .addComponent(calComDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel125)
                    .addComponent(txtComContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel150)
                    .addComponent(cboComStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(73, 73, 73)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel137)
                        .addGap(11, 11, 11))
                    .addComponent(txtComEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtComNewPw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel138))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel140)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel139)
                    .addComponent(txtComCfmPw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addComponent(btnComSave, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jLabel149.getAccessibleContext().setAccessibleParent(pnlComVacCentre);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane12)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtMCSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMCSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboMCSearchRole, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboMCSearchStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboMCSearchVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnMCSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMCSearch)
                            .addComponent(cboMCSearchRole)
                            .addComponent(cboMCSearchVacCentre)
                            .addComponent(cboMCSearchStatus))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(432, Short.MAX_VALUE))
        );

        jScrollPane10.setViewportView(jPanel3);

        jTabbedPane2.addTab("Manage Committee", jScrollPane10);

        jTabbedPane1.addTab("Committee Management", jTabbedPane2);

        jTabbedPane4.setBackground(new java.awt.Color(51, 51, 51));
        jTabbedPane4.setOpaque(true);

        jPanel15.setBackground(new java.awt.Color(51, 51, 51));

        jPanel21.setBackground(new java.awt.Color(51, 51, 51));
        jPanel21.setPreferredSize(new java.awt.Dimension(1163, 1038));

        tblVM.setBackground(new java.awt.Color(255, 255, 255));
        tblVM.setForeground(new java.awt.Color(0, 0, 0));
        tblVM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Code", "Name", "Dose Count", "Interval"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblVM.setSelectionBackground(new java.awt.Color(51, 51, 51));
        tblVM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblVMKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblVMKeyTyped(evt);
            }
        });
        jScrollPane6.setViewportView(tblVM);

        jPanel17.setBackground(new java.awt.Color(255, 102, 255));
        jPanel17.setPreferredSize(new java.awt.Dimension(502, 449));

        jLabel95.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel95.setForeground(new java.awt.Color(0, 0, 0));
        jLabel95.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel95.setText("Name:");

        jLabel96.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel96.setForeground(new java.awt.Color(0, 0, 0));
        jLabel96.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel96.setText("Dose Count:");

        txtAnvDose.setBackground(new java.awt.Color(255, 255, 255));
        txtAnvDose.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAnvDose.setForeground(new java.awt.Color(0, 0, 0));
        txtAnvDose.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtAnvDose.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtAnvDose.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAnvDoseKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtAnvDoseKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtAnvDoseKeyTyped(evt);
            }
        });

        txtAnvName.setBackground(new java.awt.Color(255, 255, 255));
        txtAnvName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAnvName.setForeground(new java.awt.Color(0, 0, 0));
        txtAnvName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtAnvName.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnAnvAdd.setBackground(new java.awt.Color(0, 255, 0));
        btnAnvAdd.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnAnvAdd.setForeground(new java.awt.Color(0, 0, 0));
        btnAnvAdd.setText("Create");
        btnAnvAdd.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnAnvAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAnvAdd.setOpaque(true);
        btnAnvAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnvAddActionPerformed(evt);
            }
        });

        jLabel97.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel97.setForeground(new java.awt.Color(0, 0, 0));
        jLabel97.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel97.setText("Add New Vaccine");

        jLabel98.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel98.setForeground(new java.awt.Color(0, 0, 0));
        jLabel98.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel98.setText("Interval (days)");

        txtAnvInterval.setBackground(new java.awt.Color(255, 255, 255));
        txtAnvInterval.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAnvInterval.setForeground(new java.awt.Color(0, 0, 0));
        txtAnvInterval.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtAnvInterval.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtAnvInterval.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtAnvIntervalKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel97, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(7, 7, 7))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAnvAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jLabel98)
                        .addGap(71, 71, 71)
                        .addComponent(txtAnvInterval))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel96, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel95, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(89, 89, 89)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtAnvName, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAnvDose, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(86, 86, 86))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel97, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel95)
                    .addComponent(txtAnvName))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel96)
                    .addComponent(txtAnvDose, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel98, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtAnvInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68)
                .addComponent(btnAnvAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38))
        );

        txtVMSearch.setBackground(new java.awt.Color(255, 255, 255));
        txtVMSearch.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        txtVMSearch.setForeground(new java.awt.Color(0, 0, 0));
        txtVMSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtVMSearch.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnVMSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/search.png"))); // NOI18N
        btnVMSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnVMSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVMSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVMSearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(txtVMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnVMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30818, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnVMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 492, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(461, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE, Short.MAX_VALUE)
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane4.addTab("Vaccine Management", jPanel15);

        jPanel23.setBackground(new java.awt.Color(51, 51, 51));
        jPanel23.setPreferredSize(new java.awt.Dimension(1163, 1038));

        tblCM.setBackground(new java.awt.Color(255, 255, 255));
        tblCM.setForeground(new java.awt.Color(0, 0, 0));
        tblCM.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Code", "Name", "Unit No.", "Street", "City", "Postcode", "State"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCM.setSelectionBackground(new java.awt.Color(51, 51, 51));
        tblCM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCMMouseClicked(evt);
            }
        });
        tblCM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblCMKeyPressed(evt);
            }
        });
        jScrollPane7.setViewportView(tblCM);

        txtCMSearch.setBackground(new java.awt.Color(255, 255, 255));
        txtCMSearch.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        txtCMSearch.setForeground(new java.awt.Color(0, 0, 0));
        txtCMSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtCMSearch.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnCMSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/search.png"))); // NOI18N
        btnCMSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnCMSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCMSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCMSearchActionPerformed(evt);
            }
        });

        cboCMStateSearch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All States" }));
        cboCMStateSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboCMStateSearchActionPerformed(evt);
            }
        });

        jPanel24.setBackground(new java.awt.Color(255, 102, 255));
        jPanel24.setPreferredSize(new java.awt.Dimension(502, 449));

        jLabel117.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel117.setForeground(new java.awt.Color(0, 0, 0));
        jLabel117.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel117.setText("Name:");

        jLabel118.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel118.setForeground(new java.awt.Color(0, 0, 0));
        jLabel118.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel118.setText("No:");

        jLabel119.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel119.setForeground(new java.awt.Color(0, 0, 0));
        jLabel119.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel119.setText("Street:");

        txtCncStreet.setBackground(new java.awt.Color(255, 255, 255));
        txtCncStreet.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncStreet.setForeground(new java.awt.Color(0, 0, 0));
        txtCncStreet.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncStreet.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtCncNo.setBackground(new java.awt.Color(255, 255, 255));
        txtCncNo.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncNo.setForeground(new java.awt.Color(0, 0, 0));
        txtCncNo.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncNo.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtCncName.setBackground(new java.awt.Color(255, 255, 255));
        txtCncName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncName.setForeground(new java.awt.Color(0, 0, 0));
        txtCncName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncName.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnCncAdd.setBackground(new java.awt.Color(0, 255, 0));
        btnCncAdd.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnCncAdd.setForeground(new java.awt.Color(0, 0, 0));
        btnCncAdd.setText("Create");
        btnCncAdd.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnCncAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCncAdd.setOpaque(true);
        btnCncAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCncAddActionPerformed(evt);
            }
        });

        jLabel120.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel120.setForeground(new java.awt.Color(0, 0, 0));
        jLabel120.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel120.setText("Create New Centre");

        jLabel121.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel121.setForeground(new java.awt.Color(0, 0, 0));
        jLabel121.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel121.setText("Postcode:");

        jLabel122.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel122.setForeground(new java.awt.Color(0, 0, 0));
        jLabel122.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel122.setText("City:");

        txtCncPost.setBackground(new java.awt.Color(255, 255, 255));
        txtCncPost.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncPost.setForeground(new java.awt.Color(0, 0, 0));
        txtCncPost.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncPost.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtCncCity.setBackground(new java.awt.Color(255, 255, 255));
        txtCncCity.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncCity.setForeground(new java.awt.Color(0, 0, 0));
        txtCncCity.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncCity.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel123.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel123.setForeground(new java.awt.Color(0, 0, 0));
        jLabel123.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel123.setText("State:");

        jLabel124.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 24)); // NOI18N
        jLabel124.setForeground(new java.awt.Color(0, 0, 0));
        jLabel124.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel124.setText("Address");

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCncAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel120, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel117, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12)
                        .addComponent(txtCncName, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(7, 7, 7))
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator7)
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addComponent(jLabel123, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(cboCncState, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel24Layout.createSequentialGroup()
                                .addComponent(jLabel121)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel122, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCncCity, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCncPost, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel24Layout.createSequentialGroup()
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel119, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel118, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCncStreet, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCncNo, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1))
                    .addComponent(jLabel124, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel120, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCncName)
                    .addComponent(jLabel117))
                .addGap(18, 18, 18)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jLabel124)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel118)
                    .addComponent(txtCncNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCncStreet, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel119))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel122)
                    .addComponent(txtCncCity, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel121, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCncPost, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel123)
                    .addComponent(cboCncState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnCncAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66))
        );

        jSplitPane2.setLeftComponent(jPanel24);

        jPanel25.setBackground(new java.awt.Color(255, 102, 255));
        jPanel25.setPreferredSize(new java.awt.Dimension(502, 449));

        jLabel134.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel134.setForeground(new java.awt.Color(0, 0, 0));
        jLabel134.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel134.setText("Name:");

        jLabel141.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel141.setForeground(new java.awt.Color(0, 0, 0));
        jLabel141.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel141.setText("No:");

        jLabel142.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel142.setForeground(new java.awt.Color(0, 0, 0));
        jLabel142.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel142.setText("Street:");

        txtCncStreet1.setBackground(new java.awt.Color(255, 255, 255));
        txtCncStreet1.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncStreet1.setForeground(new java.awt.Color(0, 0, 0));
        txtCncStreet1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncStreet1.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtCncNo1.setBackground(new java.awt.Color(255, 255, 255));
        txtCncNo1.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncNo1.setForeground(new java.awt.Color(0, 0, 0));
        txtCncNo1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncNo1.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtCncName1.setBackground(new java.awt.Color(255, 255, 255));
        txtCncName1.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncName1.setForeground(new java.awt.Color(0, 0, 0));
        txtCncName1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncName1.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnCncAdd1.setBackground(new java.awt.Color(0, 255, 0));
        btnCncAdd1.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnCncAdd1.setForeground(new java.awt.Color(0, 0, 0));
        btnCncAdd1.setText("Save");
        btnCncAdd1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnCncAdd1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCncAdd1.setOpaque(true);
        btnCncAdd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCncAdd1ActionPerformed(evt);
            }
        });

        jLabel143.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel143.setForeground(new java.awt.Color(0, 0, 0));
        jLabel143.setText("Edit Vaccine Centre");

        jLabel144.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel144.setForeground(new java.awt.Color(0, 0, 0));
        jLabel144.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel144.setText("Postcode:");

        jLabel145.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel145.setForeground(new java.awt.Color(0, 0, 0));
        jLabel145.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel145.setText("City:");

        txtCncPost1.setBackground(new java.awt.Color(255, 255, 255));
        txtCncPost1.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncPost1.setForeground(new java.awt.Color(0, 0, 0));
        txtCncPost1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncPost1.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtCncCity1.setBackground(new java.awt.Color(255, 255, 255));
        txtCncCity1.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncCity1.setForeground(new java.awt.Color(0, 0, 0));
        txtCncCity1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncCity1.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel146.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel146.setForeground(new java.awt.Color(0, 0, 0));
        jLabel146.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel146.setText("State:");

        jLabel147.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 24)); // NOI18N
        jLabel147.setForeground(new java.awt.Color(0, 0, 0));
        jLabel147.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel147.setText("Address");

        txtCncCode.setBackground(new java.awt.Color(255, 255, 255));
        txtCncCode.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtCncCode.setForeground(new java.awt.Color(0, 0, 0));
        txtCncCode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtCncCode.setEnabled(false);
        txtCncCode.setSelectionColor(new java.awt.Color(255, 255, 51));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator8)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(jLabel146, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(cboCncState1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addComponent(jLabel144)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jLabel145, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCncCity1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCncPost1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel142, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel141, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCncStreet1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCncNo1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1))
                    .addComponent(jLabel147, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                        .addGap(0, 8, Short.MAX_VALUE)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCncAdd1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                                .addComponent(jLabel143, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(txtCncCode, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                        .addComponent(jLabel134, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(13, 13, 13)
                        .addComponent(txtCncName1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel143, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCncCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel134)
                    .addComponent(txtCncName1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel147)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel141)
                    .addComponent(txtCncNo1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCncStreet1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel142))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel145)
                    .addComponent(txtCncCity1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel144, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtCncPost1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel146)
                    .addComponent(cboCncState1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnCncAdd1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63))
        );

        jSplitPane2.setRightComponent(jPanel25);

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(txtCMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboCMStateSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addContainerGap(27, Short.MAX_VALUE)
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 906, Short.MAX_VALUE)
                            .addComponent(jScrollPane7))))
                .addContainerGap(4690, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCMSearch)
                    .addComponent(cboCMStateSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSplitPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(642, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, 5623, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 26223, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, 1447, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jTabbedPane4.addTab("Centre Management", jPanel14);

        jScrollPane2.setViewportView(jTabbedPane4);

        jTabbedPane1.addTab("Vaccine & Centre Management", jScrollPane2);

        jScrollPane11.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane11.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jTabbedPane5.setBackground(new java.awt.Color(51, 51, 51));
        jTabbedPane5.setOpaque(true);

        jPanel18.setBackground(new java.awt.Color(51, 51, 51));
        jPanel18.setPreferredSize(new java.awt.Dimension(1221, 900));

        tblMA.setBackground(new java.awt.Color(255, 255, 255));
        tblMA.setForeground(new java.awt.Color(0, 0, 0));
        tblMA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Code", "Registrant", "IC / Passport", "Reg. Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMA.setSelectionBackground(new java.awt.Color(51, 51, 51));
        tblMA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMAMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMA);

        pnlApprovedAppointment.setBackground(new java.awt.Color(255, 204, 51));
        pnlApprovedAppointment.setEnabled(false);

        jLabel63.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel63.setForeground(new java.awt.Color(0, 0, 0));
        jLabel63.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel63.setText("Vaccination Centre:");

        jLabel61.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(0, 0, 0));
        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel61.setText("Vaccination Date:");

        calMaVacDate.setBackground(new java.awt.Color(255, 255, 255));
        calMaVacDate.setForeground(new java.awt.Color(0, 0, 0));

        jLabel62.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(0, 0, 0));
        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel62.setText("Vaccine:");

        jLabel60.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(0, 0, 0));
        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel60.setText("Remarks:");

        txtMaRemarks.setBackground(new java.awt.Color(255, 255, 255));
        txtMaRemarks.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaRemarks.setForeground(new java.awt.Color(0, 0, 0));
        txtMaRemarks.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtMaRemarks.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtMaRemarks.setOpaque(true);
        txtMaRemarks.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnMaSubmit.setBackground(new java.awt.Color(102, 255, 102));
        btnMaSubmit.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnMaSubmit.setForeground(new java.awt.Color(0, 0, 0));
        btnMaSubmit.setText("Submit");
        btnMaSubmit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnMaSubmit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMaSubmit.setOpaque(true);
        btnMaSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaSubmitActionPerformed(evt);
            }
        });

        jLabel65.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel65.setForeground(new java.awt.Color(0, 0, 0));
        jLabel65.setText("Appointment Schedule");

        cboMaVacCentre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMaVacCentreActionPerformed(evt);
            }
        });

        txtMaVacAdd.setEditable(false);
        txtMaVacAdd.setBackground(new java.awt.Color(255, 255, 255));
        txtMaVacAdd.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaVacAdd.setForeground(new java.awt.Color(0, 0, 0));
        txtMaVacAdd.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtMaVacAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtMaVacAdd.setEnabled(false);
        txtMaVacAdd.setOpaque(true);
        txtMaVacAdd.setSelectionColor(new java.awt.Color(255, 255, 51));

        javax.swing.GroupLayout pnlApprovedAppointmentLayout = new javax.swing.GroupLayout(pnlApprovedAppointment);
        pnlApprovedAppointment.setLayout(pnlApprovedAppointmentLayout);
        pnlApprovedAppointmentLayout.setHorizontalGroup(
            pnlApprovedAppointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlApprovedAppointmentLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pnlApprovedAppointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel65, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlApprovedAppointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnMaSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlApprovedAppointmentLayout.createSequentialGroup()
                            .addGroup(pnlApprovedAppointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel62, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel61, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel63, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlApprovedAppointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cboMaVac, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(calMaVacDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                .addComponent(cboMaVacCentre, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtMaVacAdd, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtMaRemarks, javax.swing.GroupLayout.Alignment.LEADING)))))
                .addContainerGap(11, Short.MAX_VALUE))
        );
        pnlApprovedAppointmentLayout.setVerticalGroup(
            pnlApprovedAppointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlApprovedAppointmentLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel65)
                .addGap(26, 26, 26)
                .addGroup(pnlApprovedAppointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMaRemarks, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel60))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlApprovedAppointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel61)
                    .addComponent(calMaVacDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(pnlApprovedAppointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(cboMaVac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlApprovedAppointmentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(cboMaVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaVacAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMaSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(51, 153, 255));

        jLabel10.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText("Username:");

        jLabel11.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 0, 0));
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText("Full Name:");

        jLabel12.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 0, 0));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText("IC/Passport:");

        txtMaAppCode.setEditable(false);
        txtMaAppCode.setBackground(new java.awt.Color(204, 204, 204));
        txtMaAppCode.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaAppCode.setForeground(new java.awt.Color(0, 0, 0));
        txtMaAppCode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMaAppCode.setEnabled(false);
        txtMaAppCode.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtMaIC.setEditable(false);
        txtMaIC.setBackground(new java.awt.Color(204, 204, 204));
        txtMaIC.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaIC.setForeground(new java.awt.Color(0, 0, 0));
        txtMaIC.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMaIC.setEnabled(false);
        txtMaIC.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtMaFullname.setEditable(false);
        txtMaFullname.setBackground(new java.awt.Color(204, 204, 204));
        txtMaFullname.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaFullname.setForeground(new java.awt.Color(0, 0, 0));
        txtMaFullname.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMaFullname.setEnabled(false);
        txtMaFullname.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtMaUsername.setEditable(false);
        txtMaUsername.setBackground(new java.awt.Color(204, 204, 204));
        txtMaUsername.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaUsername.setForeground(new java.awt.Color(0, 0, 0));
        txtMaUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMaUsername.setEnabled(false);
        txtMaUsername.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnMaApprove.setBackground(new java.awt.Color(102, 255, 102));
        btnMaApprove.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnMaApprove.setForeground(new java.awt.Color(0, 0, 0));
        btnMaApprove.setText("Approve");
        btnMaApprove.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnMaApprove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMaApprove.setEnabled(false);
        btnMaApprove.setOpaque(true);
        btnMaApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaApproveActionPerformed(evt);
            }
        });

        btnMaDecline.setBackground(new java.awt.Color(255, 51, 51));
        btnMaDecline.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnMaDecline.setForeground(new java.awt.Color(0, 0, 0));
        btnMaDecline.setText("Decline");
        btnMaDecline.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnMaDecline.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMaDecline.setEnabled(false);
        btnMaDecline.setOpaque(true);
        btnMaDecline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaDeclineActionPerformed(evt);
            }
        });

        jLabel64.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel64.setForeground(new java.awt.Color(0, 0, 0));
        jLabel64.setText("Registrant Info.");

        jLabel59.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(0, 0, 0));
        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel59.setText("Gender:");

        txtMaGender.setEditable(false);
        txtMaGender.setBackground(new java.awt.Color(204, 204, 204));
        txtMaGender.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaGender.setForeground(new java.awt.Color(0, 0, 0));
        txtMaGender.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMaGender.setEnabled(false);
        txtMaGender.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel86.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel86.setForeground(new java.awt.Color(0, 0, 0));
        jLabel86.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel86.setText("Date of Birth:");

        txtMaNat.setEditable(false);
        txtMaNat.setBackground(new java.awt.Color(204, 204, 204));
        txtMaNat.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaNat.setForeground(new java.awt.Color(0, 0, 0));
        txtMaNat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMaNat.setEnabled(false);
        txtMaNat.setSelectionColor(new java.awt.Color(255, 255, 51));

        calMaDob.setBackground(new java.awt.Color(255, 255, 255));
        calMaDob.setForeground(new java.awt.Color(0, 0, 0));
        calMaDob.setEnabled(false);

        jLabel89.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel89.setForeground(new java.awt.Color(0, 0, 0));
        jLabel89.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel89.setText("Address:");

        txtMaAddress.setEditable(false);
        txtMaAddress.setBackground(new java.awt.Color(204, 204, 204));
        txtMaAddress.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaAddress.setForeground(new java.awt.Color(0, 0, 0));
        txtMaAddress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMaAddress.setEnabled(false);
        txtMaAddress.setSelectionColor(new java.awt.Color(255, 255, 51));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(txtMaIC, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtMaNat))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel64)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtMaAppCode, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(60, 60, 60)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtMaUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMaFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel59, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60)
                        .addComponent(txtMaGender, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel86, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(calMaDob, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtMaAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(btnMaApprove, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnMaDecline, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel64, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaAppCode, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtMaUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtMaFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59)
                    .addComponent(txtMaGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(calMaDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel86))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel89)
                    .addComponent(txtMaAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtMaIC, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaNat, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMaApprove, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMaDecline, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        txtMaSearch.setBackground(new java.awt.Color(255, 255, 255));
        txtMaSearch.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        txtMaSearch.setForeground(new java.awt.Color(0, 0, 0));
        txtMaSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtMaSearch.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtMaSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMaSearchActionPerformed(evt);
            }
        });

        btnMASearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/search.png"))); // NOI18N
        btnMASearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnMASearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMASearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnMASearchMouseClicked(evt);
            }
        });
        btnMASearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMASearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(txtMaSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMASearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pnlApprovedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 32129, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnMASearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaSearch))
                .addGap(24, 24, 24)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(pnlApprovedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 514, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(587, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Schedule Appointment", jPanel18);

        jPanel12.setBackground(new java.awt.Color(51, 51, 51));

        txtVSearch.setBackground(new java.awt.Color(255, 255, 255));
        txtVSearch.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        txtVSearch.setForeground(new java.awt.Color(0, 0, 0));
        txtVSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtVSearch.setToolTipText("Appoinment Code or Registrant's Username / Name / IC / Passport or Remarks or Admin's Personnel Code or Doctor's Personnel Code");
        txtVSearch.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnVSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/search.png"))); // NOI18N
        btnVSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnVSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVSearchActionPerformed(evt);
            }
        });

        tblV.setBackground(new java.awt.Color(255, 255, 255));
        tblV.setForeground(new java.awt.Color(0, 0, 0));
        tblV.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Code", "Registrant", "IC/Passport", "Reg. Date", "Vaccine ", "Vac. Centre", "Vac. Date", "Remarks", "Handled By", "Vaccinated By", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblV.setSelectionBackground(new java.awt.Color(51, 51, 51));
        jScrollPane4.setViewportView(tblV);

        cboVSearchNat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Nationality" }));
        cboVSearchNat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboVSearchNatActionPerformed(evt);
            }
        });

        cboVSearchVac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboVSearchVacActionPerformed(evt);
            }
        });

        cboVSearchStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Status" }));
        cboVSearchStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboVSearchStatusActionPerformed(evt);
            }
        });

        cboVSearchVacCentre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboVSearchVacCentreActionPerformed(evt);
            }
        });

        calVSearchVacDate.setToolTipText("Vaccination Date");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(txtVSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(calVSearchVacDate, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnVSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboVSearchNat, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboVSearchVac, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboVSearchVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboVSearchStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30756, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnVSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVSearch)
                    .addComponent(calVSearchVacDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboVSearchNat)
                    .addComponent(cboVSearchVac)
                    .addComponent(cboVSearchStatus)
                    .addComponent(cboVSearchVacCentre))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(717, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("View Appointment", jPanel12);

        jPanel20.setBackground(new java.awt.Color(51, 51, 51));

        tblRA.setBackground(new java.awt.Color(255, 255, 255));
        tblRA.setForeground(new java.awt.Color(0, 0, 0));
        tblRA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Code", "Registrant", "Reg. Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRA.setSelectionBackground(new java.awt.Color(51, 51, 51));
        tblRA.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblRAMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tblRA);

        txtRaSearch.setBackground(new java.awt.Color(255, 255, 255));
        txtRaSearch.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        txtRaSearch.setForeground(new java.awt.Color(0, 0, 0));
        txtRaSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaSearch.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnRASearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/search.png"))); // NOI18N
        btnRASearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnRASearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRASearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRASearchActionPerformed(evt);
            }
        });

        jPanel19.setBackground(new java.awt.Color(51, 153, 255));

        jLabel13.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 0, 0));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Username:");

        jLabel15.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(0, 0, 0));
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel15.setText("Full Name:");

        jLabel66.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel66.setForeground(new java.awt.Color(0, 0, 0));
        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel66.setText("IC/Passport:");

        txtRaAppCode.setEditable(false);
        txtRaAppCode.setBackground(new java.awt.Color(204, 204, 204));
        txtRaAppCode.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaAppCode.setForeground(new java.awt.Color(0, 0, 0));
        txtRaAppCode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaAppCode.setEnabled(false);
        txtRaAppCode.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtRaIC.setEditable(false);
        txtRaIC.setBackground(new java.awt.Color(204, 204, 204));
        txtRaIC.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaIC.setForeground(new java.awt.Color(0, 0, 0));
        txtRaIC.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaIC.setEnabled(false);
        txtRaIC.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtRaFullname.setEditable(false);
        txtRaFullname.setBackground(new java.awt.Color(204, 204, 204));
        txtRaFullname.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaFullname.setForeground(new java.awt.Color(0, 0, 0));
        txtRaFullname.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaFullname.setEnabled(false);
        txtRaFullname.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtRaUsername.setEditable(false);
        txtRaUsername.setBackground(new java.awt.Color(204, 204, 204));
        txtRaUsername.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaUsername.setForeground(new java.awt.Color(0, 0, 0));
        txtRaUsername.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaUsername.setEnabled(false);
        txtRaUsername.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel87.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel87.setForeground(new java.awt.Color(0, 0, 0));
        jLabel87.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel87.setText("Gender:");

        txtRaGender.setEditable(false);
        txtRaGender.setBackground(new java.awt.Color(204, 204, 204));
        txtRaGender.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaGender.setForeground(new java.awt.Color(0, 0, 0));
        txtRaGender.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaGender.setEnabled(false);
        txtRaGender.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel90.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel90.setForeground(new java.awt.Color(0, 0, 0));
        jLabel90.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel90.setText("Date of Birth:");

        txtRaNat.setEditable(false);
        txtRaNat.setBackground(new java.awt.Color(204, 204, 204));
        txtRaNat.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaNat.setForeground(new java.awt.Color(0, 0, 0));
        txtRaNat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaNat.setEnabled(false);
        txtRaNat.setSelectionColor(new java.awt.Color(255, 255, 51));

        calRaDob.setBackground(new java.awt.Color(255, 255, 255));
        calRaDob.setForeground(new java.awt.Color(0, 0, 0));
        calRaDob.setEnabled(false);

        jLabel91.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel91.setForeground(new java.awt.Color(0, 0, 0));
        jLabel91.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel91.setText("Address:");

        txtRaAddress.setEditable(false);
        txtRaAddress.setBackground(new java.awt.Color(204, 204, 204));
        txtRaAddress.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaAddress.setForeground(new java.awt.Color(0, 0, 0));
        txtRaAddress.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaAddress.setEnabled(false);
        txtRaAddress.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel92.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel92.setForeground(new java.awt.Color(0, 0, 0));
        jLabel92.setText("Registrant Info.");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jLabel66, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(60, 60, 60)
                                .addComponent(txtRaIC, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRaNat))
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(60, 60, 60)
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtRaUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtRaFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jLabel87, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(60, 60, 60)
                                .addComponent(txtRaGender, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jLabel90, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(calRaDob, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addComponent(jLabel91, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtRaAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 321, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 27, Short.MAX_VALUE))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtRaAppCode, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jLabel92, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(txtRaAppCode, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtRaUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtRaFullname, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel87)
                    .addComponent(txtRaGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(calRaDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel90))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel91)
                    .addComponent(txtRaAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel66)
                    .addComponent(txtRaIC, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRaNat, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jSplitPane1.setMinimumSize(new java.awt.Dimension(4, 1));

        pnlApprovedAppointment2.setBackground(new java.awt.Color(255, 204, 51));
        pnlApprovedAppointment2.setEnabled(false);

        jLabel72.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(0, 0, 0));
        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel72.setText("Vaccination Centre:");

        jLabel73.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(0, 0, 0));
        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel73.setText("Vaccination Date:");

        calMaVacDate2.setBackground(new java.awt.Color(255, 255, 255));
        calMaVacDate2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel74.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel74.setForeground(new java.awt.Color(0, 0, 0));
        jLabel74.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel74.setText("Vaccine:");

        jLabel75.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel75.setForeground(new java.awt.Color(0, 0, 0));
        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel75.setText("Remarks:");

        txtMaRemarks2.setBackground(new java.awt.Color(255, 255, 255));
        txtMaRemarks2.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaRemarks2.setForeground(new java.awt.Color(0, 0, 0));
        txtMaRemarks2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtMaRemarks2.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtMaRemarks2.setOpaque(true);
        txtMaRemarks2.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnMaSubmit3.setBackground(new java.awt.Color(102, 255, 102));
        btnMaSubmit3.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnMaSubmit3.setForeground(new java.awt.Color(0, 0, 0));
        btnMaSubmit3.setText("Submit");
        btnMaSubmit3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnMaSubmit3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMaSubmit3.setOpaque(true);
        btnMaSubmit3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaSubmit3ActionPerformed(evt);
            }
        });

        jLabel76.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(0, 0, 0));
        jLabel76.setText("Appointment Schedule");

        cboMaVacCentre2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMaVacCentre2ActionPerformed(evt);
            }
        });

        txtMaVacAdd2.setEditable(false);
        txtMaVacAdd2.setBackground(new java.awt.Color(255, 255, 255));
        txtMaVacAdd2.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMaVacAdd2.setForeground(new java.awt.Color(0, 0, 0));
        txtMaVacAdd2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtMaVacAdd2.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtMaVacAdd2.setEnabled(false);
        txtMaVacAdd2.setOpaque(true);
        txtMaVacAdd2.setSelectionColor(new java.awt.Color(255, 255, 51));

        javax.swing.GroupLayout pnlApprovedAppointment2Layout = new javax.swing.GroupLayout(pnlApprovedAppointment2);
        pnlApprovedAppointment2.setLayout(pnlApprovedAppointment2Layout);
        pnlApprovedAppointment2Layout.setHorizontalGroup(
            pnlApprovedAppointment2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlApprovedAppointment2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pnlApprovedAppointment2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlApprovedAppointment2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnMaSubmit3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(pnlApprovedAppointment2Layout.createSequentialGroup()
                            .addGroup(pnlApprovedAppointment2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel75, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel74, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel73, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel72, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(pnlApprovedAppointment2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cboMaVac2, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(calMaVacDate2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                                .addComponent(cboMaVacCentre2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtMaVacAdd2, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtMaRemarks2, javax.swing.GroupLayout.Alignment.LEADING)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlApprovedAppointment2Layout.setVerticalGroup(
            pnlApprovedAppointment2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlApprovedAppointment2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel76)
                .addGap(26, 26, 26)
                .addGroup(pnlApprovedAppointment2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMaRemarks2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel75))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlApprovedAppointment2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel73)
                    .addComponent(calMaVacDate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(pnlApprovedAppointment2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel74)
                    .addComponent(cboMaVac2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlApprovedAppointment2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel72)
                    .addComponent(cboMaVacCentre2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtMaVacAdd2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnMaSubmit3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(pnlApprovedAppointment2);

        pnlApprovedAppointment4.setBackground(new java.awt.Color(255, 204, 51));
        pnlApprovedAppointment4.setEnabled(false);
        pnlApprovedAppointment4.setPreferredSize(new java.awt.Dimension(500, 380));

        jLabel102.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel102.setForeground(new java.awt.Color(0, 0, 0));
        jLabel102.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel102.setText("Vaccination Centre:");

        jLabel103.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel103.setForeground(new java.awt.Color(0, 0, 0));
        jLabel103.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel103.setText("Vaccination Date:");

        calRaVacDate.setBackground(new java.awt.Color(255, 255, 255));
        calRaVacDate.setForeground(new java.awt.Color(0, 0, 0));
        calRaVacDate.setEnabled(false);

        jLabel104.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel104.setForeground(new java.awt.Color(0, 0, 0));
        jLabel104.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel104.setText("Vaccine:");

        jLabel105.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel105.setForeground(new java.awt.Color(0, 0, 0));
        jLabel105.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel105.setText("Remarks:");

        txtRaRemarks.setEditable(false);
        txtRaRemarks.setBackground(new java.awt.Color(255, 255, 255));
        txtRaRemarks.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaRemarks.setForeground(new java.awt.Color(0, 0, 0));
        txtRaRemarks.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaRemarks.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRaRemarks.setEnabled(false);
        txtRaRemarks.setOpaque(true);
        txtRaRemarks.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel106.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel106.setForeground(new java.awt.Color(0, 0, 0));
        jLabel106.setText("Schedule Info.");

        txtRaVacAdd.setEditable(false);
        txtRaVacAdd.setBackground(new java.awt.Color(255, 255, 255));
        txtRaVacAdd.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaVacAdd.setForeground(new java.awt.Color(0, 0, 0));
        txtRaVacAdd.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaVacAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRaVacAdd.setEnabled(false);
        txtRaVacAdd.setOpaque(true);
        txtRaVacAdd.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtRaVac.setEditable(false);
        txtRaVac.setBackground(new java.awt.Color(255, 255, 255));
        txtRaVac.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaVac.setForeground(new java.awt.Color(0, 0, 0));
        txtRaVac.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaVac.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRaVac.setEnabled(false);
        txtRaVac.setOpaque(true);
        txtRaVac.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtRaVacCentre.setEditable(false);
        txtRaVacCentre.setBackground(new java.awt.Color(255, 255, 255));
        txtRaVacCentre.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaVacCentre.setForeground(new java.awt.Color(0, 0, 0));
        txtRaVacCentre.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaVacCentre.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRaVacCentre.setEnabled(false);
        txtRaVacCentre.setOpaque(true);
        txtRaVacCentre.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtRaReason.setEditable(false);
        txtRaReason.setBackground(new java.awt.Color(255, 255, 255));
        txtRaReason.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaReason.setForeground(new java.awt.Color(0, 0, 0));
        txtRaReason.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaReason.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRaReason.setEnabled(false);
        txtRaReason.setOpaque(true);
        txtRaReason.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel112.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel112.setForeground(new java.awt.Color(0, 0, 0));
        jLabel112.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel112.setText("Reject Reason:");

        javax.swing.GroupLayout pnlApprovedAppointment4Layout = new javax.swing.GroupLayout(pnlApprovedAppointment4);
        pnlApprovedAppointment4.setLayout(pnlApprovedAppointment4Layout);
        pnlApprovedAppointment4Layout.setHorizontalGroup(
            pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlApprovedAppointment4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel106, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlApprovedAppointment4Layout.createSequentialGroup()
                        .addGroup(pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel102, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel104, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel103, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                                .addComponent(jLabel105, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel112, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addGroup(pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtRaVacAdd)
                            .addComponent(txtRaVacCentre, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRaVac, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(calRaVacDate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                            .addComponent(txtRaRemarks, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRaReason)))))
        );
        pnlApprovedAppointment4Layout.setVerticalGroup(
            pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlApprovedAppointment4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel106)
                .addGap(26, 26, 26)
                .addGroup(pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRaRemarks, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel105))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel103)
                    .addComponent(calRaVacDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel104)
                    .addComponent(txtRaVac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel102)
                    .addComponent(txtRaVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRaVacAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlApprovedAppointment4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRaReason, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel112))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(pnlApprovedAppointment4);

        pnlApprovedAppointment5.setBackground(new java.awt.Color(255, 204, 51));
        pnlApprovedAppointment5.setEnabled(false);

        btnRaSubmit.setBackground(new java.awt.Color(102, 255, 102));
        btnRaSubmit.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnRaSubmit.setForeground(new java.awt.Color(0, 0, 0));
        btnRaSubmit.setText("Reschedule");
        btnRaSubmit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnRaSubmit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRaSubmit.setOpaque(true);
        btnRaSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRaSubmitActionPerformed(evt);
            }
        });

        jLabel111.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel111.setForeground(new java.awt.Color(0, 0, 0));
        jLabel111.setText("New Schedule");

        jLabel107.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel107.setForeground(new java.awt.Color(0, 0, 0));
        jLabel107.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel107.setText("Remarks:");

        jLabel108.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel108.setForeground(new java.awt.Color(0, 0, 0));
        jLabel108.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel108.setText("Vaccination Date:");

        jLabel109.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel109.setForeground(new java.awt.Color(0, 0, 0));
        jLabel109.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel109.setText("Vaccine:");

        jLabel110.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel110.setForeground(new java.awt.Color(0, 0, 0));
        jLabel110.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel110.setText("Vaccination Centre:");

        txtRaRemarks2.setBackground(new java.awt.Color(255, 255, 255));
        txtRaRemarks2.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaRemarks2.setForeground(new java.awt.Color(0, 0, 0));
        txtRaRemarks2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaRemarks2.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRaRemarks2.setOpaque(true);
        txtRaRemarks2.setSelectionColor(new java.awt.Color(255, 255, 51));

        calRaVacDate2.setBackground(new java.awt.Color(255, 255, 255));
        calRaVacDate2.setForeground(new java.awt.Color(0, 0, 0));

        cboRaVacCentre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboRaVacCentreActionPerformed(evt);
            }
        });

        txtRaVacAdd2.setEditable(false);
        txtRaVacAdd2.setBackground(new java.awt.Color(255, 255, 255));
        txtRaVacAdd2.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaVacAdd2.setForeground(new java.awt.Color(0, 0, 0));
        txtRaVacAdd2.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaVacAdd2.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRaVacAdd2.setEnabled(false);
        txtRaVacAdd2.setOpaque(true);
        txtRaVacAdd2.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnRaCancel.setBackground(new java.awt.Color(255, 51, 51));
        btnRaCancel.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnRaCancel.setForeground(new java.awt.Color(0, 0, 0));
        btnRaCancel.setText("Cancel");
        btnRaCancel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnRaCancel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRaCancel.setOpaque(true);
        btnRaCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRaCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlApprovedAppointment5Layout = new javax.swing.GroupLayout(pnlApprovedAppointment5);
        pnlApprovedAppointment5.setLayout(pnlApprovedAppointment5Layout);
        pnlApprovedAppointment5Layout.setHorizontalGroup(
            pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlApprovedAppointment5Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel111, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlApprovedAppointment5Layout.createSequentialGroup()
                            .addGroup(pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel110, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel109, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel108, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel107, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtRaRemarks2)
                                .addComponent(calRaVacDate2, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                                .addComponent(cboRaVacCentre, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cboRaVac, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtRaVacAdd2))))
                    .addGroup(pnlApprovedAppointment5Layout.createSequentialGroup()
                        .addComponent(btnRaSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRaCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        pnlApprovedAppointment5Layout.setVerticalGroup(
            pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlApprovedAppointment5Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlApprovedAppointment5Layout.createSequentialGroup()
                        .addComponent(jLabel111)
                        .addGap(18, 18, 18)
                        .addGroup(pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel107)
                            .addComponent(txtRaRemarks2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel108))
                    .addComponent(calRaVacDate2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboRaVac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel109))
                .addGap(18, 18, 18)
                .addGroup(pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboRaVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel110))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtRaVacAdd2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addGroup(pnlApprovedAppointment5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRaSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRaCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25))
        );

        jSplitPane1.setRightComponent(pnlApprovedAppointment5);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(txtRaSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnRASearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 991, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(298, 298, 298)
                .addComponent(jSeparator2, javax.swing.GroupLayout.DEFAULT_SIZE, 31816, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnRASearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRaSearch))
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGap(370, 370, 370)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(690, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Reschedule Appointment", jPanel20);

        jScrollPane11.setViewportView(jTabbedPane5);

        jTabbedPane1.addTab("Appointment Management", jScrollPane11);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1341, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1238, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        this.setVisible(false);
        this.dispose();
        Login lg = new Login();
        lg.setVisible(true);
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void cboCMStateSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboCMStateSearchActionPerformed
        String search = "";

        if (!txtCMSearch.getText().isBlank()) {
            search = txtCMSearch.getText();
        }

        search = search.trim().toLowerCase();

        String state = "";

        if (cboCMStateSearch.getSelectedIndex() > 0) {
            state = String.valueOf(cboCMStateSearch.getSelectedItem());
        }

        CMSearch(search, state);
    }//GEN-LAST:event_cboCMStateSearchActionPerformed

    private void btnCncAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCncAddActionPerformed
        // TODO add your handling code here:
        if (General.AlertQuestionYesNo("Do you want to add new vaccine centre?", "New Vaccine Centre Confirmation") == 1) {
            return;
        }

        if (txtCncName.getText().isBlank() || txtCncNo.getText().isBlank() || txtCncStreet.getText().isBlank() || txtCncCity.getText().isBlank() || txtCncPost.getText().isBlank() || cboCncState.getSelectedIndex() < 0) {
            General.AlertMsgError("All vaccine centre details must be filled.", "Error");
            return;
        }

        Address newVacCentreAdd = new Address(txtCncNo.getText(), txtCncStreet.getText(), txtCncCity.getText(), txtCncPost.getText(), String.valueOf(cboCncState.getSelectedItem()));
        VaccineCentre newVac = new VaccineCentre(txtCncName.getText(), newVacCentreAdd);

        if (FileOperation.SerializeObject(General.vaccineCentreFileName, newVac)) {
            General.AlertMsgInfo("New vaccine centre created!", "Success");
            CncClear();
            InitGlobalData();
            InitComboData();
            InitTableRecords();
        } else {
            General.AlertMsgError("Vaccine centre was not created. Please try again later!", "Error");
        }
    }//GEN-LAST:event_btnCncAddActionPerformed

    private void btnCMSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCMSearchActionPerformed
        String search = "";

        if (!txtCMSearch.getText().isBlank()) {
            search = txtCMSearch.getText();
        }

        search = search.trim().toLowerCase();

        String state = "";

        if (cboCMStateSearch.getSelectedIndex() > 0) {
            state = String.valueOf(cboCMStateSearch.getSelectedItem());
        }

        CMSearch(search, state);
    }//GEN-LAST:event_btnCMSearchActionPerformed

    private void tblCMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCMKeyPressed

    }//GEN-LAST:event_tblCMKeyPressed

    private void btnVMSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVMSearchActionPerformed
        // TODO add your handling code here:
        String search = "";
        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblVM.getModel();
        dtm.setRowCount(0);

        if (!txtVMSearch.getText().isBlank()) {
            search = txtVMSearch.getText();
        }

        search = search.trim().toLowerCase();

        for (Object x : htVac.values()) {
            Vaccine a = (Vaccine) x;

            if (a.getVacCode().toLowerCase().contains(search) || a.getName().toLowerCase().contains(search)) {

                Object[] dtmObj = new Object[]{
                    ++i,
                    a.getVacCode(),
                    a.getName(),
                    a.getDoseCount(),
                    a.getInterval()
                };

                dtm.addRow(dtmObj);
            }
        }

        tblVM.setModel(dtm);
    }//GEN-LAST:event_btnVMSearchActionPerformed

    private void txtAnvIntervalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAnvIntervalKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        }
    }//GEN-LAST:event_txtAnvIntervalKeyTyped

    private void btnAnvAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnvAddActionPerformed

        if (General.AlertQuestionYesNo("Do you want to add new vaccine?", "New Vaccine Confirmation") == 1) {
            return;
        }

        if (txtAnvName.getText().isBlank() || txtAnvDose.getText().isBlank() || txtAnvInterval.getText().isBlank()) {
            General.AlertMsgError("All vaccine details must be filled.", "Error");
            return;
        }

        Vaccine newVac = new Vaccine(txtAnvName.getText(), Integer.parseInt(txtAnvDose.getText()), Integer.parseInt(txtAnvInterval.getText()));

        if (FileOperation.SerializeObject(General.vaccineFileName, newVac)) {
            General.AlertMsgInfo("New vaccine created!", "Success");
            AnvClear();
            InitGlobalData();
            InitComboData();
            InitTableRecords();
        } else {
            General.AlertMsgError("Vaccine was not created. Please try again later!", "Error");
        }
    }//GEN-LAST:event_btnAnvAddActionPerformed

    private void txtAnvDoseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAnvDoseKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        }
    }//GEN-LAST:event_txtAnvDoseKeyTyped

    private void txtAnvDoseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAnvDoseKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAnvDoseKeyReleased

    private void txtAnvDoseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAnvDoseKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAnvDoseKeyPressed

    private void tblVMKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVMKeyTyped

    }//GEN-LAST:event_tblVMKeyTyped

    private void tblVMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVMKeyPressed

    }//GEN-LAST:event_tblVMKeyPressed

    private void tblPpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPpKeyPressed

    }//GEN-LAST:event_tblPpKeyPressed

    private void btnRaCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRaCancelActionPerformed

        if (General.AlertQuestionYesNo("You can't reschedule the appointment once you cancelled. Are you sure to cancel the appointment?", "Cancel Confirmation") == 0) {
            String code = txtRaAppCode.getText();

            FileOperation fo = new FileOperation(code, General.appointmentFileName);
            fo.ReadFile();
            Appointment app = (Appointment) fo.getReadResult();

            app.setStatus(AppointmentStatus.Cancelled);
            fo.ModifyRecord(app);
            InitTableRecords();
            btnRaSubmit.setEnabled(false);
            btnRaCancel.setEnabled(false);
        }
    }//GEN-LAST:event_btnRaCancelActionPerformed

    private void cboRaVacCentreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboRaVacCentreActionPerformed
        // TODO add your handling code here:
        if (cboRaVacCentre.getSelectedIndex() != -1) {
            String vacCentre = String.valueOf(cboRaVacCentre.getSelectedItem()).split(" - ")[0];
            VaccineCentre vc = (VaccineCentre) htVacCentre.get(vacCentre);
            txtRaVacAdd2.setText(vc.VacAddress.getFullAddress());
        } else {
            txtRaVacAdd2.setText("");
        }
    }//GEN-LAST:event_cboRaVacCentreActionPerformed

    private void btnRaSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRaSubmitActionPerformed
        if (General.AlertQuestionYesNo("A new appointment will be created after rescheduling. Do you want to proceed?", "Reschedule Confirmation") == 0) {
            String code = txtRaAppCode.getText();

            FileOperation fo = new FileOperation(code, General.appointmentFileName);
            fo.ReadFile();
            Appointment app = (Appointment) fo.getReadResult();
            app.setStatus(AppointmentStatus.Rescheduled);
            fo.ModifyRecord(app);

            //New app
            Appointment newApp = new Appointment(app.Ppl);

            String remarks = txtRaRemarks2.getText();
            MyDateTime vacDate = new MyDateTime(calRaVacDate2.getCalendar());

            Object[] vacArray = (Object[]) htVac.values().toArray();
            Vaccine vac = (Vaccine) vacArray[cboRaVac.getSelectedIndex()];

            String vacCentreCode = String.valueOf(cboRaVacCentre.getSelectedItem()).split(" - ")[0];
            VaccineCentre vacCentre = (VaccineCentre) htVacCentre.get(vacCentreCode);

            //Update appointment
            newApp.setLocation(vacCentre);
            newApp.setRemarks(remarks);
            newApp.setVacc(vac);
            newApp.setVaccinationDate(vacDate);
            newApp.setStatus(AppointmentStatus.Approved);
            newApp.setHandledBy(currentUser);
            newApp.setStatus(AppointmentStatus.Approved);

            //Update pending stock
            Stock s = new Stock(app.Vacc, app.CheckDoseFromAppointment(), app.Location);
            if (s.FindStock()) {

                if (s.MinusPendingQty(1, currentUser, "Reschedule Vaccination - " + app.getCode())) {
                    General.AlertMsgError("Something went wrong, please try again later!", "Error");
                    return;
                }

            } else {
                s.GenerateId();
                s.MinusPendingQty(1, currentUser, "Reschedule Vaccination - " + app.getCode());
                FileOperation.SerializeObject(General.stockFileName, s);
            }

            if (FileOperation.SerializeObject(General.appointmentFileName, newApp)) {
                General.AlertMsgInfo("Appointment Rescheduled!", "Success");

                //Update appointment hashtable
                InitGlobalData();
                InitTableRecords();

            } else {
                General.AlertMsgError("Appointment failed to reschedule, please try again later.", "Error");
            }
            btnRaSubmit.setEnabled(false);
            btnRaCancel.setEnabled(false);
        }
    }//GEN-LAST:event_btnRaSubmitActionPerformed

    private void cboMaVacCentre2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMaVacCentre2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboMaVacCentre2ActionPerformed

    private void btnMaSubmit3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaSubmit3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMaSubmit3ActionPerformed

    private void btnRASearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRASearchActionPerformed
        String search = (!txtRaSearch.getText().isBlank() ? txtRaSearch.getText() : "").trim().toLowerCase();
        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblRA.getModel();
        dtm.setRowCount(0);

        for (Object x : htAppointment.values()) {
            Appointment a = (Appointment) x;

            if (a.Ppl.getIsCitizen()) {
                Citizen c = (Citizen) a.Ppl;

                if (c.getIcNo().toLowerCase().contains(search) || a.getCode().toLowerCase().contains(search) || a.Ppl.Username.toLowerCase().contains(search) || a.Ppl.getFullName().toLowerCase().contains(search)) {
                    Object[] dtmObj = new Object[]{
                        a.getCode(),
                        a.Ppl.getFullName() + "(" + a.Ppl.Username + ")",
                        a.getRegisterDate().GetShortDate(),
                        a.getStatus()
                    };

                    dtm.addRow(dtmObj);
                }
            } else {
                NonCitizen c = (NonCitizen) a.Ppl;

                if (c.getPassport().toLowerCase().contains(search) || a.getCode().toLowerCase().contains(search) || a.Ppl.Username.toLowerCase().contains(search) || a.Ppl.getFullName().toLowerCase().contains(search)) {
                    Object[] dtmObj = new Object[]{
                        a.getCode(),
                        a.Ppl.getFullName() + "(" + a.Ppl.Username + ")",
                        a.getRegisterDate().GetShortDate(),
                        a.getStatus()
                    };

                    dtm.addRow(dtmObj);
                }
            }

        }

        tblRA.setModel(dtm);
    }//GEN-LAST:event_btnRASearchActionPerformed

    private void tblRAMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRAMouseClicked
        DefaultTableModel Tmodel = (DefaultTableModel) tblRA.getModel();

        //Display data into text field when the specific row is selected
        String appCode = Tmodel.getValueAt(tblRA.getSelectedRow(), 1).toString();
        FileOperation fo = new FileOperation(appCode, General.appointmentFileName);
        fo.ReadFile();
        Appointment app = (Appointment) fo.getReadResult();

        //Populate appointment info
        //Reg info
        txtRaAppCode.setText(app.getCode());
        txtRaUsername.setText(app.Ppl.Username);
        txtRaFullname.setText(app.Ppl.getFullName());

        String gender = General.GenderFemale == app.Ppl.getGender() ? General.GenderFemaleString : General.GenderMaleString;
        txtRaGender.setText(gender);

        calRaDob.setCalendar(app.Ppl.Dob.getCal());
        txtRaAddress.setText(app.Ppl.Address.getFullAddress());

        if (app.Ppl.getIsCitizen()) {
            Citizen c = (Citizen) app.Ppl;
            txtRaIC.setText(c.getIcNo());
            txtRaNat.setText(General.NationalityCitizen);
        } else {
            NonCitizen c = (NonCitizen) app.Ppl;
            txtRaIC.setText(c.getPassport());
            txtRaNat.setText(General.NationalityNonCitizen);
        }

        //Schedule Info
        txtRaRemarks.setText(app.getRemarks());
        calRaVacDate.setCalendar(app.VaccinationDate.getCal());
        txtRaVac.setText(app.Vacc.getVacCode() + " - " + app.Vacc.getName());
        txtRaVacCentre.setText(app.Location.getVacCode() + " - " + app.Location.getName());
        txtRaVacAdd.setText(app.Location.getVacAddress().getFullAddress());
        txtRaReason.setText(app.getRejectReason());

        //New Schedule Populate
        //Popoulate VacCentre combobox
        for (Object x : htVacCentre.values()) {
            VaccineCentre v = (VaccineCentre) x;

            if (v.getVacAddress().getState().equals(app.Ppl.Address.getState())) {
                cboRaVacCentre.addItem(v.getVacCode() + " - " + v.getName());
            }
        }

        btnRaSubmit.setEnabled(true);
        btnRaCancel.setEnabled(true);
    }//GEN-LAST:event_tblRAMouseClicked

    private void cboVSearchVacCentreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboVSearchVacCentreActionPerformed
        String search = (!txtVSearch.getText().isBlank() ? txtVSearch.getText() : "").trim().toLowerCase();
        MyDateTime vacDate = calVSearchVacDate.getCalendar() != null ? new MyDateTime(calVSearchVacDate.getCalendar()) : null;
        Vaccine vaccine = (Vaccine) (cboVSearchVac.getSelectedIndex() > 0 ? htVac.get(String.valueOf(cboVSearchVac.getSelectedItem()).split(" - ")[0]) : null);
        VaccineCentre centre = (VaccineCentre) (cboVSearchVacCentre.getSelectedIndex() > 0 ? htVacCentre.get(String.valueOf(cboVSearchVacCentre.getSelectedItem()).split(" - ")[0]) : null);
        AppointmentStatus status = (cboVSearchStatus.getSelectedIndex() > 0) ? AppointmentStatus.valueOf(String.valueOf(cboVSearchStatus.getSelectedItem())) : null;

        Class nat = null;
        if (cboVSearchNat.getSelectedIndex() > 0) {
            switch (String.valueOf(cboVSearchNat.getSelectedItem())) {
                case General.NationalityCitizen:
                    nat = Citizen.class;
                    break;
                case General.NationalityNonCitizen:
                    nat = NonCitizen.class;
                    break;
                default:
                    nat = null;
            }
        }

        VSearch(search, vacDate, nat, vaccine, centre, status);
    }//GEN-LAST:event_cboVSearchVacCentreActionPerformed

    private void cboVSearchStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboVSearchStatusActionPerformed
        String search = (!txtVSearch.getText().isBlank() ? txtVSearch.getText() : "").trim().toLowerCase();
        MyDateTime vacDate = calVSearchVacDate.getCalendar() != null ? new MyDateTime(calVSearchVacDate.getCalendar()) : null;
        Vaccine vaccine = (Vaccine) (cboVSearchVac.getSelectedIndex() > 0 ? htVac.get(String.valueOf(cboVSearchVac.getSelectedItem()).split(" - ")[0]) : null);
        VaccineCentre centre = (VaccineCentre) (cboVSearchVacCentre.getSelectedIndex() > 0 ? htVacCentre.get(String.valueOf(cboVSearchVacCentre.getSelectedItem()).split(" - ")[0]) : null);
        AppointmentStatus status = (cboVSearchStatus.getSelectedIndex() > 0) ? AppointmentStatus.valueOf(String.valueOf(cboVSearchStatus.getSelectedItem())) : null;

        Class nat = null;
        if (cboVSearchNat.getSelectedIndex() > 0) {
            switch (String.valueOf(cboVSearchNat.getSelectedItem())) {
                case General.NationalityCitizen:
                    nat = Citizen.class;
                    break;
                case General.NationalityNonCitizen:
                    nat = NonCitizen.class;
                    break;
                default:
                    nat = null;
            }
        }

        VSearch(search, vacDate, nat, vaccine, centre, status);
    }//GEN-LAST:event_cboVSearchStatusActionPerformed

    private void cboVSearchVacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboVSearchVacActionPerformed
        String search = (!txtVSearch.getText().isBlank() ? txtVSearch.getText() : "").trim().toLowerCase();
        MyDateTime vacDate = calVSearchVacDate.getCalendar() != null ? new MyDateTime(calVSearchVacDate.getCalendar()) : null;
        Vaccine vaccine = (Vaccine) (cboVSearchVac.getSelectedIndex() > 0 ? htVac.get(String.valueOf(cboVSearchVac.getSelectedItem()).split(" - ")[0]) : null);
        VaccineCentre centre = (VaccineCentre) (cboVSearchVacCentre.getSelectedIndex() > 0 ? htVacCentre.get(String.valueOf(cboVSearchVacCentre.getSelectedItem()).split(" - ")[0]) : null);
        AppointmentStatus status = (cboVSearchStatus.getSelectedIndex() > 0) ? AppointmentStatus.valueOf(String.valueOf(cboVSearchStatus.getSelectedItem())) : null;

        Class nat = null;
        if (cboVSearchNat.getSelectedIndex() > 0) {
            switch (String.valueOf(cboVSearchNat.getSelectedItem())) {
                case General.NationalityCitizen:
                    nat = Citizen.class;
                    break;
                case General.NationalityNonCitizen:
                    nat = NonCitizen.class;
                    break;
                default:
                    nat = null;
            }
        }

        VSearch(search, vacDate, nat, vaccine, centre, status);
    }//GEN-LAST:event_cboVSearchVacActionPerformed

    private void cboVSearchNatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboVSearchNatActionPerformed

        String search = (!txtVSearch.getText().isBlank() ? txtVSearch.getText() : "").trim().toLowerCase();
        MyDateTime vacDate = calVSearchVacDate.getCalendar() != null ? new MyDateTime(calVSearchVacDate.getCalendar()) : null;
        Vaccine vaccine = (Vaccine) (cboVSearchVac.getSelectedIndex() > 0 ? htVac.get(String.valueOf(cboVSearchVac.getSelectedItem()).split(" - ")[0]) : null);
        VaccineCentre centre = (VaccineCentre) (cboVSearchVacCentre.getSelectedIndex() > 0 ? htVacCentre.get(String.valueOf(cboVSearchVacCentre.getSelectedItem()).split(" - ")[0]) : null);
        AppointmentStatus status = (cboVSearchStatus.getSelectedIndex() > 0) ? AppointmentStatus.valueOf(String.valueOf(cboVSearchStatus.getSelectedItem())) : null;

        Class nat = null;
        if (cboVSearchNat.getSelectedIndex() > 0) {
            switch (String.valueOf(cboVSearchNat.getSelectedItem())) {
                case General.NationalityCitizen:
                    nat = Citizen.class;
                    break;
                case General.NationalityNonCitizen:
                    nat = NonCitizen.class;
                    break;
                default:
                    nat = null;
            }
        }

        VSearch(search, vacDate, nat, vaccine, centre, status);
    }//GEN-LAST:event_cboVSearchNatActionPerformed

    private void btnVSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVSearchActionPerformed
        String search = (!txtVSearch.getText().isBlank() ? txtVSearch.getText() : "").trim().toLowerCase();
        MyDateTime vacDate = calVSearchVacDate.getCalendar() != null ? new MyDateTime(calVSearchVacDate.getCalendar()) : null;
        Vaccine vaccine = (Vaccine) (cboVSearchVac.getSelectedIndex() > 0 ? htVac.get(String.valueOf(cboVSearchVac.getSelectedItem()).split(" - ")[0]) : null);
        VaccineCentre centre = (VaccineCentre) (cboVSearchVacCentre.getSelectedIndex() > 0 ? htVacCentre.get(String.valueOf(cboVSearchVacCentre.getSelectedItem()).split(" - ")[0]) : null);
        AppointmentStatus status = (cboVSearchStatus.getSelectedIndex() > 0) ? AppointmentStatus.valueOf(String.valueOf(cboVSearchStatus.getSelectedItem())) : null;

        Class nat = null;
        if (cboVSearchNat.getSelectedIndex() > 0) {
            switch (String.valueOf(cboVSearchNat.getSelectedItem())) {
                case General.NationalityCitizen:
                    nat = Citizen.class;
                    break;
                case General.NationalityNonCitizen:
                    nat = NonCitizen.class;
                    break;
                default:
                    nat = null;
            }
        }

        VSearch(search, vacDate, nat, vaccine, centre, status);

    }//GEN-LAST:event_btnVSearchActionPerformed

    private void btnMASearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMASearchActionPerformed
        String search = "";

        if (!txtMaSearch.getText().isBlank()) {
            search = txtMaSearch.getText();
        }

        search = search.trim().toLowerCase();

        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblMA.getModel();
        dtm.setRowCount(0);

        for (Object x : htAppointment.values()) {
            Appointment a = (Appointment) x;

            if (!(a.getStatus().equals(AppointmentStatus.Approved) || a.getStatus().equals(AppointmentStatus.Pending))) {
                continue;
            }

            if (a.Ppl.getIsCitizen()) {
                Citizen c = (Citizen) a.Ppl;

                if (c.getIcNo().toLowerCase().contains(search) || a.getCode().toLowerCase().contains(search) || a.Ppl.Username.toLowerCase().contains(search) || a.Ppl.getFullName().toLowerCase().contains(search)) {
                    Object[] dtmObj = new Object[]{
                        a.getCode(),
                        a.Ppl.getFullName() + " (" + a.Ppl.Username + ")",
                        c.getIcNo() + " (" + General.NationalityCitizen + ")",
                        a.getRegisterDate().GetShortDate(),
                        a.getStatus()
                    };

                    dtm.addRow(dtmObj);
                }
            } else {
                NonCitizen c = (NonCitizen) a.Ppl;

                if (c.getPassport().toLowerCase().contains(search) || a.getCode().toLowerCase().contains(search) || a.Ppl.Username.toLowerCase().contains(search) || a.Ppl.getFullName().toLowerCase().contains(search)) {
                    Object[] dtmObj = new Object[]{
                        a.getCode(),
                        a.Ppl.getFullName() + " (" + a.Ppl.Username + ")",
                        c.getPassport() + " (" + General.NationalityNonCitizen + ")",
                        a.getRegisterDate().GetShortDate(),
                        a.getStatus()
                    };

                    dtm.addRow(dtmObj);
                }
            }

        }

        tblMA.setModel(dtm);
    }//GEN-LAST:event_btnMASearchActionPerformed

    private void btnMASearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMASearchMouseClicked
        // TODO add your handling code here:
        String search = "";

        DefaultTableModel dtm = (DefaultTableModel) tblMA.getModel();
        dtm.setRowCount(0);

        if (!txtMaSearch.getText().isBlank()) {
            search = txtMaSearch.getText();
        }

        search = search.trim().toLowerCase();

        for (Object x : htAppointment.values()) {
            Appointment a = (Appointment) x;

            if (!(a.getStatus() != AppointmentStatus.Approved || a.getStatus() != AppointmentStatus.Cancelled || a.getStatus() != AppointmentStatus.Pending)) {
                continue;
            }

            if (a.getCode().toLowerCase().equals(search) || a.Ppl.Username.toLowerCase().contains(search) || a.Ppl.getFullName().toLowerCase().contains(search)) {

                String IcPassport = "";
                if (a.Ppl.getIsCitizen()) {
                    Citizen c = (Citizen) a.Ppl;
                    IcPassport = c.getIcNo() + " (" + General.NationalityCitizen + ")";
                } else {
                    NonCitizen c = (NonCitizen) a.Ppl;
                    IcPassport = c.getPassport() + " (" + General.NationalityNonCitizen + ")";
                }

                Object[] dtmObj = new Object[]{
                    a.getCode(),
                    a.Ppl.getFullName() + " (" + a.Ppl.Username + ")",
                    IcPassport,
                    a.getRegisterDate().GetShortDateTime(),
                    a.getStatus()
                };

                dtm.addRow(dtmObj);
            }
        }

        tblMA.setModel(dtm);
    }//GEN-LAST:event_btnMASearchMouseClicked

    private void txtMaSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaSearchActionPerformed

    private void btnMaDeclineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaDeclineActionPerformed
        if (General.AlertQuestionYesNo("Decline an appointment will automatically cancel it, do you still want to continue?", "Decline Appointment Confirmation") == 0) {
            String appCode = txtMaAppCode.getText();

            //Retrieve appointment
            FileOperation fo = new FileOperation(appCode, General.appointmentFileName);
            fo.ReadFile();
            Appointment app = (Appointment) fo.getReadResult();

            //Modify pending stock (if from approval)
//            if (app.getStatus().equals(AppointmentStatus.Approved)) {
//                Stock s = new Stock(app.Vacc, app.CheckDoseFromAppointment(), app.Location);
//                if (s.FindStock()) {
//
//                    if (s.MinusPendingQty(1, currentUser, "Cancel Vaccination - " + app.getCode())) {
//                        General.AlertMsgError("Something went wrong, please try again later!", "Error");
//                        return;
//                    }
//
//                } else {
//                    s.GenerateId();
//                    s.MinusPendingQty(1, currentUser, "Cancel Vaccination - " + app.getCode());
//                    FileOperation.SerializeObject(General.stockFileName, s);
//
//                }
//            }
            app.setStatus(AppointmentStatus.Cancelled);

            if (fo.ModifyRecord(app)) {

                General.AlertMsgInfo("Appointment (" + appCode + ") has been cancelled!", "Appointment Updated");
                InitGlobalData();
                InitTableRecords();
                ComponentReset();
                btnMaApprove.setEnabled(true);
            } else {
                General.AlertMsgError("Failed to cancel Appointment (" + appCode + "). Please try again later!", "Error");
            }
        }
    }//GEN-LAST:event_btnMaDeclineActionPerformed

    private void btnMaApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaApproveActionPerformed
        General.AlertMsgInfo("Please assign a Vaccination Date, Vaccine and Vaccination Centre for this appointment.", "Info");
        pnlApprovedAppointment.setVisible(true);
    }//GEN-LAST:event_btnMaApproveActionPerformed

    private void cboMaVacCentreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMaVacCentreActionPerformed

        if (cboMaVacCentre.getSelectedIndex() != -1) {
            String vacCentre = String.valueOf(cboMaVacCentre.getSelectedItem()).split(" - ")[0];
            VaccineCentre vc = (VaccineCentre) htVacCentre.get(vacCentre);
            txtMaVacAdd.setText(vc.VacAddress.getFullAddress());
        } else {
            txtMaVacAdd.setText("");
        }
    }//GEN-LAST:event_cboMaVacCentreActionPerformed

    private void btnMaSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaSubmitActionPerformed

        if (calMaVacDate.getCalendar() == null && cboMaVac.getSelectedIndex() == -1 || cboMaVacCentre.getSelectedIndex() == -1) {
            General.AlertMsgError("All fields in Schedule Appointment have to be filled!", "Error");
            return;
        }

        String remarks = txtMaRemarks.getText();
        MyDateTime vacDate = new MyDateTime(calMaVacDate.getCalendar());

        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = (Vaccine) vacArray[cboMaVac.getSelectedIndex()];

        String vacCentreCode = String.valueOf(cboMaVacCentre.getSelectedItem()).split(" - ")[0];
        VaccineCentre vacCentre = (VaccineCentre) htVacCentre.get(vacCentreCode);

        //Update appointment
        Appointment data = (Appointment) htAppointment.get(txtMaAppCode.getText());
        if (data != null) {
            AppointmentStatus as = data.getStatus();

            data.setLocation(vacCentre);
            data.setRemarks(remarks);
            data.setVacc(vac);
            data.setVaccinationDate(vacDate);
            data.setStatus(AppointmentStatus.Approved);
            data.setHandledBy(currentUser);

            FileOperation fo = new FileOperation(data.getCode(), General.appointmentFileName);

            if (fo.ModifyRecord(data)) {

//                if (as.equals(AppointmentStatus.Pending)) {
//                    //Modify vaccine stock
//                    Stock s = new Stock(data.Vacc, data.CheckDoseFromAppointment(), data.Location);
//
//                    if (s.FindStock()) {
//
//                        if (!s.AddPendingQty(1, currentUser, "Approve Vaccination - " + data.getCode())) {
//                            General.AlertMsgError("Something went wrong, please try again later!", "Error");
//                            return;
//                        }
//
//                        FileOperation fo2 = new FileOperation(s.getId(), General.stockFileName);
//                        if (!fo2.ModifyRecord(s)) {
//                            General.AlertMsgError("Something went wrong, please try again later!", "Error");
//                            return;
//                        }
//
//                    } else {
//                        s.GenerateId();
//                        if (!s.AddPendingQty(1, currentUser, "Approve Vaccination - " + data.getCode())) {
//                            General.AlertMsgError("Something went wrong, please try again later!", "Error");
//                            return;
//                        }
//                        FileOperation.SerializeObject(General.stockFileName, s);
//
//                    }
//                }
                General.AlertMsgInfo("Appointment Updated!", "Success");

                //Update appointment hashtable
                InitGlobalData();
                InitTableRecords();
                btnMaApprove.setEnabled(false);
            } else {
                General.AlertMsgError("Appointment failed to update, please try again later.", "Error");
            }

        }

    }//GEN-LAST:event_btnMaSubmitActionPerformed

    private void tblMAMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMAMouseClicked

        ComponentReset();

        //Display data into text field when the specific row is selected
        String JTbAppCode = String.valueOf(tblMA.getValueAt(tblMA.getSelectedRow(), 0));
        txtMaAppCode.setText(JTbAppCode);

        //Retrieve the data
        Appointment data = (Appointment) htAppointment.get(JTbAppCode);

        txtMaUsername.setText(data.Ppl.Username);
        txtMaFullname.setText(data.Ppl.getFullName());

        String Gender = data.Ppl.getGender() == General.GenderMale ? General.GenderMaleString : General.GenderFemaleString;
        txtMaGender.setText(Gender);

        calMaDob.setCalendar(data.getRegisterDate().getCal());
        txtMaAddress.setText(data.Ppl.Address.getFullAddress());

        txtMaRemarks.setText(data.getRemarks());

        //Popoulate VacCentre combobox
        cboMaVacCentre.removeAllItems();
        for (Object x : htVacCentre.values()) {
            VaccineCentre v = (VaccineCentre) x;

            if (v.getVacAddress().getState().equals(data.Ppl.Address.getState())) {
                cboMaVacCentre.addItem(v.getVacCode() + " - " + v.getName());
            }
        }

        if (data.Ppl.getIsCitizen()) {
            Citizen c = (Citizen) data.Ppl;
            txtMaIC.setText(c.getIcNo());
            txtMaNat.setText(General.NationalityCitizen);
        } else {
            NonCitizen c = (NonCitizen) data.Ppl;
            txtMaIC.setText(c.getPassport());
            txtMaNat.setText(General.NationalityCitizen);
        }

        if (data.getStatus().equals(AppointmentStatus.Pending)) {
            btnMaApprove.setEnabled(true);
            btnMaDecline.setEnabled(true);
            return;
        }

        if (data.getStatus().equals(AppointmentStatus.Approved)) {
            calMaVacDate.setCalendar(data.VaccinationDate.getCal());
            cboMaVac.setSelectedItem(data.Vacc.getVacCode() + " - " + data.Vacc.getName());
            cboMaVacCentre.setSelectedItem(data.Location.getVacCode() + " - " + data.Location.getName());
            txtMaVacAdd.setText(data.Location.VacAddress.getFullAddress());
            pnlApprovedAppointment.setVisible(true);
            btnMaDecline.setEnabled(true);
            btnMaSubmit.setText("Save");
            return;
        }

        if (data.getStatus().equals(AppointmentStatus.Cancelled)) {
            btnMaApprove.setEnabled(true);
            return;
        }

    }//GEN-LAST:event_tblMAMouseClicked

    private void txtPCfmPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPCfmPwKeyReleased
        lblPwNoMatch.setVisible(!String.valueOf(txtPNewPw.getPassword()).equals(String.valueOf(txtPCfmPw.getPassword())));
    }//GEN-LAST:event_txtPCfmPwKeyReleased

    private void txtPNewPwKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNewPwKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPNewPwKeyTyped

    private void txtPNewPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNewPwKeyReleased
        txtPCfmPw.setEnabled(txtPNewPw.getPassword().length > 0);
        txtPCfmPw.setText("");
        lblPwNoMatch.setVisible(txtPCfmPw.isEnabled());
    }//GEN-LAST:event_txtPNewPwKeyReleased

    private void btnAdPSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdPSaveActionPerformed
        if (General.AlertQuestionYesNo("Do you want to save your changes?", "Save Confirmation") == 1) {
            return;
        }

        if (!txtPEmail.getText().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")) {
            General.AlertMsgError("Email format invalid.", "Profile Create Failed!");
            return;
        }

        //Check field filled
        if (txtAdPNo.getText().isBlank() || txtPEmail.getText().isBlank()) {
            General.AlertMsgError("All details have to be filled.", "Profile Update Failed!");
            return;
        }

        //Check Pw Any Changes
        if (txtPNewPw.getPassword().length != 0) {
            //Password doesnt match

            if (!String.valueOf(txtPNewPw.getPassword()).equals(String.valueOf(txtPCfmPw.getPassword()))) {
                General.AlertMsgError("New Password doesn't match with Confirm Password.", "Profile Update Failed!");
                return;
            } else {
                currentUser.setPassword(String.valueOf(txtPNewPw.getPassword()));

            }

        }

        String phone = txtAdPNo.getText().trim();
        String email = txtPEmail.getText();

        currentUser.setContact(phone);
        currentUser.setEmail(email);

        FileOperation fo = new FileOperation(currentUser.Username, General.userFileName);
        fo.ReadFile();

        if (fo.ModifyRecord(currentUser)) {
            General.AlertMsgInfo("Profile has been updated.", "Success!");
            editProfile(false);
            InitTableRecords();
        } else {
            General.AlertMsgError("Profile changes were not saved, please try again later.", "Error!");
        }
    }//GEN-LAST:event_btnAdPSaveActionPerformed

    private void btnAdPInfoEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdPInfoEditActionPerformed
        editProfile(true);
    }//GEN-LAST:event_btnAdPInfoEditActionPerformed

    private void txtAdPNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAdPNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAdPNameActionPerformed

    private void btnSClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSClearActionPerformed
        ComponentResetCommittee();
    }//GEN-LAST:event_btnSClearActionPerformed

    private void ComponentResetCommittee() {
        txtSFName.setText("");
        txtSLName.setText("");
        txtSContact.setText("");
        calSDob.setDate(new Date());
        cboSRole.setSelectedIndex(0);
        cboSVacCentre.setSelectedItem(0);
        txtSEmail.setText("");
        txtSPass.setText("");
        txtSCfmPass.setText("");
    }

    private void btnSRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSRegisterActionPerformed
        //Empty field check
        if (txtSFName.getText().isBlank() || txtSLName.getText().isBlank()
                || txtSContact.getText().isBlank() || calSDob.getCalendar() == null
                || txtSEmail.getText().isBlank() || txtSPass.getPassword().length <= 0
                || txtSCfmPass.getPassword().length <= 0 || cboSRole.getSelectedIndex() == -1) {
            General.AlertMsgError("All fields must be filled!", "Error");
            return;
        }

        String role = String.valueOf(cboSRole.getSelectedItem());

        if (role.equals(General.PersonnelRoleDoctor) || role.equals(General.PersonnelRoleStockist)) {
            if (cboSVacCentre.getSelectedIndex() <= 0) {
                General.AlertMsgError("All fields must be filled!", "Error");
                return;
            }
        }

        if (!txtSEmail.getText().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")) {
            General.AlertMsgError("Email format invalid.", "Profile Create Failed!");
            return;
        }

        if (!String.valueOf(txtSPass.getPassword()).equals(String.valueOf(txtSCfmPass.getPassword()))) {
            General.AlertMsgError("Password doesn't match!", "Error");
            return;
        }

        //Data 
        char gender = General.GenderMale;
        if (rbSFemale.isSelected()) {
            gender = General.GenderFemale;
        }

        MyDateTime mdt = new MyDateTime(calSDob.getCalendar());

        User user = null;
        if (role.equals(General.PersonnelRoleAdmin)) {
            user = new Admin(PersonnelStatus.Active, txtSFName.getText(), txtSLName.getText(), gender, mdt, txtSEmail.getText(), String.valueOf(txtSPass.getPassword()), txtSContact.getText());
        } else if (role.equals(General.PersonnelRoleDoctor)) {
            Object[] vcArray = (Object[]) htVacCentre.values().toArray();
            VaccineCentre vc = (VaccineCentre) vcArray[cboSVacCentre.getSelectedIndex() - 1];
            user = new Doctor(vc, PersonnelStatus.Active, txtSFName.getText(), txtSLName.getText(), gender, mdt, txtSEmail.getText(), String.valueOf(txtSPass.getPassword()), txtSContact.getText());
        } else if (role.equals(General.PersonnelRoleStockist)) {
            Object[] vcArray = (Object[]) htVacCentre.values().toArray();
            VaccineCentre vc = (VaccineCentre) vcArray[cboSVacCentre.getSelectedIndex() - 1];
            user = new Stockist(vc, PersonnelStatus.Active, txtSFName.getText(), txtSLName.getText(), gender, mdt, txtSEmail.getText(), String.valueOf(txtSPass.getPassword()), txtSContact.getText());
        }

        if (user != null) {
            if (FileOperation.SerializeObject(General.userFileName, user)) {
                General.AlertMsgInfo("Committee (" + role + ") Created! The username is " + user.Username + ".", "Success!");
                ComponentResetCommittee();
                InitGlobalData();
                InitTableRecords();
            }
        } else {
            General.AlertMsgError("Something went wrong, please try again later!", "Error");
        }


    }//GEN-LAST:event_btnSRegisterActionPerformed

    private void rbSMaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbSMaleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbSMaleActionPerformed

    private void btnMCSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMCSearchActionPerformed
        String search = (!txtMCSearch.getText().isBlank() ? txtMCSearch.getText() : "").trim().toLowerCase();
        String role = cboMCSearchRole.getSelectedIndex() > 0 ? String.valueOf(cboMCSearchRole.getSelectedItem()) : "";
        VaccineCentre centre = (VaccineCentre) (cboMCSearchVacCentre.getSelectedIndex() > 0 ? htVacCentre.get(String.valueOf(cboMCSearchVacCentre.getSelectedItem()).split(" - ")[0]) : null);
        PersonnelStatus status = (cboMCSearchStatus.getSelectedIndex() > 0) ? PersonnelStatus.valueOf(String.valueOf(cboMCSearchStatus.getSelectedItem())) : null;

        MCSearch(search, role, status, centre);

    }//GEN-LAST:event_btnMCSearchActionPerformed

    private void MCSearch(String search, String role, PersonnelStatus status, VaccineCentre vc) {
        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblMC.getModel();
        dtm.setRowCount(0);

        for (Object x : htUser.keySet()) {
            User u = (User) htUser.get(x);

            if (u.getUserRole().equals(General.UserRolePeople)) {
                continue;
            }

            Personnel v = (Personnel) u;

            Object[] dtmObj = null;

            if (status != null) {
                if (!v.getStatus().equals(status)) {
                    continue;
                }
            }

            if (!role.isBlank()) {
                if (!v.getPersonnelRole().equals(role)) {
                    continue;
                }
            }

            if (v.getPersonnelRole().equals(General.PersonnelRoleAdmin)) {
                Admin c = (Admin) v;

                if (vc != null) {
                    continue;
                }

                if ((c.Username.toLowerCase().contains(search) || c.getFullName().toLowerCase().contains(search) || c.getContact().toLowerCase().contains(search) || c.getEmail().toLowerCase().contains(search))) {
                    dtmObj = new Object[]{
                        ++i,
                        v.Username,
                        v.getFullName(),
                        v.getGender(),
                        General.PersonnelRoleAdmin,
                        "-",
                        v.getHiredDate(),
                        v.getStatus()
                    };
                }

            } else if (v.getPersonnelRole().equals(General.PersonnelRoleDoctor)) {

                Doctor c = (Doctor) v;

                if (vc != null) {
                    if (!c.VacCentre.getVacCode().equals(vc.getVacCode())) {
                        continue;
                    }
                }

                if ((c.Username.toLowerCase().contains(search) || c.getFullName().toLowerCase().contains(search) || c.getContact().toLowerCase().contains(search) || c.getEmail().toLowerCase().contains(search))) {
                    dtmObj = new Object[]{
                        ++i,
                        c.Username,
                        c.getFullName(),
                        c.getGender(),
                        General.PersonnelRoleDoctor,
                        c.VacCentre.getVacCode() + " - " + c.VacCentre.getName(),
                        c.getHiredDate(),
                        c.getStatus()
                    };
                }

            } else if (v.getPersonnelRole().equals(General.PersonnelRoleStockist)) {
                Stockist c = (Stockist) v;

                if (vc != null) {
                    if (!c.VacCentre.getVacCode().equals(vc.getVacCode())) {
                        continue;
                    }
                }

                if ((c.Username.toLowerCase().contains(search) || c.getFullName().toLowerCase().contains(search) || c.getContact().toLowerCase().contains(search) || c.getEmail().toLowerCase().contains(search))) {
                    dtmObj = new Object[]{
                        ++i,
                        c.Username,
                        c.getFullName(),
                        c.getGender(),
                        General.PersonnelRoleStockist,
                        c.VacCentre.getVacCode() + " - " + c.VacCentre.getName(),
                        c.getHiredDate(),
                        c.getStatus()
                    };
                }

            } else {
                continue;
            }

            if (dtmObj != null) {
                dtm.addRow(dtmObj);
            }

        }

        //-------------------
        tblMC.setModel(dtm);
    }

    private void txtComFNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtComFNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtComFNameActionPerformed

    private void btnComSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnComSaveActionPerformed
        if (General.AlertQuestionYesNo("Do you want to save your changes?", "Save Confirmation") == 1) {
            return;
        }

        if (txtComCode.getText().isBlank()) {
            General.AlertMsgError("Please select a committee.", "Record Required");
            return;
        }

        //Check field filled
        if (txtComFName.getText().isBlank() || txtComLName.getText().isBlank()
                || txtComContact.getText().isBlank() || txtComEmail.getText().isBlank()
                || calComDob.getCalendar() == null) {
            General.AlertMsgError("All details have to be filled.", "Profile Update Failed!");
            return;
        }

        if (!txtComEmail.getText().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")) {
            General.AlertMsgError("Email format invalid.", "Profile Create Failed!");
            return;
        }

        String role = String.valueOf(cboComRole.getSelectedItem());

        if ((role.equals(General.PersonnelRoleDoctor) || role.equals(General.PersonnelRoleStockist)) && cboComVacCentre.getSelectedIndex() <= 0) {
            General.AlertMsgError("All details have to be filled.", "Profile Update Failed!");
            return;
        }

        //Retrieve committee
        String code = txtComCode.getText();

        FileOperation fo = new FileOperation(code, General.userFileName);
        fo.ReadFile();
        Personnel v = (Personnel) fo.getReadResult();

        //Check Pw Any Changes
        if (txtComNewPw.getPassword().length != 0) {
            //Password doesnt match

            if (!String.valueOf(txtComNewPw.getPassword()).equals(String.valueOf(txtComCfmPw.getPassword()))) {
                General.AlertMsgError("New Password doesn't match with Confirm Password.", "Profile Update Failed!");
                return;
            } else {
                v.setPassword(String.valueOf(txtComNewPw.getPassword()));

            }

        }

        if (v.getPersonnelRole().equals(General.PersonnelRoleDoctor)) {
            Doctor u = (Doctor) v;

            String vacCentre = String.valueOf(cboComVacCentre.getSelectedItem()).split(" - ")[0];

            FileOperation fo2 = new FileOperation(vacCentre, General.vaccineCentreFileName);
            fo2.ReadFile();
            VaccineCentre vc = (VaccineCentre) fo2.getReadResult();

            u.setVacCentre(vc);

            v = u;
        } else if (v.getPersonnelRole().equals(General.PersonnelRoleStockist)) {
            Stockist u = (Stockist) v;

            String vacCentre = String.valueOf(cboComVacCentre.getSelectedItem()).split(" - ")[0];

            FileOperation fo2 = new FileOperation(vacCentre, General.vaccineCentreFileName);
            fo2.ReadFile();
            VaccineCentre vc = (VaccineCentre) fo2.getReadResult();

            u.setVacCentre(vc);

            v = u;
        }

        String phone = txtComContact.getText().trim();
        String email = txtComEmail.getText();

        v.setContact(phone);
        v.setEmail(email);
        v.setFirst_Name(txtComFName.getText().trim());
        v.setFirst_Name(txtComLName.getText().trim());

        char Gender = v.getGender();

        if (rbComMale.isSelected()) {
            Gender = General.GenderMale;
        }

        if (rbComFemale.isSelected()) {
            Gender = General.GenderFemale;
        }
        v.setGender(Gender);

        MyDateTime dob = new MyDateTime(calComDob.getCalendar());
        v.setDob(dob);
        v.setStatus(PersonnelStatus.valueOf(String.valueOf(cboComStatus.getSelectedItem())));

        if (fo.ModifyRecord(v)) {
            General.AlertMsgInfo("Committee (" + v.Username + ") has been updated!", "Success");
            InitGlobalData();
            InitTableRecords();
        } else {
            General.AlertMsgError("Failed to update user (" + v.Username + "). Please try again later!", "Error");
        }

    }//GEN-LAST:event_btnComSaveActionPerformed

    private void txtComNewPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtComNewPwKeyReleased
        // TODO add your handling code here:
        txtComCfmPw.setEnabled(txtComNewPw.getPassword().length > 0);
        txtComCfmPw.setText("");
        lblPwNoMatch.setVisible(txtPCfmPw.isEnabled());
    }//GEN-LAST:event_txtComNewPwKeyReleased

    private void txtComNewPwKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtComNewPwKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtComNewPwKeyTyped

    private void txtComCfmPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtComCfmPwKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtComCfmPwKeyReleased

    private void tblMCMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMCMouseClicked
        //Display data into text field when the specific row is selected
        String code = String.valueOf(tblMC.getValueAt(tblMC.getSelectedRow(), 1));
        txtComCode.setText(code);

        //Retrieve the data
        Personnel data = (Personnel) htUser.get(code);

        txtComFName.setText(data.getFirst_Name());
        txtComLName.setText(data.getLast_Name());

        if (data.getGender() == General.GenderMale) {
            rbComMale.setSelected(true);
        } else {
            rbComFemale.setSelected(true);
        }

        calComDob.setCalendar(data.Dob.getCal());
        txtComContact.setText(data.getContact());
        txtComEmail.setText(data.getEmail());
        cboComStatus.setSelectedItem(data.getStatus());
        calComHiredDate.setCalendar(data.getHiredDate().getCal());

        if (data.getPersonnelRole().equals(General.PersonnelRoleAdmin)) {
            cboComRole.setSelectedItem(General.PersonnelRoleAdmin);
            pnlComVacCentre.setVisible(false);
        } else if (data.getPersonnelRole().equals(General.PersonnelRoleDoctor)) {
            Doctor d = (Doctor) data;
            cboComRole.setSelectedItem(General.PersonnelRoleDoctor);
            pnlComVacCentre.setVisible(true);
            cboComVacCentre.setSelectedItem(d.VacCentre.getVacCode() + " - " + d.VacCentre.getName());
        } else if (data.getPersonnelRole().equals(General.PersonnelRoleStockist)) {
            Stockist s = (Stockist) data;
            cboComRole.setSelectedItem(General.PersonnelRoleStockist);
            pnlComVacCentre.setVisible(true);
            cboComVacCentre.setSelectedItem(s.VacCentre.getVacCode() + " - " + s.VacCentre.getName());
        } else {
            General.AlertMsgError("No role found", "Error");
        }


    }//GEN-LAST:event_tblMCMouseClicked

    private void cboSRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSRoleActionPerformed
        // TODO add your handling code here:
        cboSVacCentre.setEnabled(cboSRole.getSelectedItem().equals(General.PersonnelRoleDoctor) || cboSRole.getSelectedItem().equals(General.PersonnelRoleStockist));
    }//GEN-LAST:event_cboSRoleActionPerformed

    private void btnCncAdd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCncAdd1ActionPerformed
        // TODO add your handling code here:
        if (txtCncCode.getText().isBlank()) {
            General.AlertMsgError("Please select a record.", "Record Required");
            return;
        }

        if (txtCncName1.getText().isBlank() || txtCncNo1.getText().isBlank()
                || txtCncStreet1.getText().isBlank() || txtCncCity1.getText().isBlank()
                || txtCncPost1.getText().isBlank() || cboCncState1.getSelectedIndex() == -1) {
            General.AlertMsgError("Update failed! All fields must be filled.", "Error");
            return;
        }

        String code = txtCncCode.getText();

        //Retrieve appointment
        FileOperation fo = new FileOperation(code, General.vaccineCentreFileName);
        fo.ReadFile();
        VaccineCentre v = (VaccineCentre) fo.getReadResult();

        v.setName(txtCncName1.getText());

        Address add = new Address(txtCncNo1.getText(), txtCncStreet1.getText(), txtCncCity1.getText(), txtCncPost1.getText(), String.valueOf(cboCncState1.getSelectedItem()));
        v.setVacAddress(add);

        if (fo.ModifyRecord(v)) {
            General.AlertMsgInfo("Vaccine Centre (" + v.getVacCode() + ") has been updated!", "Success");
            InitGlobalData();
            InitTableRecords();
        } else {
            General.AlertMsgError("Failed to update Vaccine Centre (" + v.getVacCode() + "). Please try again later!", "Error");
        }

    }//GEN-LAST:event_btnCncAdd1ActionPerformed

    private void tblCMMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCMMouseClicked

        //Display data into text field when the specific row is selected
        String code = String.valueOf(tblCM.getValueAt(tblCM.getSelectedRow(), 1));
        txtCncCode.setText(code);

        //Retrieve the data
        VaccineCentre data = (VaccineCentre) htVacCentre.get(code);

        txtCncName1.setText(data.getName());
        txtCncNo1.setText(data.VacAddress.getNo());
        txtCncStreet1.setText(data.VacAddress.getStreet());
        txtCncCity1.setText(data.VacAddress.getCity());
        txtCncPost1.setText(data.VacAddress.getPostcode());
        cboCncState1.setSelectedItem(data.VacAddress.getState());

    }//GEN-LAST:event_tblCMMouseClicked

    private void txtComLNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtComLNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtComLNameActionPerformed

    private void rbComMaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbComMaleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbComMaleActionPerformed

    private void cboMCSearchRoleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMCSearchRoleActionPerformed
        String search = (!txtMCSearch.getText().isBlank() ? txtMCSearch.getText() : "").trim().toLowerCase();
        String role = cboMCSearchRole.getSelectedIndex() > 0 ? String.valueOf(cboMCSearchRole.getSelectedItem()) : "";
        VaccineCentre centre = (VaccineCentre) (cboMCSearchVacCentre.getSelectedIndex() > 0 ? htVacCentre.get(String.valueOf(cboMCSearchVacCentre.getSelectedItem()).split(" - ")[0]) : null);
        PersonnelStatus status = (cboMCSearchStatus.getSelectedIndex() > 0) ? PersonnelStatus.valueOf(String.valueOf(cboMCSearchStatus.getSelectedItem())) : null;

        MCSearch(search, role, status, centre);
    }//GEN-LAST:event_cboMCSearchRoleActionPerformed

    private void cboMCSearchStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMCSearchStatusActionPerformed
        String search = (!txtMCSearch.getText().isBlank() ? txtMCSearch.getText() : "").trim().toLowerCase();
        String role = cboMCSearchRole.getSelectedIndex() > 0 ? String.valueOf(cboMCSearchRole.getSelectedItem()) : "";
        VaccineCentre centre = (VaccineCentre) (cboMCSearchVacCentre.getSelectedIndex() > 0 ? htVacCentre.get(String.valueOf(cboMCSearchVacCentre.getSelectedItem()).split(" - ")[0]) : null);
        PersonnelStatus status = (cboMCSearchStatus.getSelectedIndex() > 0) ? PersonnelStatus.valueOf(String.valueOf(cboMCSearchStatus.getSelectedItem())) : null;

        MCSearch(search, role, status, centre);
    }//GEN-LAST:event_cboMCSearchStatusActionPerformed

    private void cboMCSearchVacCentreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMCSearchVacCentreActionPerformed
        String search = (!txtMCSearch.getText().isBlank() ? txtMCSearch.getText() : "").trim().toLowerCase();
        String role = cboMCSearchRole.getSelectedIndex() > 0 ? String.valueOf(cboMCSearchRole.getSelectedItem()) : "";
        VaccineCentre centre = (VaccineCentre) (cboMCSearchVacCentre.getSelectedIndex() > 0 ? htVacCentre.get(String.valueOf(cboMCSearchVacCentre.getSelectedItem()).split(" - ")[0]) : null);
        PersonnelStatus status = (cboMCSearchStatus.getSelectedIndex() > 0) ? PersonnelStatus.valueOf(String.valueOf(cboMCSearchStatus.getSelectedItem())) : null;

        MCSearch(search, role, status, centre);
    }//GEN-LAST:event_cboMCSearchVacCentreActionPerformed

    private void cboPpSearchStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPpSearchStatusActionPerformed
        // TODO add your handling code here:
        String search = (!txtPpSearch.getText().isBlank() ? txtPpSearch.getText() : "").trim().toLowerCase();
        String state = cboPpSearchState.getSelectedIndex() > 0 ? String.valueOf(cboPpSearchState.getSelectedItem()) : "";
        VaccinationStatus status = (cboPpSearchStatus.getSelectedIndex() > 0) ? VaccinationStatus.valueOf(String.valueOf(cboPpSearchStatus.getSelectedItem())) : null;

        PpSearch(search, state, status);
    }//GEN-LAST:event_cboPpSearchStatusActionPerformed

    private void btnPpSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPpSearchActionPerformed
        String search = (!txtPpSearch.getText().isBlank() ? txtPpSearch.getText() : "").trim().toLowerCase();
        String state = cboPpSearchState.getSelectedIndex() > 0 ? String.valueOf(cboPpSearchState.getSelectedItem()) : "";
        VaccinationStatus status = (cboPpSearchStatus.getSelectedIndex() > 0) ? VaccinationStatus.valueOf(String.valueOf(cboPpSearchStatus.getSelectedItem())) : null;

        PpSearch(search, state, status);
    }//GEN-LAST:event_btnPpSearchActionPerformed

    private void PpSearch(String search, String state, VaccinationStatus status) {
        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblPp.getModel();
        dtm.setRowCount(0);

        for (Object x : htUser.keySet()) {
            User u = (User) htUser.get(x);

            if (u.getUserRole().equals(General.UserRolePersonnel)) {
                continue;
            }

            People v = (People) u;

            Object[] dtmObj = null;

            if (status != null) {
                if (!status.equals(v.getVacStatus())) {
                    continue;
                }
            }

            if (!state.isBlank()) {
                if (!v.Address.getState().equals(state)) {
                    continue;
                }
            }

            if (v.getIsCitizen()) {
                Citizen c = (Citizen) v;

                String gender = c.getGender() == General.GenderMale ? General.GenderMaleString : General.GenderFemaleString;

                if ((c.Username.toLowerCase().contains(search) || c.getIcNo().toLowerCase().contains(search) || c.getFullName().toLowerCase().contains(search) || c.getContact().toLowerCase().contains(search) || c.getEmail().toLowerCase().contains(search))) {
                    dtmObj = new Object[]{
                        ++i,
                        c.Username,
                        c.getFullName(),
                        c.getIcNo() + " (" + General.NationalityCitizen + ")",
                        gender,
                        c.Dob.GetShortDate(),
                        c.getContact(),
                        c.Address.getFullAddress(),
                        c.getEmail(),
                        c.RegistrationDate.GetShortDateTime(),
                        c.getVacStatus() + " Vaccinated"
                    };
                }

            } else if (!v.getIsCitizen()) {

                NonCitizen c = (NonCitizen) v;

                String gender = c.getGender() == General.GenderMale ? General.GenderMaleString : General.GenderFemaleString;

                if ((c.Username.toLowerCase().contains(search) || c.getPassport().toLowerCase().contains(search) || c.getFullName().toLowerCase().contains(search) || c.getContact().toLowerCase().contains(search) || c.getEmail().toLowerCase().contains(search))) {
                    dtmObj = new Object[]{
                        ++i,
                        c.Username,
                        c.getFullName(),
                        c.getPassport() + " (" + General.NationalityNonCitizen + ")",
                        gender,
                        c.Dob.GetShortDate(),
                        c.getContact(),
                        c.Address.getFullAddress(),
                        c.getEmail(),
                        c.RegistrationDate.GetShortDateTime(),
                        c.getVacStatus()
                    };
                }

            } else {
                continue;
            }

            if (dtmObj != null) {
                dtm.addRow(dtmObj);
            }

        }

        //-------------------
        tblPp.setModel(dtm);
    }

    private void cboPpSearchStateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboPpSearchStateActionPerformed
        // TODO add your handling code here:
        String search = (!txtPpSearch.getText().isBlank() ? txtPpSearch.getText() : "").trim().toLowerCase();
        String state = cboPpSearchState.getSelectedIndex() > 0 ? String.valueOf(cboPpSearchState.getSelectedItem()) : "";
        VaccinationStatus status = (cboPpSearchStatus.getSelectedIndex() > 0) ? VaccinationStatus.valueOf(String.valueOf(cboPpSearchStatus.getSelectedItem())) : null;

        PpSearch(search, state, status);
    }//GEN-LAST:event_cboPpSearchStateActionPerformed

    private void rbPpMaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPpMaleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbPpMaleActionPerformed

    private void btnSRegister1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSRegister1ActionPerformed
        if (General.AlertQuestionYesNo("Do you want to save your changes?", "Save Confirmation") == 1) {
            return;
        }

        if (txtPpCode.getText().isBlank()) {
            General.AlertMsgError("Please select a record.", "Record Required");
            return;
        }

        //Check field filled
        if (txtPpFName.getText().isBlank() || txtPpLName.getText().isBlank()
                || txtPpContact.getText().isBlank() || txtPpEmail.getText().isBlank()
                || calPpDob.getCalendar() == null || txtPpIC.getText().isBlank()
                || txtPpAddNo.getText().isBlank() || txtPpAddStreet.getText().isBlank()
                || txtPpAddCity.getText().isBlank() || txtPpAddPost.getText().isBlank()
                || cboPpAddState.getSelectedIndex() == -1) {
            General.AlertMsgError("All details have to be filled.", "Profile Update Failed!");
            return;
        }

        if (!txtPpEmail.getText().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")) {
            General.AlertMsgError("Email format invalid.", "Profile Create Failed!");
            return;
        }

        //Retrieve committee
        String code = txtPpCode.getText();

        FileOperation fo = new FileOperation(code, General.userFileName);
        fo.ReadFile();
        People v = (People) fo.getReadResult();

        //Check Pw Any Changes
        if (txtPpNewPw.getPassword().length != 0) {
            //Password doesnt match

            if (!String.valueOf(txtPpNewPw.getPassword()).equals(String.valueOf(txtPpCfmPw.getPassword()))) {
                General.AlertMsgError("New Password doesn't match with Confirm Password.", "Profile Update Failed!");
                return;
            } else {
                v.setPassword(String.valueOf(txtPpNewPw.getPassword()));

            }

        }

        String phone = txtPpContact.getText().trim();
        String email = txtPpEmail.getText();

        v.setContact(phone);
        v.setEmail(email);
        v.setFirst_Name(txtPpFName.getText().trim());
        v.setLast_Name(txtPpLName.getText().trim());

        char Gender = v.getGender();

        if (rbPpMale.isSelected()) {
            Gender = General.GenderMale;
        }

        if (rbPpFemale.isSelected()) {
            Gender = General.GenderFemale;
        }
        v.setGender(Gender);

        MyDateTime dob = new MyDateTime(calPpDob.getCalendar());
        v.setDob(dob);

        v.setAddress(txtPpAddNo.getText(), txtPpAddStreet.getText(), txtPpAddCity.getText(), txtPpAddPost.getText(), String.valueOf(cboPpAddState.getSelectedItem()));

        if (v.getIsCitizen()) {
            Citizen u = (Citizen) v;

            u.setIcNo(txtPpIC.getText());

            v = u;
        } else if (!v.getIsCitizen()) {
            NonCitizen u = (NonCitizen) v;

            u.setPassport(txtPpIC.getText());

            v = u;
        }

        if (fo.ModifyRecord(v)) {
            InitGlobalData();
            InitTableRecords();

            General.AlertMsgInfo("People (" + v.Username + ") has been updated!", "Success");

        } else {
            General.AlertMsgError("Failed to update people (" + v.Username + "). Please try again later!", "Error");
        }
    }//GEN-LAST:event_btnSRegister1ActionPerformed

    private void txtPpNewPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPpNewPwKeyReleased
        txtPpCfmPw.setEnabled(txtPpNewPw.getPassword().length > 0);
        txtPpCfmPw.setText("");
    }//GEN-LAST:event_txtPpNewPwKeyReleased

    private void txtPpNewPwKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPpNewPwKeyTyped

    }//GEN-LAST:event_txtPpNewPwKeyTyped

    private void txtPpCfmPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPpCfmPwKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPpCfmPwKeyReleased

    private void tblPpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPpMouseClicked
        //Display data into text field when the specific row is selected
        String code = String.valueOf(tblPp.getValueAt(tblPp.getSelectedRow(), 1));
        txtPpCode.setText(code);

        //Retrieve the data
        People data = (People) htUser.get(code);

        txtPpFName.setText(data.getFirst_Name());
        txtPpLName.setText(data.getLast_Name());

        if (data.getGender() == General.GenderMale) {
            rbPpMale.setSelected(true);
        } else {
            rbPpFemale.setSelected(true);
        }

        calPpDob.setCalendar(data.Dob.getCal());
        txtPpContact.setText(data.getContact());
        txtPpEmail.setText(data.getEmail());

        txtPpAddNo.setText(data.Address.getNo());
        txtPpAddStreet.setText(data.Address.getStreet());
        txtPpAddCity.setText(data.Address.getCity());
        txtPpAddPost.setText(data.Address.getPostcode());
        cboPpAddState.setSelectedItem(data.Address.getState());

        if (data.getIsCitizen()) {
            Citizen c = (Citizen) data;
            txtPpNat.setText(General.NationalityCitizen);
            txtPpIC.setText(c.getIcNo());

        } else {
            NonCitizen c = (NonCitizen) data;
            txtPpNat.setText(General.NationalityNonCitizen);
            txtPpIC.setText(c.getPassport());
        }
    }//GEN-LAST:event_tblPpMouseClicked

    private void VSearch(String search, MyDateTime vacDate, Class nationality, Vaccine vaccine, VaccineCentre centre, AppointmentStatus status) {
        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblV.getModel();
        dtm.setRowCount(0);

        for (Object x : htAppointment.values()) {
            Appointment a = (Appointment) x;

            if (vacDate != null) {
                MyDateTime today = new MyDateTime(vacDate.getCal().get(Calendar.YEAR), vacDate.getCal().get(Calendar.MONTH), vacDate.getCal().get(Calendar.DATE));
                MyDateTime tomorrow = today;
                tomorrow.getCal().add(Calendar.DATE, 1);
                if (!(a.VaccinationDate.getCal().after(today)) && a.VaccinationDate.getCal().before(tomorrow)) {
                    continue;
                }
            }

            if (vaccine != null) {
                if (!a.Vacc.getVacCode().equals(vaccine.getVacCode())) {
                    continue;
                }
            }

            if (centre != null) {
                if (!a.Location.getVacCode().equals(centre.getVacCode())) {
                    continue;
                }
            }

            if (status != null) {
                if (!a.getStatus().equals(status)) {
                    continue;
                }
            }

            //Ppl type
            Class pplClass = a.Ppl.getClass();

            if (nationality != null) {
                if (!nationality.equals(pplClass)) {
                    continue;
                }
            }

            //Search bar check
            if (pplClass.equals(Citizen.class)) {
                Citizen c = (Citizen) a.Ppl;

                if ((c.getIcNo().toLowerCase().contains(search) || a.getCode().toLowerCase().contains(search) || a.Ppl.Username.toLowerCase().contains(search) || a.Ppl.getFullName().toLowerCase().contains(search))) {
                    Object[] dtmObj = new Object[]{
                        ++i,
                        a.getCode(),
                        a.Ppl.getFullName() + "(" + a.Ppl.Username + ")",
                        c.getIcNo() + "(" + General.NationalityCitizen + ")",
                        a.getRegisterDate().GetShortDate(),
                        a.Vacc != null ? a.Vacc.getVacCode() + " - " + a.Vacc.getName() : "-",
                        a.Location != null ? a.Location.getVacCode() + " - " + a.Location.getName() : "-",
                        a.VaccinationDate != null ? a.VaccinationDate.GetShortDate() : "-",
                        a.getRemarks(),
                        a.getHandledBy() != null ? a.getHandledBy().Username : "-",
                        a.getVaccinatedBy() != null ? a.getVaccinatedBy().Username : "-",
                        a.getStatus()
                    };

                    dtm.addRow(dtmObj);
                }
            } else {
                NonCitizen c = (NonCitizen) a.Ppl;
                if (c.getPassport().toLowerCase().contains(search) || a.getCode().toLowerCase().contains(search) || a.Ppl.Username.toLowerCase().contains(search) || a.Ppl.getFullName().toLowerCase().contains(search)) {
                    Object[] dtmObj = new Object[]{
                        ++i,
                        a.getCode(),
                        a.Ppl.getFullName() + "(" + a.Ppl.Username + ")",
                        c.getPassport() + "(" + General.NationalityNonCitizen + ")",
                        a.getRegisterDate().GetShortDate(),
                        a.Vacc != null ? a.Vacc.getVacCode() + " - " + a.Vacc.getName() : "-",
                        a.Location != null ? a.Location.getVacCode() + " - " + a.Location.getName() : "-",
                        a.VaccinationDate != null ? a.VaccinationDate.GetShortDate() : "-",
                        a.getRemarks(),
                        a.getHandledBy() != null ? a.getHandledBy().Username : "-",
                        a.getVaccinatedBy() != null ? a.getVaccinatedBy().Username : "-",
                        a.getStatus()
                    };

                    dtm.addRow(dtmObj);
                }
            }

        }

        tblV.setModel(dtm);
    }

    private void CMSearch(String search, String state) {
        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblCM.getModel();
        dtm.setRowCount(0);

        for (Object x : htVacCentre.values()) {
            VaccineCentre a = (VaccineCentre) x;

            if (!state.isBlank()) {
                if (!a.getVacAddress().getState().equals(state)) {
                    continue;
                }
            }

            if (a.getVacCode().toLowerCase().contains(search) || a.getName().toLowerCase().contains(search) || a.getVacAddress().getStreet().toLowerCase().contains(search) || a.getVacAddress().getNo().toLowerCase().contains(search) || a.getVacAddress().getCity().toLowerCase().contains(search) || a.getVacAddress().getPostcode().toLowerCase().contains(search)) {

                Object[] dtmObj = new Object[]{
                    ++i,
                    a.getVacCode(),
                    a.getName(),
                    a.getVacAddress().getNo(),
                    a.getVacAddress().getStreet(),
                    a.getVacAddress().getCity(),
                    a.getVacAddress().getPostcode(),
                    a.getVacAddress().getState()
                };

                dtm.addRow(dtmObj);
            }
        }

        tblCM.setModel(dtm);
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AdminLoadingPage.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdminLoadingPage.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdminLoadingPage.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdminLoadingPage.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdminLoadingPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdPInfoEdit;
    private javax.swing.JButton btnAdPSave;
    private javax.swing.JButton btnAnvAdd;
    private javax.swing.JButton btnCMSearch;
    private javax.swing.JButton btnCncAdd;
    private javax.swing.JButton btnCncAdd1;
    private javax.swing.JButton btnComSave;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMASearch;
    private javax.swing.JButton btnMCSearch;
    private javax.swing.JButton btnMaApprove;
    private javax.swing.JButton btnMaDecline;
    private javax.swing.JButton btnMaSubmit;
    private javax.swing.JButton btnMaSubmit3;
    private javax.swing.JButton btnPpSearch;
    private javax.swing.JButton btnRASearch;
    private javax.swing.JButton btnRaCancel;
    private javax.swing.JButton btnRaSubmit;
    private javax.swing.JButton btnSClear;
    private javax.swing.JButton btnSRegister;
    private javax.swing.JButton btnSRegister1;
    private javax.swing.JButton btnVMSearch;
    private javax.swing.JButton btnVSearch;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private com.toedter.calendar.JDateChooser calAdPHiredDate;
    private com.toedter.calendar.JDateChooser calComDob;
    private com.toedter.calendar.JDateChooser calComHiredDate;
    private com.toedter.calendar.JDateChooser calMaDob;
    private com.toedter.calendar.JDateChooser calMaVacDate;
    private com.toedter.calendar.JDateChooser calMaVacDate2;
    private com.toedter.calendar.JDateChooser calPpDob;
    private com.toedter.calendar.JDateChooser calRaDob;
    private com.toedter.calendar.JDateChooser calRaVacDate;
    private com.toedter.calendar.JDateChooser calRaVacDate2;
    private com.toedter.calendar.JDateChooser calSDob;
    private com.toedter.calendar.JDateChooser calVSearchVacDate;
    private javax.swing.JComboBox<String> cboAdPGender;
    private javax.swing.JComboBox<String> cboCMStateSearch;
    private javax.swing.JComboBox<String> cboCncState;
    private javax.swing.JComboBox<String> cboCncState1;
    private javax.swing.JComboBox<String> cboComRole;
    private javax.swing.JComboBox<String> cboComStatus;
    private javax.swing.JComboBox<String> cboComVacCentre;
    private javax.swing.JComboBox<String> cboMCSearchRole;
    private javax.swing.JComboBox<String> cboMCSearchStatus;
    private javax.swing.JComboBox<String> cboMCSearchVacCentre;
    private javax.swing.JComboBox<String> cboMaVac;
    private javax.swing.JComboBox<String> cboMaVac2;
    private javax.swing.JComboBox<String> cboMaVacCentre;
    private javax.swing.JComboBox<String> cboMaVacCentre2;
    private javax.swing.JComboBox<String> cboPpAddState;
    private javax.swing.JComboBox<String> cboPpSearchState;
    private javax.swing.JComboBox<String> cboPpSearchStatus;
    private javax.swing.JComboBox<String> cboRaVac;
    private javax.swing.JComboBox<String> cboRaVacCentre;
    private javax.swing.JComboBox<String> cboSRole;
    private javax.swing.JComboBox<String> cboSVacCentre;
    private javax.swing.JComboBox<String> cboVSearchNat;
    private javax.swing.JComboBox<String> cboVSearchStatus;
    private javax.swing.JComboBox<String> cboVSearchVac;
    private javax.swing.JComboBox<String> cboVSearchVacCentre;
    private com.toedter.calendar.JDateChooser jDob;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel112;
    private javax.swing.JLabel jLabel113;
    private javax.swing.JLabel jLabel114;
    private javax.swing.JLabel jLabel115;
    private javax.swing.JLabel jLabel116;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel125;
    private javax.swing.JLabel jLabel126;
    private javax.swing.JLabel jLabel127;
    private javax.swing.JLabel jLabel128;
    private javax.swing.JLabel jLabel129;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel130;
    private javax.swing.JLabel jLabel131;
    private javax.swing.JLabel jLabel132;
    private javax.swing.JLabel jLabel133;
    private javax.swing.JLabel jLabel134;
    private javax.swing.JLabel jLabel135;
    private javax.swing.JLabel jLabel136;
    private javax.swing.JLabel jLabel137;
    private javax.swing.JLabel jLabel138;
    private javax.swing.JLabel jLabel139;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel140;
    private javax.swing.JLabel jLabel141;
    private javax.swing.JLabel jLabel142;
    private javax.swing.JLabel jLabel143;
    private javax.swing.JLabel jLabel144;
    private javax.swing.JLabel jLabel145;
    private javax.swing.JLabel jLabel146;
    private javax.swing.JLabel jLabel147;
    private javax.swing.JLabel jLabel148;
    private javax.swing.JLabel jLabel149;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel150;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel152;
    private javax.swing.JLabel jLabel153;
    private javax.swing.JLabel jLabel154;
    private javax.swing.JLabel jLabel155;
    private javax.swing.JLabel jLabel156;
    private javax.swing.JLabel jLabel157;
    private javax.swing.JLabel jLabel158;
    private javax.swing.JLabel jLabel159;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JSplitPane jSplitPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JLabel lblPwNoMatch;
    private javax.swing.JLabel lblVSName2;
    private javax.swing.JLabel lblVSName3;
    private javax.swing.JLabel lblWbUsername;
    private javax.swing.JPanel pnlApprovedAppointment;
    private javax.swing.JPanel pnlApprovedAppointment2;
    private javax.swing.JPanel pnlApprovedAppointment4;
    private javax.swing.JPanel pnlApprovedAppointment5;
    private javax.swing.JPanel pnlComVacCentre;
    private javax.swing.JPanel pnlCredential;
    private javax.swing.JRadioButton rbComFemale;
    private javax.swing.JRadioButton rbComMale;
    private javax.swing.JRadioButton rbPpFemale;
    private javax.swing.JRadioButton rbPpMale;
    private javax.swing.JRadioButton rbSFemale;
    private javax.swing.JRadioButton rbSMale;
    private javax.swing.JTable tblCM;
    private javax.swing.JTable tblMA;
    private javax.swing.JTable tblMC;
    private javax.swing.JTable tblPp;
    private javax.swing.JTable tblRA;
    private javax.swing.JTable tblV;
    private javax.swing.JTable tblVM;
    private javax.swing.JTextField txtAdPName;
    private javax.swing.JTextField txtAdPNo;
    private javax.swing.JTextField txtAdPRole;
    private javax.swing.JTextField txtAdPUser;
    private javax.swing.JTextField txtAnvDose;
    private javax.swing.JTextField txtAnvInterval;
    private javax.swing.JTextField txtAnvName;
    private javax.swing.JTextField txtCMSearch;
    private javax.swing.JTextField txtCncCity;
    private javax.swing.JTextField txtCncCity1;
    private javax.swing.JTextField txtCncCode;
    private javax.swing.JTextField txtCncName;
    private javax.swing.JTextField txtCncName1;
    private javax.swing.JTextField txtCncNo;
    private javax.swing.JTextField txtCncNo1;
    private javax.swing.JTextField txtCncPost;
    private javax.swing.JTextField txtCncPost1;
    private javax.swing.JTextField txtCncStreet;
    private javax.swing.JTextField txtCncStreet1;
    private javax.swing.JPasswordField txtComCfmPw;
    private javax.swing.JTextField txtComCode;
    private javax.swing.JTextField txtComContact;
    private javax.swing.JTextField txtComEmail;
    private javax.swing.JTextField txtComFName;
    private javax.swing.JTextField txtComLName;
    private javax.swing.JPasswordField txtComNewPw;
    private javax.swing.JTextField txtMCSearch;
    private javax.swing.JTextField txtMaAddress;
    private javax.swing.JTextField txtMaAppCode;
    private javax.swing.JTextField txtMaFullname;
    private javax.swing.JTextField txtMaGender;
    private javax.swing.JTextField txtMaIC;
    private javax.swing.JTextField txtMaNat;
    private javax.swing.JTextField txtMaRemarks;
    private javax.swing.JTextField txtMaRemarks2;
    private javax.swing.JTextField txtMaSearch;
    private javax.swing.JTextField txtMaUsername;
    private javax.swing.JTextField txtMaVacAdd;
    private javax.swing.JTextField txtMaVacAdd2;
    private javax.swing.JPasswordField txtPCfmPw;
    private javax.swing.JTextField txtPEmail;
    private javax.swing.JPasswordField txtPNewPw;
    private javax.swing.JTextField txtPpAddCity;
    private javax.swing.JTextField txtPpAddNo;
    private javax.swing.JTextField txtPpAddPost;
    private javax.swing.JTextField txtPpAddStreet;
    private javax.swing.JPasswordField txtPpCfmPw;
    private javax.swing.JTextField txtPpCode;
    private javax.swing.JTextField txtPpContact;
    private javax.swing.JTextField txtPpEmail;
    private javax.swing.JTextField txtPpFName;
    private javax.swing.JTextField txtPpIC;
    private javax.swing.JTextField txtPpLName;
    private javax.swing.JTextField txtPpNat;
    private javax.swing.JPasswordField txtPpNewPw;
    private javax.swing.JTextField txtPpSearch;
    private javax.swing.JTextField txtRaAddress;
    private javax.swing.JTextField txtRaAppCode;
    private javax.swing.JTextField txtRaFullname;
    private javax.swing.JTextField txtRaGender;
    private javax.swing.JTextField txtRaIC;
    private javax.swing.JTextField txtRaNat;
    private javax.swing.JTextField txtRaReason;
    private javax.swing.JTextField txtRaRemarks;
    private javax.swing.JTextField txtRaRemarks2;
    private javax.swing.JTextField txtRaSearch;
    private javax.swing.JTextField txtRaUsername;
    private javax.swing.JTextField txtRaVac;
    private javax.swing.JTextField txtRaVacAdd;
    private javax.swing.JTextField txtRaVacAdd2;
    private javax.swing.JTextField txtRaVacCentre;
    private javax.swing.JPasswordField txtSCfmPass;
    private javax.swing.JTextField txtSContact;
    private javax.swing.JTextField txtSEmail;
    private javax.swing.JTextField txtSFName;
    private javax.swing.JTextField txtSLName;
    private javax.swing.JPasswordField txtSPass;
    private javax.swing.JTextField txtVMSearch;
    private javax.swing.JTextField txtVSearch;
    // End of variables declaration//GEN-END:variables
}
