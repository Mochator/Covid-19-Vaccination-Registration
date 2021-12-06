/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import java.io.File;
import java.io.IOException;
import com.toedter.calendar.JDateChooser;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author ljk99
 */
public class LoadingPage extends javax.swing.JFrame {

    private static People currentUser;
    private static Hashtable<String, Object> htAppointment = new Hashtable<String, Object>();

    /**
     * Creates new form LoadingPage
     */
    public LoadingPage() {
        initComponents();
        ComponentReset();

        //---Profile Tab---
        //Init state combo
        General.MalaysiaStates().forEach(d -> cmbPState.addItem(d));

        //---Vaccination Appointment---
        //Init Vaccine Types
        ArrayList<Object> vaccineTypes = FileOperation.DeserializeObject(General.vaccineFileName);
        for (Object x : vaccineTypes) {
            Vaccine v = (Vaccine) x;
            cmbVAVaccine.addItem(v.getVacCode() + " - " + v.getName());
        }

        //Init Vaccine Centre
        ArrayList<Object> vaccineCentres = FileOperation.DeserializeObject(General.vaccineCentreFileName);
        for (Object x : vaccineCentres) {
            VaccineCentre v = (VaccineCentre) x;
            cmbVACentre.addItem(v.getVacCode() + " - " + v.getName());
        }

    }

    private void ComponentReset() {
        pnlCredential.setVisible(false);
        lblPwNoMatch.setVisible(false);
        btnSave.setVisible(false);

        pnlRejectReason.setVisible(false);
        pnlVA.setVisible(false);
    }

    private void editProfile(boolean b) {
        btnSave.setVisible(b);
        btnEditProfile.setVisible(!b);
        pnlCredential.setVisible(b);
        for (Component x : jPanel9.getComponents()) {

            if (x instanceof JTextField) {
                JTextField j = (JTextField) x;
                j.setEnabled(j.isEditable() && b);
            } else if (x instanceof JComboBox) {
                JComboBox j = (JComboBox) x;
                j.setEnabled(b);

            } else if (x instanceof JPasswordField) {
                JPasswordField j = (JPasswordField) x;
                j.setEnabled(b);
                j.setText("");
            }
        }
    }

    private void ClearVAFields() {
        txtVACode.setText("");
        dateVADate.setCalendar(null);
        cmbVACentre.setSelectedIndex(-1);
        cmbVAVaccine.setSelectedIndex(-1);
        txtVAAddress.setText("");
        txtVAReason.setText("");

        ComponentReset();
    }

    public void PopulateUserData() {

        //---Profile Tab---
        txtPFullName.setText(currentUser.getFullName());

        txtPGender.setText(currentUser.getGender() == General.GenderMale ? General.GenderMaleString : General.GenderFemaleString);

        jDob.setCalendar(currentUser.Dob.getCal());

        if (currentUser.getIsCitizen()) {
            Citizen c = (Citizen) currentUser;
            txtPIC.setText(c.getIcNo());
            txtPNationality.setText(General.NationalityCitizen);

        } else {
            NonCitizen c = (NonCitizen) currentUser;
            txtPIC.setText(c.getPassport());
            txtPNationality.setText(General.NationalityNonCitizen);

        }

        txtPPhone.setText(currentUser.getContact());

        txtPAddNo.setText(currentUser.Address.getNo());
        txtPAddStreet.setText(currentUser.Address.getStreet());
        txtPAddCity.setText(currentUser.Address.getCity());
        txtPAddPostcode.setText(currentUser.Address.getPostcode());
        cmbPState.setSelectedItem(currentUser.Address.getState());
        txtPEmail.setText(currentUser.getEmail());
        txtPUsername.setText(currentUser.Username);

        lblVaccinatedStatus.setText(currentUser.getVacStatus().name());

        txtPNewPw.setText("");
        txtPCfmPw.setText("");

        //---Vaccination Appointment Tab---
        //Enroll disable
        ArrayList<Object> allUserAppointments = FileOperation.DeserializeObject(General.appointmentFileName);
        Hashtable<String, Object> ht = FileOperation.ConvertToHashTable(allUserAppointments);

        //Appointment List
        DefaultTableModel dtm = (DefaultTableModel) tblAppointment.getModel();

        dtm.setRowCount(0);
        int activeApp = 0;

        for (Object x : ht.keySet()) {
            Appointment a = (Appointment) ht.get(x);
            if (a.Ppl.Username.equals(currentUser.Username)) {
                htAppointment.put(String.valueOf(x), a);

                //Get vaccine data
                String vaVaccine = "-";
                if (a.Vacc != null) {
                    FileOperation foVac = new FileOperation(a.Vacc.getVacCode(), General.vaccineFileName);
                    foVac.ReadFile();
                    Vaccine vac = (Vaccine) foVac.getReadResult();
                    vaVaccine = vac.getVacCode() + " - " + vac.getName();
                }

                String vaDate = a.VaccinationDate == null ? null : a.VaccinationDate.GetShortDate();

                String vaCentre = "-";
                if (a.Location != null) {
                    FileOperation foCentre = new FileOperation(a.Location.getVacCode(), General.vaccineCentreFileName);
                    foCentre.ReadFile();
                    VaccineCentre vacCentre = (VaccineCentre) foCentre.getReadResult();
                    vaCentre = vacCentre.getVacCode() + " - " + vacCentre.getName();
                }

                Object[] dtmObj = new Object[]{a.getCode(), a.getRegisterDate().GetShortDateTime(), vaDate, vaCentre, vaVaccine, a.getStatus()};

                dtm.addRow(dtmObj);

                if (!(a.getStatus().equals(AppointmentStatus.Cancelled))) {
                    activeApp++;
                }
            }
        }

        if (activeApp > 0) {
            btnSubmit.setEnabled(false);
            btnSubmit.setText("You have enrolled!");
        }

        btnCert.setVisible(currentUser.getVacStatus().equals(VaccinationStatus.Fully));

    }

