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
import java.util.Hashtable;
import java.util.regex.Pattern;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import vaccinationsystem.Appointment.AppointmentStatus;

/**
 *
 * @author ljk99
 */
public class AdminLoadingPage extends javax.swing.JFrame {

    private static Admin currentUser;
    private Hashtable<String, Object> htVac;
    private Hashtable<String, Object> htVacCentre;
    private Hashtable<String, Object> htAppointment;

    /**
     * Creates new form AdminLoadingPage
     */
    public AdminLoadingPage() {
        initComponents();
        ComponentReset();
        
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        
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

    }

    private void InitComboData() {
        //---Profile Tab---
        //Init Gender ComboBox
        General.GenderString().forEach(d -> cboAdPGender.addItem(d));

        //---Vaccination Appointment---
        //Init Vaccine Types (VM)
        for (Object x : htVac.values()) {
            Vaccine v = (Vaccine) x;
            cboMaVac.addItem(v.getVacCode() + " - " + v.getName());
        }

        //Init Nationality (V)
        General.Nationalities().forEach(d -> cboVSearchNat.addItem(d));

        //Init Vaccine Types (V)
        for (Object x : htVac.values()) {
            Vaccine v = (Vaccine) x;
            cboVSearchVac.addItem(v.getVacCode() + " - " + v.getName());
        }

        //Init Vaccine Centre (V)
        for (Object x : htVacCentre.values()) {
            VaccineCentre v = (VaccineCentre) x;
            cboVSearchVacCentre.addItem(v.getVacCode() + " - " + v.getName());
        }

        //---Vaccine Centre Management---
        General.MalaysiaStates().forEach(d -> cboCMStateSearch.addItem(d));
        General.MalaysiaStates().forEach(d -> cboCncState.addItem(d));

    }