    public void setCurrentUserCitizen(Citizen user) {
        currentUser = user;
    }

    public void setCurrentUserNonCitizen(NonCitizen user) {
        currentUser = user;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        lblVSName1 = new javax.swing.JLabel();
        lblVSSymtom1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblVSName2 = new javax.swing.JLabel();
        lblVaccinatedStatus = new javax.swing.JLabel();
        btnCert = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel9 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtPFullName = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtPIC = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        txtPPhone = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        txtPAddNo = new javax.swing.JTextField();
        txtPAddStreet = new javax.swing.JTextField();
        txtPAddCity = new javax.swing.JTextField();
        cmbPState = new javax.swing.JComboBox<>();
        pnlCredential = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        txtPEmail = new javax.swing.JTextField();
        txtPNewPw = new javax.swing.JPasswordField();
        txtPCfmPw = new javax.swing.JPasswordField();
        jLabel42 = new javax.swing.JLabel();
        lblPwNoMatch = new javax.swing.JLabel();
        btnEditProfile = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        txtPAddPostcode = new javax.swing.JTextField();
        jDob = new com.toedter.calendar.JDateChooser();
        txtPGender = new javax.swing.JTextField();
        txtPNationality = new javax.swing.JTextField();
        txtPUsername = new javax.swing.JTextField();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        btnSubmit = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblAppointment = new javax.swing.JTable();
        pnlVA = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        txtVACode = new javax.swing.JTextField();
        btnVAReject = new javax.swing.JButton();
        btnVAAccept = new javax.swing.JButton();
        dateVADate = new com.toedter.calendar.JDateChooser();
        cmbVACentre = new javax.swing.JComboBox<>();
        cmbVAVaccine = new javax.swing.JComboBox<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtVAAddress = new javax.swing.JTextArea();
        pnlRejectReason = new javax.swing.JPanel();
        jLabel37 = new javax.swing.JLabel();
        txtVAReason = new javax.swing.JTextField();
        btnVAReject1 = new javax.swing.JButton();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 51));
        jPanel1.setForeground(new java.awt.Color(0, 0, 0));

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

        jPanel13.setBackground(new java.awt.Color(255, 204, 51));

        lblVSName1.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 20)); // NOI18N
        lblVSName1.setForeground(new java.awt.Color(0, 0, 0));
        lblVSName1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVSName1.setText("COVID-19");

        lblVSSymtom1.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 25)); // NOI18N
        lblVSSymtom1.setForeground(new java.awt.Color(0, 0, 0));
        lblVSSymtom1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVSSymtom1.setText("Vaccinated");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/vaccine.png"))); // NOI18N

        lblVSName2.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 20)); // NOI18N
        lblVSName2.setForeground(new java.awt.Color(0, 0, 0));
        lblVSName2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVSName2.setText("Vaccination Status");

        lblVaccinatedStatus.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 25)); // NOI18N
        lblVaccinatedStatus.setForeground(new java.awt.Color(0, 0, 0));
        lblVaccinatedStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVaccinatedStatus.setText("Not");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lblVSName1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblVSName2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblVSSymtom1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblVaccinatedStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel13Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVSName1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVSName2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(lblVaccinatedStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblVSSymtom1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        btnCert.setBackground(new java.awt.Color(51, 51, 51));
        btnCert.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        btnCert.setForeground(new java.awt.Color(255, 255, 255));
        btnCert.setText("Vaccination Certificate");
        btnCert.setBorder(null);
        btnCert.setOpaque(true);
        btnCert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCert, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCert, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.setBackground(new java.awt.Color(0, 0, 0));
        jTabbedPane1.setForeground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 12)); // NOI18N
        jTabbedPane1.setOpaque(true);

        jPanel9.setBackground(new java.awt.Color(51, 51, 51));
        jPanel9.setForeground(new java.awt.Color(255, 255, 255));
        jPanel9.setAutoscrolls(true);

        jLabel16.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Basic Information");

        jLabel17.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Full Name:");

        txtPFullName.setEditable(false);
        txtPFullName.setBackground(new java.awt.Color(204, 204, 204));
        txtPFullName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPFullName.setForeground(new java.awt.Color(0, 0, 0));
        txtPFullName.setEnabled(false);
        txtPFullName.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPFullName.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel19.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel19.setText("IC/Passport:");

        txtPIC.setEditable(false);
        txtPIC.setBackground(new java.awt.Color(204, 204, 204));
        txtPIC.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPIC.setForeground(new java.awt.Color(0, 0, 0));
        txtPIC.setEnabled(false);
        txtPIC.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPIC.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtPIC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPICKeyPressed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Nationality:");

        jLabel21.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel21.setText("Contact No:");

        txtPPhone.setBackground(new java.awt.Color(204, 204, 204));
        txtPPhone.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPPhone.setForeground(new java.awt.Color(0, 0, 0));
        txtPPhone.setEnabled(false);
        txtPPhone.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPPhone.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel29.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("Date of Birth:");

        jLabel39.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel39.setText("Address:");

        jLabel40.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("Username:");

        jLabel41.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel41.setText("Gender");

        txtPAddNo.setBackground(new java.awt.Color(204, 204, 204));
        txtPAddNo.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPAddNo.setForeground(new java.awt.Color(0, 0, 0));
        txtPAddNo.setToolTipText("No.");
        txtPAddNo.setEnabled(false);
        txtPAddNo.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPAddNo.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPAddStreet.setBackground(new java.awt.Color(204, 204, 204));
        txtPAddStreet.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPAddStreet.setForeground(new java.awt.Color(0, 0, 0));
        txtPAddStreet.setToolTipText("Street");
        txtPAddStreet.setEnabled(false);
        txtPAddStreet.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPAddStreet.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPAddCity.setBackground(new java.awt.Color(204, 204, 204));
        txtPAddCity.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPAddCity.setForeground(new java.awt.Color(0, 0, 0));
        txtPAddCity.setToolTipText("Postcode");
        txtPAddCity.setEnabled(false);
        txtPAddCity.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPAddCity.setSelectionColor(new java.awt.Color(255, 255, 51));

        cmbPState.setEnabled(false);

        pnlCredential.setBackground(new java.awt.Color(57, 57, 57));
        pnlCredential.setMinimumSize(new java.awt.Dimension(0, 0));

        jLabel38.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel38.setText("Email:");

        jLabel43.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 255, 255));
        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel43.setText("New Password:");

        jLabel46.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel46.setForeground(new java.awt.Color(255, 255, 255));
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel46.setText("Confirm Password:");

        txtPEmail.setBackground(new java.awt.Color(204, 204, 204));
        txtPEmail.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPEmail.setForeground(new java.awt.Color(0, 0, 0));
        txtPEmail.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPEmail.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPNewPw.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPNewPw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPNewPwKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPNewPwKeyTyped(evt);
            }
        });

        txtPCfmPw.setEnabled(false);
        txtPCfmPw.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPCfmPw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPCfmPwKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPCfmPwKeyTyped(evt);
            }
        });

        jLabel42.setFont(new java.awt.Font("Bell MT", 0, 10)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(204, 204, 204));
        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel42.setText("Leave this field blank to retain current password.");

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
                            .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPwNoMatch, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtPCfmPw, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                        .addComponent(txtPNewPw, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txtPEmail, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel42, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60))
        );
        pnlCredentialLayout.setVerticalGroup(
            pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCredentialLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(txtPEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPNewPw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlCredentialLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPCfmPw, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPwNoMatch)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        btnEditProfile.setText("Edit Profile");
        btnEditProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditProfileActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        txtPAddPostcode.setBackground(new java.awt.Color(204, 204, 204));
        txtPAddPostcode.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPAddPostcode.setForeground(new java.awt.Color(0, 0, 0));
        txtPAddPostcode.setToolTipText("Postcode");
        txtPAddPostcode.setEnabled(false);
        txtPAddPostcode.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPAddPostcode.setSelectionColor(new java.awt.Color(255, 255, 51));

        jDob.setBackground(new java.awt.Color(255, 255, 255));
        jDob.setForeground(new java.awt.Color(0, 0, 0));
        jDob.setEnabled(false);

        txtPGender.setEditable(false);
        txtPGender.setBackground(new java.awt.Color(204, 204, 204));
        txtPGender.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPGender.setForeground(new java.awt.Color(0, 0, 0));
        txtPGender.setEnabled(false);
        txtPGender.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPGender.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtPNationality.setEditable(false);
        txtPNationality.setBackground(new java.awt.Color(204, 204, 204));
        txtPNationality.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPNationality.setForeground(new java.awt.Color(0, 0, 0));
        txtPNationality.setEnabled(false);
        txtPNationality.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPNationality.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtPNationality.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPNationalityKeyPressed(evt);
            }
        });

        txtPUsername.setEditable(false);
        txtPUsername.setBackground(new java.awt.Color(204, 204, 204));
        txtPUsername.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtPUsername.setForeground(new java.awt.Color(0, 0, 0));
        txtPUsername.setEnabled(false);
        txtPUsername.setSelectedTextColor(new java.awt.Color(0, 0, 0));
        txtPUsername.setSelectionColor(new java.awt.Color(255, 255, 51));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEditProfile))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPFullName, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(txtPGender, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(txtPUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 145, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(4, 4, 4)))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPIC)
                            .addComponent(txtPPhone, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(txtPAddStreet)
                            .addComponent(txtPAddNo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(txtPAddCity, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtPAddPostcode))
                            .addComponent(cmbPState, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jDob, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtPNationality))))
                .addGap(63, 63, 63))
            .addComponent(pnlCredential, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnEditProfile)
                        .addComponent(btnSave)))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(txtPUsername))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtPFullName))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(txtPGender))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel29)
                    .addComponent(jDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel19)
                    .addComponent(txtPIC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtPNationality, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtPPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39)
                    .addComponent(txtPAddNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPAddStreet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPAddCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPAddPostcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbPState, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlCredential, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(812, 812, 812))
        );

        jScrollPane1.setViewportView(jPanel9);

        jTabbedPane1.addTab("Profile", jScrollPane1);

        jTabbedPane3.setBackground(new java.awt.Color(51, 51, 51));
        jTabbedPane3.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        jTabbedPane3.setOpaque(true);

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));
        jPanel4.setPreferredSize(new java.awt.Dimension(650, 1000));

        jLabel9.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Vaccination Programme Registration");

        btnSubmit.setBackground(new java.awt.Color(102, 255, 102));
        btnSubmit.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnSubmit.setForeground(new java.awt.Color(0, 0, 0));
        btnSubmit.setText("Enroll me!");
        btnSubmit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnSubmit.setOpaque(true);
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        jLabel44.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 255, 255));
        jLabel44.setText("<html><body>\nBy enrolling the vaccination programme, I hereby confirm that all my given profile information are accurate. I hereby acknowledge the effect of vaccination and agree to take full responsibility.\n</body>\n</html>");
        jLabel44.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1139, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Enroll", jPanel4);

        jPanel6.setBackground(new java.awt.Color(51, 51, 51));
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));

        tblAppointment.setBackground(new java.awt.Color(255, 255, 255));
        tblAppointment.setForeground(new java.awt.Color(0, 0, 0));
        tblAppointment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Code", "Enroll Date", "Vaccination Date", "Vaccine", "Vaccine Centre", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAppointment.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblAppointmentMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblAppointment);

        pnlVA.setBackground(new java.awt.Color(51, 51, 51));

        jLabel31.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel31.setText("Appointment Code");

        jLabel32.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel32.setText("Vaccination Date");

        jLabel33.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel33.setText("Vaccine Centre");

        jLabel35.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel35.setText("Vaccine");

        txtVACode.setEditable(false);
        txtVACode.setBackground(new java.awt.Color(204, 204, 204));
        txtVACode.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtVACode.setForeground(new java.awt.Color(0, 0, 0));
        txtVACode.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtVACode.setEnabled(false);
        txtVACode.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnVAReject.setText("Decline");
        btnVAReject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVARejectActionPerformed(evt);
            }
        });

        btnVAAccept.setText("Accept");
        btnVAAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVAAcceptActionPerformed(evt);
            }
        });

        dateVADate.setEnabled(false);

        cmbVACentre.setEnabled(false);

        cmbVAVaccine.setEnabled(false);

        txtVAAddress.setColumns(20);
        txtVAAddress.setRows(5);
        txtVAAddress.setEnabled(false);
        jScrollPane3.setViewportView(txtVAAddress);

        javax.swing.GroupLayout pnlVALayout = new javax.swing.GroupLayout(pnlVA);
        pnlVA.setLayout(pnlVALayout);
        pnlVALayout.setHorizontalGroup(
            pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVALayout.createSequentialGroup()
                .addGroup(pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlVALayout.createSequentialGroup()
                        .addGroup(pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel32, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(pnlVALayout.createSequentialGroup()
                                    .addGap(39, 39, 39)
                                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jLabel31, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlVALayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(27, 27, 27)
                        .addGroup(pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3)
                            .addComponent(txtVACode, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(dateVADate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbVACentre, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbVAVaccine, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnlVALayout.createSequentialGroup()
                        .addGap(101, 101, 101)
                        .addComponent(btnVAAccept, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnVAReject, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(146, Short.MAX_VALUE))
        );
        pnlVALayout.setVerticalGroup(
            pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlVALayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlVALayout.createSequentialGroup()
                        .addGroup(pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtVACode, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel32))
                    .addComponent(dateVADate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(cmbVACentre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(cmbVAVaccine, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(pnlVALayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVAAccept)
                    .addComponent(btnVAReject))
                .addGap(17, 17, 17))
        );

        jLabel37.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(0, 0, 0));
        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel37.setText("Reason");

        txtVAReason.setBackground(new java.awt.Color(204, 204, 204));
        txtVAReason.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtVAReason.setForeground(new java.awt.Color(0, 0, 0));
        txtVAReason.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtVAReason.setEnabled(false);
        txtVAReason.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnVAReject1.setText("Submit");
        btnVAReject1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVAReject1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlRejectReasonLayout = new javax.swing.GroupLayout(pnlRejectReason);
        pnlRejectReason.setLayout(pnlRejectReasonLayout);
        pnlRejectReasonLayout.setHorizontalGroup(
            pnlRejectReasonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlRejectReasonLayout.createSequentialGroup()
                .addGroup(pnlRejectReasonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlRejectReasonLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnVAReject1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlRejectReasonLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(txtVAReason, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)))
                .addGap(97, 97, 97))
        );
        pnlRejectReasonLayout.setVerticalGroup(
            pnlRejectReasonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlRejectReasonLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlRejectReasonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVAReason, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addGap(18, 18, 18)
                .addComponent(btnVAReject1)
                .addContainerGap(55, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlVA, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(pnlRejectReason, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 249, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlRejectReason, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(668, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("View Appointment", jPanel6);

        jScrollPane4.setViewportView(jTabbedPane3);

        jTabbedPane1.addTab("Vaccination", jScrollPane4);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        this.dispose();
        Login login = new Login();
        login.setVisible(true);
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnEditProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditProfileActionPerformed
        // TODO add your handling code here:
        editProfile(true);
    }//GEN-LAST:event_btnEditProfileActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed

        if (General.AlertQuestionYesNo("Do you want to save your changes?", "Save Confirmation") == 1) {
            return;
        }

        //Check field filled
        if (txtPPhone.getText().isBlank() || txtPEmail.getText().isBlank() || txtPAddNo.getText().isBlank() || txtPAddStreet.getText().isBlank() || txtPAddCity.getText().isBlank() || txtPAddPostcode.getText().isBlank()) {
            General.AlertMsgError("All details have to be filled.", "Profile Update Failed!");
            return;
        }

        if (!txtPEmail.getText().matches("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$")) {
            General.AlertMsgError("Email format invalid.", "Profile Create Failed!");
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

        String phone = txtPPhone.getText().trim();
        String addNo = txtPAddNo.getText();
        String addStreet = txtPAddStreet.getText();
        String addCity = txtPAddCity.getText();
        String addPostcode = txtPAddPostcode.getText();
        String addState = String.valueOf(cmbPState.getSelectedItem());
        String email = txtPEmail.getText();

        currentUser.setContact(phone);
        currentUser.setEmail(email);

        currentUser.Address.setNo(addNo);
        currentUser.Address.setStreet(addStreet);
        currentUser.Address.setCity(addCity);
        currentUser.Address.setState(addState);
        currentUser.Address.setPostcode(addPostcode);

        FileOperation fo = new FileOperation(currentUser.Username, General.userFileName);
        fo.ReadFile();

        if (fo.ModifyRecord(currentUser)) {
            General.AlertMsgInfo("Profile has been updated.", "Success!");
            editProfile(false);
            PopulateUserData();
        } else {
            General.AlertMsgError("Profile changes were not saved, please try again later.", "Error!");
        }

    }//GEN-LAST:event_btnSaveActionPerformed

    private void txtPNewPwKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNewPwKeyTyped
        // TODO add your handling code here:

    }//GEN-LAST:event_txtPNewPwKeyTyped

    private void txtPICKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPICKeyPressed
        // TODO add your handling code here:

        if (txtPIC.getText().matches("^([\\d]|\\-{2})+$")) {
            txtPNationality.setText(General.NationalityCitizen);
        } else {
            txtPNationality.setText(General.NationalityNonCitizen);
        }

    }//GEN-LAST:event_txtPICKeyPressed

    private void txtPNationalityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNationalityKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPNationalityKeyPressed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        if (General.AlertQuestionYesNo("Do you want to enroll into the vaccination programme?", "Enrollment Confirmation") == 0) {
            // Create appointment

            Appointment app = new Appointment(currentUser);

            if (FileOperation.SerializeObject(General.appointmentFileName, app)) {
                General.AlertMsgInfo("Vaccination programme enrollment submitted.", "Success!");
                PopulateUserData();
            } else {
                General.AlertMsgInfo("Failed to enroll vaccination programme. Please try again later.", "Error!");
            }

        }
    }//GEN-LAST:event_btnSubmitActionPerformed

    private void btnVAAcceptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVAAcceptActionPerformed
        if (General.AlertQuestionYesNo("Do you want to accept the appointment?", "Appointment Confirmation") == 0) {
            String appCode = txtVACode.getText();
            FileOperation fo = new FileOperation(appCode, General.appointmentFileName);
            fo.ReadFile();
            Appointment app = (Appointment) fo.getReadResult();
            app.setStatus(AppointmentStatus.Accepted);

            Stock s = new Stock(app.Vacc, app.CheckDoseFromAppointment(), app.Location);
            if (s.FindStock()) {

                if (s.AddPendingQty(1, currentUser, "Accept Vaccination - " + app.getCode())) {
                    General.AlertMsgError("Something went wrong, please try again later!", "Error");
                    return;
                }

            } else {
                s.GenerateId();
                s.AddPendingQty(1, currentUser, "Accept Vaccination - " + app.getCode());
                FileOperation.SerializeObject(General.stockFileName, s);

            }

            if (fo.ModifyRecord(app)) {
                General.AlertMsgInfo("Appointment succesfully updated!", "Success");
                btnVAAccept.setEnabled(false);
                btnVAReject.setEnabled(false);
                pnlRejectReason.setVisible(false);
                PopulateUserData();
            } else {
                General.AlertMsgInfo("Appointment failed to update.", "Error!");

            }
        }
    }//GEN-LAST:event_btnVAAcceptActionPerformed

    private void tblAppointmentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblAppointmentMouseClicked
        pnlRejectReason.setVisible(false);
        txtVAReason.setText("");
        btnVAAccept.setEnabled(false);
        btnVAReject.setEnabled(false);
        txtVAReason.setEnabled(false);

        int row = tblAppointment.getSelectedRow();
        String appCode = String.valueOf(tblAppointment.getValueAt(row, 0));

        FileOperation fo = new FileOperation(appCode, General.appointmentFileName);
        fo.ReadFile();
        Appointment app = (Appointment) fo.getReadResult();

        ClearVAFields();

        txtVACode.setText(app.getCode());

        if (app.VaccinationDate != null) {
            dateVADate.setCalendar(app.VaccinationDate.getCal());
        }
        if (app.Location != null) {
            cmbVACentre.setSelectedItem(app.Location.getVacCode() + " - " + app.Location.getName());
            String vaAddress = app.Location.VacAddress != null ? app.Location.VacAddress.getFullAddress() : "-";
            txtVAAddress.setText(vaAddress);
        }

        if (app.Vacc != null) {
            cmbVAVaccine.setSelectedItem(app.Vacc.getVacCode() + " - " + app.Vacc.getName());
        }

        if (app.getStatus().equals(AppointmentStatus.Approved)) {
            pnlVA.setVisible(true);
            btnVAAccept.setEnabled(true);
            btnVAReject.setEnabled(true);
            return;
        }

        if (app.getStatus().equals(AppointmentStatus.Accepted)) {
            pnlVA.setVisible(true);
            return;
        }

        if (app.getStatus().equals(AppointmentStatus.Declined)) {
            pnlVA.setVisible(true);
            pnlRejectReason.setVisible(true);
            txtVAReason.setText(app.getRejectReason());
            return;
        }
    }//GEN-LAST:event_tblAppointmentMouseClicked

    private void btnVARejectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVARejectActionPerformed
        // TODO add your handling code here:
        General.AlertMsgInfo("Appointment is only considered officially declined after reject reason given. Please take note that you will be requeued automatically after declined.", "");
        pnlRejectReason.setVisible(true);
        txtVAReason.setEnabled(true);

    }//GEN-LAST:event_btnVARejectActionPerformed

    private void txtPCfmPwKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPCfmPwKeyTyped

    }//GEN-LAST:event_txtPCfmPwKeyTyped

    private void txtPNewPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNewPwKeyReleased
        txtPCfmPw.setEnabled(txtPNewPw.getPassword().length > 0);
        txtPCfmPw.setText("");
        lblPwNoMatch.setVisible(txtPCfmPw.isEnabled());
    }//GEN-LAST:event_txtPNewPwKeyReleased

    private void txtPCfmPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPCfmPwKeyReleased
        lblPwNoMatch.setVisible(!String.valueOf(txtPNewPw.getPassword()).equals(String.valueOf(txtPCfmPw.getPassword())));
    }//GEN-LAST:event_txtPCfmPwKeyReleased

    private void btnVAReject1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVAReject1ActionPerformed

        if (txtVAReason.getText().isBlank()) {
            General.AlertMsgError("Decline reason can't be blank.", "Error");
            return;
        }

        if (General.AlertQuestionYesNo("Your appointment will be recreated and rescheduled according to the given reject reason. Do you want to proceed?", "Reject Confirmation") == 0) {

            String appCode = txtVACode.getText();
            FileOperation fo = new FileOperation(appCode, General.appointmentFileName);
            fo.ReadFile();
            Appointment app = (Appointment) fo.getReadResult();
            if (app != null) {
                app.setRejectReason(txtVAReason.getText());
                app.setStatus(AppointmentStatus.Declined);

//                Stock s = new Stock(app.Vacc, app.CheckDoseFromAppointment(), app.Location);
//                if (s.FindStock()) {
//
//                    if (s.MinusPendingQty(1, currentUser, "Decline Vaccination - " + app.getCode())) {
//                        General.AlertMsgError("Something went wrong, please try again later!", "Error");
//                        return;
//                    }
//
//                } 
//                else {
//                    s.GenerateId();
//                    s.MinusPendingQty(1, currentUser, "Decline Vaccination - " + app.getCode());
//                    FileOperation.SerializeObject(General.stockFileName, s);
//                }

                if (fo.ModifyRecord(app)) {
                    General.AlertMsgInfo("Appointment has been updated!", "Success");
                    pnlRejectReason.setVisible(false);
                    PopulateUserData();
                    return;
                }
            }

        } else {
            General.AlertMsgInfo("Appointment was not updated. Please either Accept or Decline the appointment.", "Alert");
        }
    }//GEN-LAST:event_btnVAReject1ActionPerformed

    private void btnCertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCertActionPerformed
        try {
            Image covid19 = ImageIO.read(new File("Images\\cert.jpg"));

            int width = covid19.getWidth(null);
            int height = covid19.getHeight(null);

            // Constructs a BufferedImage of one of the predefined image types.
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            // Create a graphics which can be used to draw into the buffered image
            Graphics2D g2d = bufferedImage.createGraphics();

            g2d.drawImage(covid19, 0, 0, null);
            g2d.setColor(Color.black);

            g2d.setColor(Color.darkGray);
            g2d.setFont(new Font("TimesRoman", Font.BOLD, 36));
            g2d.drawString("DIGITAL CERTIFICATE", 50, 150);

            g2d.setFont(new Font("TimesRoman", Font.PLAIN, 36));
            g2d.drawString("for COVID-19 Vaccination", 50, 200);

            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(50, 220, 500, 220);

            g2d.setColor(Color.BLUE);
            g2d.setFont(new Font("TimesRoman", Font.BOLD, 36));
            g2d.drawString(currentUser.getFullName(), 50, 275);

            g2d.setColor(new Color(111, 195, 255, 50));
            g2d.fillRect(50, 300, width - 50, 200);

            ArrayList<Appointment> al = new ArrayList<Appointment>();
            int i = 0;

            for (Object x : htAppointment.values()) {
                Appointment a = (Appointment) x;
                if (a.getStatus().equals(AppointmentStatus.Completed)) {
                    al.add(a);

                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("TimesRoman", Font.BOLD, 24));
                    g2d.drawString(a.Vacc.GetCodeName(), 75, 350);

                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("TimesRoman", Font.PLAIN, 18));
                    g2d.drawString("#" + String.valueOf(a.CheckDoseFromAppointment()), 100 + (i * 150), 400);
                    g2d.drawString(a.getLocation().GetCodeName(), 100 + (i * 150), 420);
                    g2d.drawString(a.getVaccinationDate().GetLongDate(), 100 + (i * 150), 440);
                    i++;
                }
            }

            g2d.dispose();

            // Save as JPG
            String home = System.getProperty("user.home");
            File file = new File(home+"/Downloads/" + currentUser.Username + "Vaccination_Certificate.jpg"); 
            ImageIO.write(bufferedImage, "jpg", file);
            General.AlertMsgInfo("Certificate is generated and downloaded in your PC Download folder.", "Generate Success!");
        } catch (Exception ex) {

        }
    }//GEN-LAST:event_btnCertActionPerformed

    protected void manipulatePdf(String dest) throws Exception {

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
            java.util.logging.Logger.getLogger(LoadingPage.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoadingPage.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoadingPage.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoadingPage.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoadingPage().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCert;
    private javax.swing.JButton btnEditProfile;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JButton btnVAAccept;
    private javax.swing.JButton btnVAReject;
    private javax.swing.JButton btnVAReject1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cmbPState;
    private javax.swing.JComboBox<String> cmbVACentre;
    private javax.swing.JComboBox<String> cmbVAVaccine;
    private com.toedter.calendar.JDateChooser dateVADate;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDob;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblPwNoMatch;
    private javax.swing.JLabel lblVSName1;
    private javax.swing.JLabel lblVSName2;
    private javax.swing.JLabel lblVSSymtom1;
    private javax.swing.JLabel lblVaccinatedStatus;
    private javax.swing.JPanel pnlCredential;
    private javax.swing.JPanel pnlRejectReason;
    private javax.swing.JPanel pnlVA;
    private javax.swing.JTable tblAppointment;
    private javax.swing.JTextField txtPAddCity;
    private javax.swing.JTextField txtPAddNo;
    private javax.swing.JTextField txtPAddPostcode;
    private javax.swing.JTextField txtPAddStreet;
    private javax.swing.JPasswordField txtPCfmPw;
    private javax.swing.JTextField txtPEmail;
    private javax.swing.JTextField txtPFullName;
    private javax.swing.JTextField txtPGender;
    private javax.swing.JTextField txtPIC;
    private javax.swing.JTextField txtPNationality;
    private javax.swing.JPasswordField txtPNewPw;
    private javax.swing.JTextField txtPPhone;
    private javax.swing.JTextField txtPUsername;
    private javax.swing.JTextArea txtVAAddress;
    private javax.swing.JTextField txtVACode;
    private javax.swing.JTextField txtVAReason;
    // End of variables declaration//GEN-END:variables
}