    private void PopulateUserData() {
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

        for (Object x : htAppointment.keySet()) {
            Appointment a = (Appointment) htAppointment.get(x);

            if (!(a.getStatus() != AppointmentStatus.Approved || a.getStatus() != AppointmentStatus.Cancelled || a.getStatus() != AppointmentStatus.Pending)) {
                continue;
            }

            String IcPassport = "";
            if (a.Ppl.getClass().equals(Citizen.class)) {
                Citizen c = (Citizen) a.Ppl;
                IcPassport = c.getIcNo() + " (" + General.NationalityMalaysian + ")";
            } else {
                NonCitizen c = (NonCitizen) a.Ppl;
                IcPassport = c.getPassport() + " (" + General.NationalityNonMalaysian + ")";
            }

            Object[] dtmObj = new Object[]{
                a.getCode(),
                a.Ppl.getFullName() + " (" + a.Ppl.Username + ")",
                IcPassport,
                a.RegisterDate.GetShortDateTime(),
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
            if (v.Ppl.getClass().equals(Citizen.class)) {
                Citizen c = (Citizen) v.Ppl;
                IcPassport = c.getIcNo() + " (" + General.NationalityMalaysian + ")";
            } else {
                NonCitizen c = (NonCitizen) v.Ppl;
                IcPassport = c.getPassport() + " (" + General.NationalityNonMalaysian + ")";
            }

            Object[] dtmObj = new Object[]{
                ++vacVCount,
                v.getCode(),
                v.Ppl.getFullName() + " (" + v.Ppl.Username + ")",
                IcPassport,
                v.RegisterDate.GetShortDate(),
                v.Vacc == null ? "-" : (v.Vacc.getName() + " (" + v.Vacc.getVacCode() + ")"),
                v.Location == null ? "-" : (v.Location.getName() + " (" + v.Location.getVacCode() + ")"),
                v.VaccinationDate == null ? "-" : v.VaccinationDate.GetShortDate(),
                v.getRemarks(),
                v.HandledBy == null ? "-" : v.HandledBy.Username,
                v.getVaccinatedBy() == null ? "-" : (v.getVaccinatedBy().Username),
                v.getStatus()
            };

            dtmV.addRow(dtmObj);

        }

        tblV.setModel(dtmV);

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

        dtmVacCentre.setRowCount(
                0);
        int vacCentreCount = 0;

        for (Object x
                : htVacCentre.keySet()) {
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        lblVSName1 = new javax.swing.JLabel();
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
        jLabel57 = new javax.swing.JLabel();
        txtPEmail = new javax.swing.JTextField();
        txtPNewPw = new javax.swing.JPasswordField();
        txtPCfmPw = new javax.swing.JPasswordField();
        jLabel99 = new javax.swing.JLabel();
        lblPwNoMatch = new javax.swing.JLabel();
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
        jSeparator1 = new javax.swing.JSeparator();
        txtMaSearch = new javax.swing.JTextField();
        btnMASearch = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblRA = new javax.swing.JTable();
        txtRaSearch = new javax.swing.JTextField();
        btnRASearch = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        txtRaVC = new javax.swing.JTextField();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel69 = new javax.swing.JLabel();
        txtRaVN = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        txtRaRemarks = new javax.swing.JTextField();
        btnRaSubmit = new javax.swing.JButton();
        jLabel71 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        txtRaStats = new javax.swing.JTextField();
        txtRaNat = new javax.swing.JTextField();
        txtRaIC = new javax.swing.JTextField();
        txtRaGender = new javax.swing.JTextField();
        txtRaName = new javax.swing.JTextField();
        btnRaApprove = new javax.swing.JButton();
        btnRaDecline = new javax.swing.JButton();
        jLabel76 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
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
        jScrollPane10 = new javax.swing.JScrollPane();
        jTabbedPane6 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtAdNat = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        txtAdName = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtAdIC = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtAdNo = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        txtAdDob = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtAdCPass = new javax.swing.JTextField();
        txtAdPass = new javax.swing.JTextField();
        txtAdEmail = new javax.swing.JTextField();
        btnAdRegister = new javax.swing.JButton();
        cboAdGender = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        txtDcName = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        txtDrIC = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txtDrNat = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        txtDrDob = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtDrNo = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        txtDrCPass = new javax.swing.JTextField();
        txtDrPass = new javax.swing.JTextField();
        txtDrEmail = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        cboDrGender = new javax.swing.JComboBox<>();
        jLabel39 = new javax.swing.JLabel();
        txtDrVac = new javax.swing.JTextField();
        btnDrRegister = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        txtSName1 = new javax.swing.JTextField();
        cboSGender = new javax.swing.JComboBox<>();
        txtSIC = new javax.swing.JTextField();
        txtSNat = new javax.swing.JTextField();
        txtSDob = new javax.swing.JTextField();
        txtSNo = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        txtSEmail = new javax.swing.JTextField();
        txtSPass = new javax.swing.JTextField();
        txtSCPass = new javax.swing.JTextField();
        btnSRegister = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        txtMaSearch1 = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblPp = new javax.swing.JTable();
        btnPpSearch = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jLabel83 = new javax.swing.JLabel();
        txtPpNPass = new javax.swing.JTextField();
        txtPpEmail = new javax.swing.JTextField();
        txtPpNat = new javax.swing.JTextField();
        txtPpIC = new javax.swing.JTextField();
        txtPpName = new javax.swing.JTextField();
        btnPpUpdate = new javax.swing.JButton();
        jLabel84 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        cboPpStatus = new javax.swing.JComboBox<>();
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
        btnVmUpdate = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        tblCM = new javax.swing.JTable();
        txtCMSearch = new javax.swing.JTextField();
        btnCMSearch = new javax.swing.JButton();
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
        btnCncUpdate = new javax.swing.JButton();
        cboCMStateSearch = new javax.swing.JComboBox<>();

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

        lblVSName1.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 20)); // NOI18N
        lblVSName1.setForeground(new java.awt.Color(0, 0, 0));
        lblVSName1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVSName1.setText("Admin Panel");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblVSName1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(lblVSName1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(123, Short.MAX_VALUE))
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
                .addContainerGap(35, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(122, 122, 122)
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
        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
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

        jLabel57.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(255, 255, 255));
        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel57.setText("Confirm Password:");

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

        javax.swing.GroupLayout pnlCredentialLayout = new javax.swing.GroupLayout(pnlCredential);
        pnlCredential.setLayout(pnlCredentialLayout);
        pnlCredentialLayout.setHorizontalGroup(
            pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlCredentialLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlCredentialLayout.createSequentialGroup()
                        .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel57, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPwNoMatch, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtPCfmPw, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                        .addComponent(txtPNewPw, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtPEmail, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel99, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(662, 662, 662))
        );
        pnlCredentialLayout.setVerticalGroup(
            pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCredentialLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(txtPEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPNewPw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel55))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel99)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPCfmPw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPwNoMatch)
                .addContainerGap(36, Short.MAX_VALUE))
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(347, 347, 347))
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
                                        .addComponent(calAdPHiredDate, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(692, Short.MAX_VALUE))))
            .addComponent(pnlCredential, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(381, 381, 381)
                .addComponent(btnAdPSave, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAdPInfoEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
                .addContainerGap(306, Short.MAX_VALUE))
        );

        jScrollPane8.setViewportView(jPanel4);

        jTabbedPane1.addTab("Profile", jScrollPane8);

        jTabbedPane5.setBackground(new java.awt.Color(51, 51, 51));
        jTabbedPane5.setOpaque(true);

        jPanel18.setBackground(new java.awt.Color(51, 51, 51));
        jPanel18.setPreferredSize(new java.awt.Dimension(1221, 900));

        tblMA.setForeground(new java.awt.Color(204, 204, 204));
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
                .addGap(36, 36, 36)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(txtMaSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMASearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pnlApprovedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 187, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnMASearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMaSearch))
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pnlApprovedAppointment, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 730, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Schedule Appointment", jPanel18);

        jPanel20.setBackground(new java.awt.Color(51, 51, 51));

        tblRA.setForeground(new java.awt.Color(204, 204, 204));
        tblRA.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Full Name", "Gender", "IC/Passport", "Nationality", "Status"
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

        jPanel10.setBackground(new java.awt.Color(255, 204, 51));

        jLabel67.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel67.setForeground(new java.awt.Color(0, 0, 0));
        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel67.setText("Vaccination Centre:");

        jLabel68.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(0, 0, 0));
        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel68.setText("Vaccination Date:");

        txtRaVC.setBackground(new java.awt.Color(255, 255, 255));
        txtRaVC.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaVC.setForeground(new java.awt.Color(0, 0, 0));
        txtRaVC.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaVC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRaVC.setOpaque(true);
        txtRaVC.setSelectionColor(new java.awt.Color(255, 255, 51));

        jDateChooser2.setBackground(new java.awt.Color(255, 255, 255));
        jDateChooser2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel69.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(0, 0, 0));
        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel69.setText("Vaccine:");

        txtRaVN.setBackground(new java.awt.Color(255, 255, 255));
        txtRaVN.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaVN.setForeground(new java.awt.Color(0, 0, 0));
        txtRaVN.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaVN.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRaVN.setOpaque(true);
        txtRaVN.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel70.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel70.setForeground(new java.awt.Color(0, 0, 0));
        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel70.setText("Remarks:");

        txtRaRemarks.setBackground(new java.awt.Color(255, 255, 255));
        txtRaRemarks.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaRemarks.setForeground(new java.awt.Color(0, 0, 0));
        txtRaRemarks.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtRaRemarks.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtRaRemarks.setOpaque(true);
        txtRaRemarks.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnRaSubmit.setBackground(new java.awt.Color(102, 255, 102));
        btnRaSubmit.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnRaSubmit.setForeground(new java.awt.Color(0, 0, 0));
        btnRaSubmit.setText("Submit");
        btnRaSubmit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnRaSubmit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRaSubmit.setOpaque(true);
        btnRaSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRaSubmitActionPerformed(evt);
            }
        });

        jLabel71.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(0, 0, 0));
        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel71.setText("Appointment Information");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(btnRaSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel70, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel69, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel68, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel67, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRaVC)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtRaVN)
                            .addComponent(txtRaRemarks)))
                    .addComponent(jLabel71, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel71)
                .addGap(26, 26, 26)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(txtRaRemarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel68)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel69)
                    .addComponent(txtRaVN))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(txtRaVC))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(btnRaSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(137, 137, 137))
        );

        jPanel11.setBackground(new java.awt.Color(51, 153, 255));

        jLabel14.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 0, 0));
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("Full Name:");

        jLabel72.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel72.setForeground(new java.awt.Color(0, 0, 0));
        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel72.setText("Gender:");

        jLabel73.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel73.setForeground(new java.awt.Color(0, 0, 0));
        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel73.setText("IC/Passport:");

        jLabel74.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel74.setForeground(new java.awt.Color(0, 0, 0));
        jLabel74.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel74.setText("Nationality:");

        jLabel75.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel75.setForeground(new java.awt.Color(0, 0, 0));
        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel75.setText("Status");

        txtRaStats.setEditable(false);
        txtRaStats.setBackground(new java.awt.Color(204, 204, 204));
        txtRaStats.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaStats.setForeground(new java.awt.Color(0, 0, 0));
        txtRaStats.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaStats.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtRaNat.setEditable(false);
        txtRaNat.setBackground(new java.awt.Color(204, 204, 204));
        txtRaNat.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaNat.setForeground(new java.awt.Color(0, 0, 0));
        txtRaNat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaNat.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtRaIC.setEditable(false);
        txtRaIC.setBackground(new java.awt.Color(204, 204, 204));
        txtRaIC.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaIC.setForeground(new java.awt.Color(0, 0, 0));
        txtRaIC.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaIC.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtRaGender.setEditable(false);
        txtRaGender.setBackground(new java.awt.Color(204, 204, 204));
        txtRaGender.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaGender.setForeground(new java.awt.Color(0, 0, 0));
        txtRaGender.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaGender.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtRaName.setEditable(false);
        txtRaName.setBackground(new java.awt.Color(204, 204, 204));
        txtRaName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtRaName.setForeground(new java.awt.Color(0, 0, 0));
        txtRaName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtRaName.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnRaApprove.setBackground(new java.awt.Color(102, 255, 102));
        btnRaApprove.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnRaApprove.setForeground(new java.awt.Color(0, 0, 0));
        btnRaApprove.setText("Reschedule");
        btnRaApprove.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnRaApprove.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRaApprove.setOpaque(true);
        btnRaApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRaApproveActionPerformed(evt);
            }
        });

        btnRaDecline.setBackground(new java.awt.Color(255, 51, 51));
        btnRaDecline.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnRaDecline.setForeground(new java.awt.Color(0, 0, 0));
        btnRaDecline.setText("Cancel");
        btnRaDecline.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));
        btnRaDecline.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnRaDecline.setOpaque(true);
        btnRaDecline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRaDeclineActionPerformed(evt);
            }
        });

        jLabel76.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel76.setForeground(new java.awt.Color(0, 0, 0));
        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel76.setText("Appointment Verification");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel76, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel73, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel74, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel75, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel72, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 207, Short.MAX_VALUE)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                                .addComponent(btnRaDecline, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRaApprove, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtRaGender, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtRaName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtRaIC, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtRaStats, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtRaNat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(63, 63, 63))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel76, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRaName)
                    .addComponent(jLabel14))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRaGender)
                    .addComponent(jLabel72))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRaIC)
                    .addComponent(jLabel73))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel74)
                    .addComponent(txtRaNat))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtRaStats)
                    .addComponent(jLabel75))
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRaDecline, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRaApprove, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(txtRaSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRASearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator2))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnRASearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRaSearch))
                .addGap(27, 27, 27)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 11, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 667, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Reschedule Appointment", jPanel20);

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

        tblV.setForeground(new java.awt.Color(204, 204, 204));
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

        cboVSearchVac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Vaccine" }));

        cboVSearchStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Status" }));

        cboVSearchVacCentre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Vaccine Centre" }));

        calVSearchVacDate.setToolTipText("Vaccination Date");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 1084, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(txtVSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnVSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(calVSearchVacDate, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboVSearchNat, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboVSearchVac, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboVSearchVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboVSearchStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnVSearch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVSearch)
                    .addComponent(cboVSearchStatus)
                    .addComponent(cboVSearchVacCentre)
                    .addComponent(calVSearchVacDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboVSearchNat)
                    .addComponent(cboVSearchVac))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("View Appointment", jPanel12);

        jScrollPane11.setViewportView(jTabbedPane5);

        jTabbedPane1.addTab("Appointment Management", jScrollPane11);

        jTabbedPane6.setBackground(new java.awt.Color(51, 51, 51));
        jTabbedPane6.setForeground(new java.awt.Color(204, 204, 204));
        jTabbedPane6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTabbedPane6.setOpaque(true);

        jPanel3.setBackground(new java.awt.Color(255, 102, 102));
        jPanel3.setForeground(new java.awt.Color(204, 204, 204));

        jLabel16.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(0, 0, 0));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Basic Information");

        jLabel17.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(0, 0, 0));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Nationality:");

        txtAdNat.setEditable(false);
        txtAdNat.setBackground(new java.awt.Color(204, 204, 204));
        txtAdNat.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdNat.setForeground(new java.awt.Color(0, 0, 0));
        txtAdNat.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtAdNat.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel18.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(0, 0, 0));
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Full Name:");

        txtAdName.setBackground(new java.awt.Color(255, 255, 255));
        txtAdName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdName.setForeground(new java.awt.Color(0, 0, 0));
        txtAdName.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtAdName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdName.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel19.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 0, 0));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("Gender:");

        jLabel20.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 0, 0));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("IC/Passport");

        txtAdIC.setBackground(new java.awt.Color(255, 255, 255));
        txtAdIC.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdIC.setForeground(new java.awt.Color(0, 0, 0));
        txtAdIC.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtAdIC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdIC.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtAdIC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtAdICKeyPressed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 0, 0));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Contact No:");

        txtAdNo.setBackground(new java.awt.Color(255, 255, 255));
        txtAdNo.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdNo.setForeground(new java.awt.Color(0, 0, 0));
        txtAdNo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtAdNo.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdNo.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel22.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 0, 0));
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("Date of Birth:");

        txtAdDob.setBackground(new java.awt.Color(255, 255, 255));
        txtAdDob.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdDob.setForeground(new java.awt.Color(0, 0, 0));
        txtAdDob.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtAdDob.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdDob.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel23.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(0, 0, 0));
        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel23.setText("Account Information");

        jLabel24.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(0, 0, 0));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("E-mail");

        jLabel25.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 0, 0));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Password:");

        jLabel26.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Confirm Password:");

        txtAdCPass.setBackground(new java.awt.Color(255, 255, 255));
        txtAdCPass.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdCPass.setForeground(new java.awt.Color(0, 0, 0));
        txtAdCPass.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtAdCPass.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdCPass.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtAdPass.setBackground(new java.awt.Color(255, 255, 255));
        txtAdPass.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdPass.setForeground(new java.awt.Color(0, 0, 0));
        txtAdPass.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtAdPass.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdPass.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtAdEmail.setBackground(new java.awt.Color(255, 255, 255));
        txtAdEmail.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtAdEmail.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtAdEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdEmail.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnAdRegister.setBackground(new java.awt.Color(102, 255, 102));
        btnAdRegister.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnAdRegister.setForeground(new java.awt.Color(0, 0, 0));
        btnAdRegister.setText("Register");
        btnAdRegister.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnAdRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdRegister.setOpaque(true);
        btnAdRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdRegisterActionPerformed(evt);
            }
        });

        cboAdGender.setBackground(new java.awt.Color(255, 255, 255));
        cboAdGender.setForeground(new java.awt.Color(0, 0, 0));
        cboAdGender.setMaximumRowCount(2);
        cboAdGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Male", "Female" }));
        cboAdGender.setOpaque(true);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(txtAdNat, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(txtAdName, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtAdIC, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                                    .addComponent(cboAdGender, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtAdDob, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAdNo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 667, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtAdPass, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAdCPass, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtAdEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnAdRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(169, 169, 169))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtAdName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(cboAdGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtAdIC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtAdNat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtAdDob))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtAdNo))
                .addGap(57, 57, 57)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(txtAdEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(txtAdPass))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(txtAdCPass))
                .addGap(32, 32, 32)
                .addComponent(btnAdRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(453, 453, 453))
        );

        jTabbedPane6.addTab("Admin", jPanel3);

        jPanel7.setBackground(new java.awt.Color(255, 204, 51));

        jLabel27.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 0, 0));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel27.setText("Basic Information");

        jLabel28.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(0, 0, 0));
        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel28.setText("Full Name:");

        jLabel29.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(0, 0, 0));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("Gender:");

        txtDcName.setBackground(new java.awt.Color(255, 255, 255));
        txtDcName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtDcName.setForeground(new java.awt.Color(0, 0, 0));
        txtDcName.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDcName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDcName.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel30.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 0, 0));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("IC/Passport");

        txtDrIC.setBackground(new java.awt.Color(255, 255, 255));
        txtDrIC.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtDrIC.setForeground(new java.awt.Color(0, 0, 0));
        txtDrIC.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDrIC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDrIC.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtDrIC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDrICKeyPressed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(0, 0, 0));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Nationality:");

        txtDrNat.setEditable(false);
        txtDrNat.setBackground(new java.awt.Color(204, 204, 204));
        txtDrNat.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtDrNat.setForeground(new java.awt.Color(0, 0, 0));
        txtDrNat.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDrNat.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel32.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(0, 0, 0));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("Date of Birth:");

        txtDrDob.setBackground(new java.awt.Color(255, 255, 255));
        txtDrDob.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtDrDob.setForeground(new java.awt.Color(0, 0, 0));
        txtDrDob.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDrDob.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDrDob.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel33.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(0, 0, 0));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel33.setText("Contact No:");

        txtDrNo.setBackground(new java.awt.Color(255, 255, 255));
        txtDrNo.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtDrNo.setForeground(new java.awt.Color(0, 0, 0));
        txtDrNo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDrNo.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDrNo.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel34.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(0, 0, 0));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel34.setText("E-mail");

        jLabel35.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(0, 0, 0));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("Password:");

        jLabel36.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(0, 0, 0));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel36.setText("Confirm Password:");

        txtDrCPass.setBackground(new java.awt.Color(255, 255, 255));
        txtDrCPass.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtDrCPass.setForeground(new java.awt.Color(0, 0, 0));
        txtDrCPass.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDrCPass.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDrCPass.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtDrPass.setBackground(new java.awt.Color(255, 255, 255));
        txtDrPass.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtDrPass.setForeground(new java.awt.Color(0, 0, 0));
        txtDrPass.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDrPass.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDrPass.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtDrEmail.setBackground(new java.awt.Color(255, 255, 255));
        txtDrEmail.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtDrEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtDrEmail.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDrEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDrEmail.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel37.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(0, 0, 0));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel37.setText("Account Information");

        cboDrGender.setBackground(new java.awt.Color(255, 255, 255));
        cboDrGender.setForeground(new java.awt.Color(0, 0, 0));
        cboDrGender.setMaximumRowCount(2);
        cboDrGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Male", "Female" }));
        cboDrGender.setOpaque(true);

        jLabel39.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(0, 0, 0));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel39.setText("Vaccination Centre:");

        txtDrVac.setBackground(new java.awt.Color(255, 255, 255));
        txtDrVac.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtDrVac.setForeground(new java.awt.Color(0, 0, 0));
        txtDrVac.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtDrVac.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtDrVac.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnDrRegister.setBackground(new java.awt.Color(102, 255, 102));
        btnDrRegister.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnDrRegister.setForeground(new java.awt.Color(0, 0, 0));
        btnDrRegister.setText("Register");
        btnDrRegister.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnDrRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDrRegister.setOpaque(true);
        btnDrRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDrRegisterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(txtDrNat, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(txtDcName, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(567, 567, 567)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboDrGender, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDrIC, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDrDob, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDrNo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtDrPass, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDrCPass, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDrEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(txtDrVac, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnDrRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel27, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(169, 169, 169))))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(txtDcName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(cboDrGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(txtDrIC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel31)
                    .addComponent(txtDrNat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(txtDrDob))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(txtDrNo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel39)
                    .addComponent(txtDrVac))
                .addGap(33, 33, 33)
                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(txtDrEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(txtDrPass))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(txtDrCPass))
                .addGap(27, 27, 27)
                .addComponent(btnDrRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(441, 441, 441))
        );

        jTabbedPane6.addTab("Doctor", jPanel7);

        jPanel8.setBackground(new java.awt.Color(51, 102, 255));

        jLabel38.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(0, 0, 0));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel38.setText("Basic Information");

        jLabel40.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(0, 0, 0));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("Full Name:");

        jLabel41.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(0, 0, 0));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel41.setText("Gender:");

        jLabel42.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(0, 0, 0));
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel42.setText("IC/Passport");

        jLabel43.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(0, 0, 0));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel43.setText("Nationality:");

        jLabel44.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(0, 0, 0));
        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel44.setText("Date of Birth:");

        jLabel45.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel45.setForeground(new java.awt.Color(0, 0, 0));
        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel45.setText("Contact No:");

        txtSName1.setBackground(new java.awt.Color(255, 255, 255));
        txtSName1.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSName1.setForeground(new java.awt.Color(0, 0, 0));
        txtSName1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSName1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSName1.setSelectionColor(new java.awt.Color(255, 255, 51));

        cboSGender.setBackground(new java.awt.Color(255, 255, 255));
        cboSGender.setForeground(new java.awt.Color(0, 0, 0));
        cboSGender.setMaximumRowCount(2);
        cboSGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Male", "Female" }));
        cboSGender.setOpaque(true);

        txtSIC.setBackground(new java.awt.Color(255, 255, 255));
        txtSIC.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSIC.setForeground(new java.awt.Color(0, 0, 0));
        txtSIC.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSIC.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSIC.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtSNat.setEditable(false);
        txtSNat.setBackground(new java.awt.Color(204, 204, 204));
        txtSNat.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSNat.setForeground(new java.awt.Color(0, 0, 0));
        txtSNat.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSNat.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtSNat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSNatActionPerformed(evt);
            }
        });
        txtSNat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtSNatKeyPressed(evt);
            }
        });

        txtSDob.setBackground(new java.awt.Color(255, 255, 255));
        txtSDob.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSDob.setForeground(new java.awt.Color(0, 0, 0));
        txtSDob.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSDob.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSDob.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtSNo.setBackground(new java.awt.Color(255, 255, 255));
        txtSNo.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSNo.setForeground(new java.awt.Color(0, 0, 0));
        txtSNo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSNo.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSNo.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel46.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(0, 0, 0));
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("Account Information");

        jLabel47.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel47.setForeground(new java.awt.Color(0, 0, 0));
        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel47.setText("E-mail");

        jLabel48.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel48.setForeground(new java.awt.Color(0, 0, 0));
        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel48.setText("Password:");

        jLabel49.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel49.setForeground(new java.awt.Color(0, 0, 0));
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel49.setText("Confirm Password:");

        txtSEmail.setBackground(new java.awt.Color(255, 255, 255));
        txtSEmail.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtSEmail.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSEmail.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSEmail.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtSPass.setBackground(new java.awt.Color(255, 255, 255));
        txtSPass.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSPass.setForeground(new java.awt.Color(0, 0, 0));
        txtSPass.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSPass.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSPass.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtSCPass.setBackground(new java.awt.Color(255, 255, 255));
        txtSCPass.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtSCPass.setForeground(new java.awt.Color(0, 0, 0));
        txtSCPass.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSCPass.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtSCPass.setSelectionColor(new java.awt.Color(255, 255, 51));

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

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel46, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(txtSNat, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(txtSName1, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(567, 567, 567)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cboSGender, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSIC, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel44, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSDob, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSNo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSPass, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSCPass, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnSRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel38, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(169, 169, 169))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(txtSName1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(cboSGender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(txtSIC))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel43)
                    .addComponent(txtSNat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(txtSDob))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(txtSNo))
                .addGap(68, 68, 68)
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(txtSEmail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(txtSPass))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel49)
                    .addComponent(txtSCPass))
                .addGap(27, 27, 27)
                .addComponent(btnSRegister, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(441, 441, 441))
        );

        jTabbedPane6.addTab("Stockist", jPanel8);

        jScrollPane10.setViewportView(jTabbedPane6);

        jTabbedPane1.addTab("Committee Registration", jScrollPane10);

        jPanel9.setBackground(new java.awt.Color(51, 51, 51));

        txtMaSearch1.setBackground(new java.awt.Color(255, 255, 255));
        txtMaSearch1.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        txtMaSearch1.setForeground(new java.awt.Color(0, 0, 0));
        txtMaSearch1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtMaSearch1.setSelectionColor(new java.awt.Color(255, 255, 51));

        tblPp.setForeground(new java.awt.Color(204, 204, 204));
        tblPp.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Full Name", "Gender", "IC/Passport", "Nationality", "E-Mail", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPp.setSelectionBackground(new java.awt.Color(51, 51, 51));
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

        btnPpSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/search.png"))); // NOI18N
        btnPpSearch.setText("Search");
        btnPpSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnPpSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPpSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPpSearchActionPerformed(evt);
            }
        });

        jPanel13.setBackground(new java.awt.Color(51, 153, 255));

        jLabel79.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel79.setForeground(new java.awt.Color(0, 0, 0));
        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel79.setText("Full Name:");

        jLabel80.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel80.setForeground(new java.awt.Color(0, 0, 0));
        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel80.setText("Nationality:");

        jLabel81.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel81.setForeground(new java.awt.Color(0, 0, 0));
        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel81.setText("IC/Passport:");

        jLabel82.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel82.setForeground(new java.awt.Color(0, 0, 0));
        jLabel82.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel82.setText("E-mail:");

        jLabel83.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel83.setForeground(new java.awt.Color(0, 0, 0));
        jLabel83.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel83.setText("New Password:");

        txtPpNPass.setBackground(new java.awt.Color(255, 255, 255));
        txtPpNPass.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpNPass.setForeground(new java.awt.Color(0, 0, 0));
        txtPpNPass.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpNPass.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPpEmail.setBackground(new java.awt.Color(255, 255, 255));
        txtPpEmail.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtPpEmail.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpEmail.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPpNat.setEditable(false);
        txtPpNat.setBackground(new java.awt.Color(204, 204, 204));
        txtPpNat.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpNat.setForeground(new java.awt.Color(0, 0, 0));
        txtPpNat.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpNat.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtPpNat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPpNatKeyPressed(evt);
            }
        });

        txtPpIC.setBackground(new java.awt.Color(255, 255, 255));
        txtPpIC.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpIC.setForeground(new java.awt.Color(0, 0, 0));
        txtPpIC.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpIC.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPpName.setBackground(new java.awt.Color(255, 255, 255));
        txtPpName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPpName.setForeground(new java.awt.Color(0, 0, 0));
        txtPpName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtPpName.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnPpUpdate.setBackground(new java.awt.Color(102, 255, 102));
        btnPpUpdate.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnPpUpdate.setForeground(new java.awt.Color(0, 0, 0));
        btnPpUpdate.setText("Update");
        btnPpUpdate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnPpUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPpUpdate.setOpaque(true);
        btnPpUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPpUpdateActionPerformed(evt);
            }
        });

        jLabel84.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel84.setForeground(new java.awt.Color(0, 0, 0));
        jLabel84.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel84.setText("User Credentials");

        jLabel85.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel85.setForeground(new java.awt.Color(0, 0, 0));
        jLabel85.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel85.setText("Status:");

        cboPpStatus.setBackground(new java.awt.Color(255, 255, 255));
        cboPpStatus.setForeground(new java.awt.Color(0, 0, 0));
        cboPpStatus.setMaximumRowCount(2);
        cboPpStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Not Vaccinated", "Partially Vaccinated", "Fully Vaccinated" }));
        cboPpStatus.setOpaque(true);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel84, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(258, 258, 258)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel82, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel83)
                            .addComponent(jLabel85, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPpNPass, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPpEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                            .addComponent(cboPpStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel81, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel79, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel80, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(45, 45, 45)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnPpUpdate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPpIC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(txtPpName, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtPpNat, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGap(393, 393, 393))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel84, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPpName)
                    .addComponent(jLabel79))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPpIC, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel81))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel80)
                    .addComponent(txtPpNat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cboPpStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel82, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtPpEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPpNPass, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel83))
                .addGap(10, 10, 10)
                .addComponent(btnPpUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(txtMaSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPpSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane5)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMaSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPpSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jScrollPane9.setViewportView(jPanel9);

        jTabbedPane1.addTab("People Management", jScrollPane9);

        jTabbedPane4.setBackground(new java.awt.Color(51, 51, 51));
        jTabbedPane4.setOpaque(true);

        jPanel15.setBackground(new java.awt.Color(51, 51, 51));

        jPanel21.setBackground(new java.awt.Color(51, 51, 51));
        jPanel21.setPreferredSize(new java.awt.Dimension(1163, 1038));

        tblVM.setForeground(new java.awt.Color(204, 204, 204));
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
                false, false, true, true, true
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

        btnVmUpdate.setBackground(new java.awt.Color(0, 255, 0));
        btnVmUpdate.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnVmUpdate.setForeground(new java.awt.Color(0, 0, 0));
        btnVmUpdate.setText("Update Vaccine Details");
        btnVmUpdate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnVmUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnVmUpdate.setOpaque(true);
        btnVmUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVmUpdateActionPerformed(evt);
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
                        .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnVmUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(txtVMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnVMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(169, Short.MAX_VALUE))
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
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnVmUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(461, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, 1197, Short.MAX_VALUE)
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

        tblCM.setForeground(new java.awt.Color(204, 204, 204));
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
                                .addGap(0, 95, Short.MAX_VALUE))
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
                .addGap(11, 11, 11)
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
                .addGap(28, 28, 28)
                .addComponent(btnCncAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        btnCncUpdate.setBackground(new java.awt.Color(0, 255, 0));
        btnCncUpdate.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnCncUpdate.setForeground(new java.awt.Color(0, 0, 0));
        btnCncUpdate.setText("Update Vaccine Centre");
        btnCncUpdate.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnCncUpdate.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCncUpdate.setOpaque(true);
        btnCncUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCncUpdateActionPerformed(evt);
            }
        });

        cboCMStateSearch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All States" }));

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel23Layout.createSequentialGroup()
                        .addComponent(txtCMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboCMStateSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCncUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))
                .addGap(22, 22, 22))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCMSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCMSearch)
                    .addComponent(cboCMStateSearch))
                .addGap(18, 18, 18)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, 416, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCncUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 656, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(679, 679, 679))
        );

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, 1197, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, 1038, Short.MAX_VALUE)
        );

        jTabbedPane4.addTab("Centre Management", jPanel14);

        jScrollPane2.setViewportView(jTabbedPane4);

        jTabbedPane1.addTab("Vaccine & Centre Management", jScrollPane2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1081, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1007, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void txtAdICKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAdICKeyPressed
        String txtIC = txtAdIC.getText();

        if (!Pattern.matches("^[0-9]+$", txtIC)) {
            txtAdNat.setText("Non-Citizen");
        } else if (Pattern.matches("^[0-9]+$", txtIC)) { //Add input length validation at resgister button to validate whether this length of ic is valid or no.
            txtAdNat.setText("Citizen");
        }
    }//GEN-LAST:event_txtAdICKeyPressed

    private void txtSNatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSNatKeyPressed
        String txtIC3 = txtSIC.getText();

        if (!Pattern.matches("^[0-9]+$", txtIC3)) {
            txtSNat.setText("Non-Citizen");
        } else if (Pattern.matches("^[0-9]+$", txtIC3)) { //Add input length validation at resgister button to validate whether this length of ic is valid or no.
            txtSNat.setText("Citizen");
        }
    }//GEN-LAST:event_txtSNatKeyPressed

    private void txtDrICKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDrICKeyPressed
        String txtIC2 = txtSIC.getText();

        if (!Pattern.matches("^[0-9]+$", txtIC2)) {
            txtSNat.setText("Non-Citizen");
        } else if (Pattern.matches("^[0-9]+$", txtIC2)) { //Add input length validation at resgister button to validate whether this length of ic is valid or no.
            txtSNat.setText("Citizen");
        }
    }//GEN-LAST:event_txtDrICKeyPressed

    private void btnAdPSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdPSaveActionPerformed
        if (General.AlertQuestionYesNo("Do you want to save your changes?", "Save Confirmation") == 1) {
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

    private void txtAdPNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAdPNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAdPNameActionPerformed

    private void btnAdPInfoEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdPInfoEditActionPerformed
        editProfile(true);
    }//GEN-LAST:event_btnAdPInfoEditActionPerformed

    private void btnAdRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdRegisterActionPerformed
        //Add input to database

        //Prompt Success
    }//GEN-LAST:event_btnAdRegisterActionPerformed

    private void btnDrRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDrRegisterActionPerformed
        //Add input to database

        //Prompt Success
    }//GEN-LAST:event_btnDrRegisterActionPerformed

    private void btnSRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSRegisterActionPerformed
        //Add input to database

        //Prompt Success
    }//GEN-LAST:event_btnSRegisterActionPerformed

    private void btnMaApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaApproveActionPerformed
        General.AlertMsgInfo("Please assign a Vaccination Date, Vaccine and Vaccination Centre for this appointment.", "Info");
        pnlApprovedAppointment.setVisible(true);
    }//GEN-LAST:event_btnMaApproveActionPerformed

    private void btnMaDeclineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaDeclineActionPerformed
        if (General.AlertQuestionYesNo("Decline an appointment will automatically cancel it, do you still want to continue?", "Decline Appointment Confirmation") == 0) {
            String appCode = txtMaAppCode.getText();

            //Retrieve appointment
            FileOperation fo = new FileOperation(appCode, General.appointmentFileName);
            fo.ReadFile();
            Appointment app = (Appointment) fo.getReadResult();

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

    private void btnMaSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaSubmitActionPerformed

        if (calMaVacDate.getCalendar() == null && cboMaVac.getSelectedIndex() == -1 || cboMaVacCentre.getSelectedIndex() == -1) {
            General.AlertMsgError("All fields in Schedule Appointment have to be filled!", "Error");
            return;
        }

        String remarks = txtMaRemarks.getText();
        MyDateTime vacDate = new MyDateTime(calMaVacDate.getCalendar());

        Vaccine[] vacArray = (Vaccine[]) htVac.values().toArray();
        Vaccine vac = vacArray[cboMaVac.getSelectedIndex()];

        VaccineCentre[] vacCentreArray = (VaccineCentre[]) htVacCentre.values().toArray();
        VaccineCentre vacCentre = vacCentreArray[cboMaVacCentre.getSelectedIndex()];

        //Update appointment
        Appointment data = (Appointment) htAppointment.get(txtMaAppCode.getText());
        if (data != null) {
            data.setLocation(vacCentre);
            data.setRemarks(remarks);
            data.setVacc(vac);
            data.setVaccinationDate(vacDate);

            FileOperation fo = new FileOperation(data.getCode(), General.appointmentFileName);
            fo.ModifyRecord(data);

            //Update appointment hashtable
            InitGlobalData();

        }


    }//GEN-LAST:event_btnMaSubmitActionPerformed

    private void btnRASearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRASearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRASearchActionPerformed

    private void btnRaSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRaSubmitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRaSubmitActionPerformed

    private void btnRaApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRaApproveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRaApproveActionPerformed

    private void btnRaDeclineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRaDeclineActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRaDeclineActionPerformed

    private void btnVSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnVSearchActionPerformed

    private void btnPpSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPpSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPpSearchActionPerformed

    private void btnPpUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPpUpdateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPpUpdateActionPerformed

    private void txtSNatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSNatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSNatActionPerformed

    private void txtPpNatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPpNatKeyPressed
        String txtIC4 = txtPpIC.getText();

        if (!Pattern.matches("^[0-9]+$", txtIC4)) {
            txtPpName.setText("Non-Citizen");
        } else if (Pattern.matches("^[0-9]+$", txtIC4)) { //Add input length validation at resgister button to validate whether this length of ic is valid or no.
            txtPpName.setText("Citizen");
        }
    }//GEN-LAST:event_txtPpNatKeyPressed

    private void btnCMSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCMSearchActionPerformed
        // TODO add your handling code here:
        String search = "";
        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblCM.getModel();
        dtm.setRowCount(0);

        if (!txtCMSearch.getText().isBlank()) {
            search = txtCMSearch.getText();
        }

        search = search.trim().toLowerCase();

        String state = "";

        if (cboCMStateSearch.getSelectedIndex() != 0) {
            state = String.valueOf(cboCMStateSearch.getSelectedItem());
        }

        for (Object x : htVacCentre.values()) {
            VaccineCentre a = (VaccineCentre) x;

            if (a.getVacCode().contains(search) || a.getName().contains(search) || a.getVacAddress().getStreet().contains(search) || a.getVacAddress().getNo().contains(search) || a.getVacAddress().getCity().contains(search) || a.getVacAddress().getPostcode().contains(search) || a.getVacAddress().getState().contains(state)) {

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

    }//GEN-LAST:event_btnCMSearchActionPerformed

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
        VaccineCentre newVac = new VaccineCentre(txtAnvName.getText(), newVacCentreAdd);

        if (FileOperation.SerializeObject(General.vaccineCentreFileName, newVac)) {
            General.AlertMsgInfo("New vaccine centre created!", "Success");
            AnvClear();
            InitGlobalData();
            InitTableRecords();
        } else {
            General.AlertMsgError("Vaccine centre was not created. Please try again later!", "Error");
        }

    }//GEN-LAST:event_btnCncAddActionPerformed

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
            InitTableRecords();
        } else {
            General.AlertMsgError("Vaccine was not created. Please try again later!", "Error");
        }
    }//GEN-LAST:event_btnAnvAddActionPerformed

    private void tblRAMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblRAMouseClicked
        DefaultTableModel Tmodel = (DefaultTableModel) tblRA.getModel();

        //Display data into text field when the specific row is selected
        String JTbName;
        String JTbGender;
        String JTbIC;
        String JTbNat;
        String JTbStats;

        JTbName = Tmodel.getValueAt(tblRA.getSelectedRow(), 1).toString();
        JTbGender = Tmodel.getValueAt(tblRA.getSelectedRow(), 2).toString();
        JTbIC = Tmodel.getValueAt(tblRA.getSelectedRow(), 3).toString();
        JTbNat = Tmodel.getValueAt(tblRA.getSelectedRow(), 4).toString();
        JTbStats = Tmodel.getValueAt(tblRA.getSelectedRow(), 5).toString();

        txtRaName.setText(JTbName);
        txtRaGender.setText(JTbGender);
        txtRaIC.setText(JTbIC);
        txtRaNat.setText(JTbNat);
        txtRaStats.setText(JTbStats);

    }//GEN-LAST:event_tblRAMouseClicked

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

        calMaDob.setCalendar(data.RegisterDate.getCal());
        txtMaAddress.setText(data.Ppl.Address.getFullAddress());

        //Popoulate VacCentre combobox
        for (Object x : htVacCentre.values()) {
            VaccineCentre v = (VaccineCentre) x;

            if (v.getVacAddress().getState().equals(data.Ppl.Address.getState())) {
                cboMaVacCentre.addItem(v.getVacCode() + " - " + v.getName());
            }
        }

        if (data.Ppl.getClass().equals(Citizen.class
        )) {
            Citizen c = (Citizen) data.Ppl;
            txtMaIC.setText(c.getIcNo());
            txtMaNat.setText(General.NationalityMalaysian);
        } else {
            NonCitizen c = (NonCitizen) data.Ppl;
            txtMaIC.setText(c.getPassport());
            txtMaNat.setText(General.NationalityMalaysian);
        }

        if (data.getStatus().equals(AppointmentStatus.Pending)) {
            btnMaApprove.setEnabled(true);
            btnMaDecline.setEnabled(true);
            return;
        }

        if (data.getStatus().equals(AppointmentStatus.Approved)) {
            txtMaRemarks.setText(data.getRemarks());
            calMaVacDate.setCalendar(data.VaccinationDate.getCal());
            cboMaVac.setSelectedItem(data.Vacc.getVacCode() + " - " + data.Vacc.getName());
            cboMaVacCentre.setSelectedItem(data.Location.getVacCode() + " - " + data.Location.getName());
            txtMaVacAdd.setText(data.Location.VacAddress.getFullAddress());
            pnlApprovedAppointment.setEnabled(true);
            btnMaDecline.setEnabled(true);
            btnMaSubmit.setText("Save");
            return;
        }

        if (data.getStatus().equals(AppointmentStatus.Cancelled)) {
            btnMaApprove.setEnabled(true);
            return;
        }


    }//GEN-LAST:event_tblMAMouseClicked

    private void tblVMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVMKeyPressed


    }//GEN-LAST:event_tblVMKeyPressed

    private void tblCMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCMKeyPressed
        DefaultTableModel Tmode4 = (DefaultTableModel) tblRA.getModel();

        //Display data into text field when the specific row is selected
        String JTbID3;
        String JTbName3;
        String JTbNo3;
        String JTbStreet3;
        String JTbPostcode3;
        String JTbCity3;
        String JTbStates3;

        JTbID3 = Tmode4.getValueAt(tblCM.getSelectedRow(), 1).toString();
        JTbName3 = Tmode4.getValueAt(tblCM.getSelectedRow(), 2).toString();
        JTbNo3 = Tmode4.getValueAt(tblCM.getSelectedRow(), 3).toString();
        JTbStreet3 = Tmode4.getValueAt(tblCM.getSelectedRow(), 4).toString();
        JTbPostcode3 = Tmode4.getValueAt(tblCM.getSelectedRow(), 5).toString();
        JTbCity3 = Tmode4.getValueAt(tblCM.getSelectedRow(), 6).toString();
        JTbStates3 = Tmode4.getValueAt(tblCM.getSelectedRow(), 7).toString();

    }//GEN-LAST:event_tblCMKeyPressed

    private void tblPpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblPpKeyPressed
        DefaultTableModel Tmode4 = (DefaultTableModel) tblRA.getModel();

        //Display data into text field when the specific row is selected
        String JTbName4;
        String JTbIC4;
        String JTbNat4;
        String JTbStatus4;
        String JTbEmail4;
        String JTbPass4;

        JTbName4 = Tmode4.getValueAt(tblPp.getSelectedRow(), 1).toString();
        JTbIC4 = Tmode4.getValueAt(tblPp.getSelectedRow(), 2).toString();
        JTbNat4 = Tmode4.getValueAt(tblPp.getSelectedRow(), 3).toString();
        JTbStatus4 = Tmode4.getValueAt(tblPp.getSelectedRow(), 4).toString();
        JTbEmail4 = Tmode4.getValueAt(tblPp.getSelectedRow(), 5).toString();
        JTbPass4 = Tmode4.getValueAt(tblPp.getSelectedRow(), 6).toString();

        txtPpName.setText(JTbName4);
        txtPpIC.setText(JTbIC4);
        txtPpNat.setText(JTbNat4);
        cboPpStatus.setSelectedItem(JTbStatus4);
        txtPpEmail.setText(JTbEmail4);
        txtPpNPass.setText(JTbPass4);
    }//GEN-LAST:event_tblPpKeyPressed

    private void txtPNewPwKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNewPwKeyTyped
        // TODO add your handling code here:

    }//GEN-LAST:event_txtPNewPwKeyTyped

    private void txtPNewPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNewPwKeyReleased
        txtPCfmPw.setEnabled(txtPNewPw.getPassword().length > 0);
        txtPCfmPw.setText("");
        lblPwNoMatch.setVisible(txtPCfmPw.isEnabled());
    }//GEN-LAST:event_txtPNewPwKeyReleased

    private void txtPCfmPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPCfmPwKeyReleased
        lblPwNoMatch.setVisible(!String.valueOf(txtPNewPw.getPassword()).equals(String.valueOf(txtPCfmPw.getPassword())));
    }//GEN-LAST:event_txtPCfmPwKeyReleased

    private void btnMASearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMASearchActionPerformed


    }//GEN-LAST:event_btnMASearchActionPerformed

    private void cboMaVacCentreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMaVacCentreActionPerformed
        String appCode = txtMaAppCode.getText();
        FileOperation fo = new FileOperation(appCode, General.appointmentFileName);
        fo.ReadFile();
        Appointment app = (Appointment) fo.getReadResult();
        
        if (cboMaVacCentre.getSelectedIndex() != -1) {
            VaccineCentre[] vacCentreArray = (VaccineCentre[]) htVacCentre.values().toArray();
            VaccineCentre vacCentre = vacCentreArray[cboMaVacCentre.getSelectedIndex()];
            txtMaVacAdd.setText(vacCentre.VacAddress.getFullAddress());
        } else {
            txtMaVacAdd.setText("");
        }
    }//GEN-LAST:event_cboMaVacCentreActionPerformed

    private void txtMaSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMaSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMaSearchActionPerformed

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

            if (a.getCode().toLowerCase().equals(search) || a.Ppl.Username.contains(search) || a.Ppl.getFullName().contains(search)) {

                String IcPassport = "";
                if (a.Ppl.getClass().equals(Citizen.class
                )) {
                    Citizen c = (Citizen) a.Ppl;
                    IcPassport = c.getIcNo() + " (" + General.NationalityMalaysian + ")";
                } else {
                    NonCitizen c = (NonCitizen) a.Ppl;
                    IcPassport = c.getPassport() + " (" + General.NationalityNonMalaysian + ")";
                }

                Object[] dtmObj = new Object[]{
                    a.getCode(),
                    a.Ppl.getFullName() + " (" + a.Ppl.Username + ")",
                    IcPassport,
                    a.RegisterDate.GetShortDateTime(),
                    a.getStatus()
                };

                dtm.addRow(dtmObj);
            }
        }

        tblMA.setModel(dtm);
    }//GEN-LAST:event_btnMASearchMouseClicked

    private void txtAnvDoseKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAnvDoseKeyReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_txtAnvDoseKeyReleased

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

            if (a.getVacCode().contains(search) || a.getName().contains(search)) {

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

    private void txtAnvDoseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAnvDoseKeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtAnvDoseKeyPressed

    private void txtAnvDoseKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAnvDoseKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        }
    }//GEN-LAST:event_txtAnvDoseKeyTyped

    private void txtAnvIntervalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtAnvIntervalKeyTyped
        // TODO add your handling code here:
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
        }
    }//GEN-LAST:event_txtAnvIntervalKeyTyped

    private void btnVmUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVmUpdateActionPerformed
        if (General.AlertQuestionYesNo("Do you want to save vaccine information changes?", "Changes Confirmation") == 1) {
            return;
        }

        DefaultTableModel dtm = (DefaultTableModel) tblVM.getModel();
        int i = 0;

        try {
            while (i < dtm.getRowCount()) {
                int col = 1;
                String code = String.valueOf(dtm.getValueAt(i, col++));
                String name = String.valueOf(dtm.getValueAt(i, col++));
                int dose = Integer.parseInt(String.valueOf(dtm.getValueAt(i, col++)));
                int interval = Integer.parseInt(String.valueOf(dtm.getValueAt(i, col++)));

                Vaccine vaccine = new Vaccine(code, name, dose, interval);

                htVac.replace(code, vaccine);
                i++;
            }
        } catch (Exception ex) {
            General.AlertMsgError("Dose and Interval fields have to be integer.", "Error");
        }

        FileOperation fo = new FileOperation(General.vaccineFileName);
        fo.setHt(htVac);

        if (fo.ModifyRecords()) {
            General.AlertMsgInfo("Vaccine information are successfully updated!", "Success");
            InitGlobalData();
            InitTableRecords();
        } else {
            General.AlertMsgError("Failed to update vaccine information. Please try again later!", "Error");
        }

    }//GEN-LAST:event_btnVmUpdateActionPerformed

    private void tblVMKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblVMKeyTyped

    }//GEN-LAST:event_tblVMKeyTyped

    private void btnCncUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCncUpdateActionPerformed
        if (General.AlertQuestionYesNo("Do you want to update vaccine centre information?", "Changes Confirmation") == 1) {
            return;
        }

        DefaultTableModel dtm = (DefaultTableModel) tblCM.getModel();
        int i = 0;

        try {
            while (i < dtm.getRowCount()) {
                int col = 1;
                String code = String.valueOf(dtm.getValueAt(i, col++));
                String name = String.valueOf(dtm.getValueAt(i, col++));
                String addNo = String.valueOf(dtm.getValueAt(i, col++));
                String addStreet = String.valueOf(dtm.getValueAt(i, col++));
                String addPost = String.valueOf(dtm.getValueAt(i, col++));
                String addCity = String.valueOf(dtm.getValueAt(i, col++));
                String addState = String.valueOf(dtm.getValueAt(i, col++));;

                Address add = new Address(addNo, addStreet, addPost, addCity, addState);
                VaccineCentre vc = new VaccineCentre(code, name, add);

                htVacCentre.replace(code, vc);
                i++;
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        FileOperation fo = new FileOperation(General.vaccineCentreFileName);
        fo.setHt(htVacCentre);

        if (fo.ModifyRecords()) {
            General.AlertMsgInfo("Vaccine centre information are successfully updated!", "Success");
            InitGlobalData();
            InitTableRecords();
        } else {
            General.AlertMsgError("Failed to update vaccine centre information. Please try again later!", "Error");
        }
    }//GEN-LAST:event_btnCncUpdateActionPerformed

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
    private javax.swing.JButton btnAdRegister;
    private javax.swing.JButton btnAnvAdd;
    private javax.swing.JButton btnCMSearch;
    private javax.swing.JButton btnCncAdd;
    private javax.swing.JButton btnCncUpdate;
    private javax.swing.JButton btnDrRegister;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMASearch;
    private javax.swing.JButton btnMaApprove;
    private javax.swing.JButton btnMaDecline;
    private javax.swing.JButton btnMaSubmit;
    private javax.swing.JButton btnPpSearch;
    private javax.swing.JButton btnPpUpdate;
    private javax.swing.JButton btnRASearch;
    private javax.swing.JButton btnRaApprove;
    private javax.swing.JButton btnRaDecline;
    private javax.swing.JButton btnRaSubmit;
    private javax.swing.JButton btnSRegister;
    private javax.swing.JButton btnVMSearch;
    private javax.swing.JButton btnVSearch;
    private javax.swing.JButton btnVmUpdate;
    private com.toedter.calendar.JDateChooser calAdPHiredDate;
    private com.toedter.calendar.JDateChooser calMaDob;
    private com.toedter.calendar.JDateChooser calMaVacDate;
    private com.toedter.calendar.JDateChooser calVSearchVacDate;
    private javax.swing.JComboBox<String> cboAdGender;
    private javax.swing.JComboBox<String> cboAdPGender;
    private javax.swing.JComboBox<String> cboCMStateSearch;
    private javax.swing.JComboBox<String> cboCncState;
    private javax.swing.JComboBox<String> cboDrGender;
    private javax.swing.JComboBox<String> cboMaVac;
    private javax.swing.JComboBox<String> cboMaVacCentre;
    private javax.swing.JComboBox<String> cboPpStatus;
    private javax.swing.JComboBox<String> cboSGender;
    private javax.swing.JComboBox<String> cboVSearchNat;
    private javax.swing.JComboBox<String> cboVSearchStatus;
    private javax.swing.JComboBox<String> cboVSearchVac;
    private javax.swing.JComboBox<String> cboVSearchVacCentre;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private com.toedter.calendar.JDateChooser jDob;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel117;
    private javax.swing.JLabel jLabel118;
    private javax.swing.JLabel jLabel119;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel120;
    private javax.swing.JLabel jLabel121;
    private javax.swing.JLabel jLabel122;
    private javax.swing.JLabel jLabel123;
    private javax.swing.JLabel jLabel124;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTabbedPane jTabbedPane6;
    private javax.swing.JLabel lblPwNoMatch;
    private javax.swing.JLabel lblVSName1;
    private javax.swing.JPanel pnlApprovedAppointment;
    private javax.swing.JPanel pnlCredential;
    private javax.swing.JTable tblCM;
    private javax.swing.JTable tblMA;
    private javax.swing.JTable tblPp;
    private javax.swing.JTable tblRA;
    private javax.swing.JTable tblV;
    private javax.swing.JTable tblVM;
    private javax.swing.JTextField txtAdCPass;
    private javax.swing.JTextField txtAdDob;
    private javax.swing.JTextField txtAdEmail;
    private javax.swing.JTextField txtAdIC;
    private javax.swing.JTextField txtAdName;
    private javax.swing.JTextField txtAdNat;
    private javax.swing.JTextField txtAdNo;
    private javax.swing.JTextField txtAdPName;
    private javax.swing.JTextField txtAdPNo;
    private javax.swing.JTextField txtAdPRole;
    private javax.swing.JTextField txtAdPUser;
    private javax.swing.JTextField txtAdPass;
    private javax.swing.JTextField txtAnvDose;
    private javax.swing.JTextField txtAnvInterval;
    private javax.swing.JTextField txtAnvName;
    private javax.swing.JTextField txtCMSearch;
    private javax.swing.JTextField txtCncCity;
    private javax.swing.JTextField txtCncName;
    private javax.swing.JTextField txtCncNo;
    private javax.swing.JTextField txtCncPost;
    private javax.swing.JTextField txtCncStreet;
    private javax.swing.JTextField txtDcName;
    private javax.swing.JTextField txtDrCPass;
    private javax.swing.JTextField txtDrDob;
    private javax.swing.JTextField txtDrEmail;
    private javax.swing.JTextField txtDrIC;
    private javax.swing.JTextField txtDrNat;
    private javax.swing.JTextField txtDrNo;
    private javax.swing.JTextField txtDrPass;
    private javax.swing.JTextField txtDrVac;
    private javax.swing.JTextField txtMaAddress;
    private javax.swing.JTextField txtMaAppCode;
    private javax.swing.JTextField txtMaFullname;
    private javax.swing.JTextField txtMaGender;
    private javax.swing.JTextField txtMaIC;
    private javax.swing.JTextField txtMaNat;
    private javax.swing.JTextField txtMaRemarks;
    private javax.swing.JTextField txtMaSearch;
    private javax.swing.JTextField txtMaSearch1;
    private javax.swing.JTextField txtMaUsername;
    private javax.swing.JTextField txtMaVacAdd;
    private javax.swing.JPasswordField txtPCfmPw;
    private javax.swing.JTextField txtPEmail;
    private javax.swing.JPasswordField txtPNewPw;
    private javax.swing.JTextField txtPpEmail;
    private javax.swing.JTextField txtPpIC;
    private javax.swing.JTextField txtPpNPass;
    private javax.swing.JTextField txtPpName;
    private javax.swing.JTextField txtPpNat;
    private javax.swing.JTextField txtRaGender;
    private javax.swing.JTextField txtRaIC;
    private javax.swing.JTextField txtRaName;
    private javax.swing.JTextField txtRaNat;
    private javax.swing.JTextField txtRaRemarks;
    private javax.swing.JTextField txtRaSearch;
    private javax.swing.JTextField txtRaStats;
    private javax.swing.JTextField txtRaVC;
    private javax.swing.JTextField txtRaVN;
    private javax.swing.JTextField txtSCPass;
    private javax.swing.JTextField txtSDob;
    private javax.swing.JTextField txtSEmail;
    private javax.swing.JTextField txtSIC;
    private javax.swing.JTextField txtSName1;
    private javax.swing.JTextField txtSNat;
    private javax.swing.JTextField txtSNo;
    private javax.swing.JTextField txtSPass;
    private javax.swing.JTextField txtVMSearch;
    private javax.swing.JTextField txtVSearch;
    // End of variables declaration//GEN-END:variables
}
