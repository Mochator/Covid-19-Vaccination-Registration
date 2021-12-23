/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import javax.swing.JFrame;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author ljk99
 */
public class StockistLoadingPage extends javax.swing.JFrame {

    private static Stockist currentUser;
    private Hashtable<String, Object> htStock;
    private Hashtable<String, Object> htActualStockAudit;
    private Hashtable<String, Object> htPendingStockAudit;
    private Hashtable<String, Object> htVac;
    private Hashtable<String, Object> htVacCentre;

    /**
     * Creates new form StockistLoadingPage
     */
    public StockistLoadingPage() {
        initComponents();
        editProfile(false);

        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        this.setSize(d);
        this.setMaximumSize(d);
    }

    private void editProfile(boolean b) {
        btnAdPSave.setVisible(b);
        btnAdPInfoEdit.setVisible(!b);
        pnlCredential.setVisible(b);

        Color col = b ? Color.WHITE : Color.GRAY;

        for (Component x : pnlCredential.getComponents()) {

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
        ArrayList<Object> allStocks = FileOperation.DeserializeObject(General.stockFileName);
        htStock = FileOperation.ConvertToHashTable(allStocks);

        ArrayList<Object> allActualStockAudit = FileOperation.DeserializeObject(General.stockAuditFileName);
        htActualStockAudit = FileOperation.ConvertToHashTable(allActualStockAudit);

        ArrayList<Object> allPendingStockAudit = FileOperation.DeserializeObject(General.pendingStockAuditFileName);
        htPendingStockAudit = FileOperation.ConvertToHashTable(allPendingStockAudit);

        ArrayList<Object> allVaccines = FileOperation.DeserializeObject(General.vaccineFileName);
        htVac = FileOperation.ConvertToHashTable(allVaccines);

        ArrayList<Object> allCentres = FileOperation.DeserializeObject(General.vaccineCentreFileName);
        htVacCentre = FileOperation.ConvertToHashTable(allCentres);
    }

    private void InitComboData() {
        //---Profile Tab---
        //Init Gender ComboBox
        General.GenderString().forEach(d -> cboAdPGender.addItem(d));

        //---Stock---
        //Init Vaccine Types 
        for (Object x : htVac.values()) {
            Vaccine v = (Vaccine) x;
            cboMsVac.addItem(v.getVacCode() + " - " + v.getName());
        }

        //Init Vaccine Centres 
        for (Object x : htVacCentre.values()) {
            VaccineCentre v = (VaccineCentre) x;
            cboMsCentre.addItem(v.getVacCode() + " - " + v.getName());
        }

        //Init Vaccine Types 
        for (Object x : htVac.values()) {
            Vaccine v = (Vaccine) x;
            cboMsSearchVac.addItem(v.getVacCode() + " - " + v.getName());
        }

        //Search Stock Flow
        cboSfSearchVac.removeAllItems();
        cboSfSearchVac.insertItemAt("All Vaccine", 0);
        for (Object x : htVac.values()) {
            Vaccine v = (Vaccine) x;
            cboSfSearchVac.addItem(v.getVacCode() + " - " + v.getName());
        }
        cboSfSearchVac.setSelectedIndex(0);

        //Search Stock Flow
        cboSfSearchVac1.removeAllItems();
        cboSfSearchVac1.insertItemAt("All Vaccine", 0);
        for (Object x : htVac.values()) {
            Vaccine v = (Vaccine) x;
            cboSfSearchVac1.addItem(v.getVacCode() + " - " + v.getName());
        }
        cboSfSearchVac1.setSelectedIndex(0);

    }

    private void PopulateUserData() {
        //---Profile Tab---
        txtAdPFullName.setText(currentUser.getFullName());

        String Gender = currentUser.getGender() == General.GenderMale ? General.GenderMaleString : General.GenderFemaleString;
        cboAdPGender.setSelectedItem(Gender);

        jDob.setCalendar(currentUser.Dob.getCal());

        txtAdPNo.setText(currentUser.getContact());
        txtPEmail.setText(currentUser.getEmail());
        txtAdPUser.setText(currentUser.Username);

        txtAdPRole.setText(General.PersonnelRoleStockist);
        calAdPHiredDate.setCalendar(currentUser.HiredDate.getCal());
        txtAdPVacCentre.setText(currentUser.VacCentre.getVacCode() + " - " + currentUser.VacCentre.getName());

        txtPNewPw.setText("");
        txtPCfmPw.setText("");
        lblWbUsername.setText(currentUser.getFirst_Name());

        cboMsCentre.setSelectedItem(currentUser.VacCentre.getVacCode() + " - " + currentUser.VacCentre.getName());

    }

    private void InitTableRecords() {
        //----------Stock----------
        DefaultTableModel dtmMs = (DefaultTableModel) tblMs.getModel();
        dtmMs.setRowCount(0);
        int i = 0;

        for (Object x : htStock.values()) {
            Stock a = (Stock) x;

            if (!a.VacCentre.getVacCode().equals(currentUser.VacCentre.getVacCode())) {
                continue;
            }

            Object[] dtmObj = new Object[]{
                ++i,
                a.VacType.GetCodeName(),
                a.getDose(),
                a.getQuantity(),
                a.getPendingQuantity(),
                a.VacCentre.GetCodeName()
            };

            dtmMs.addRow(dtmObj);

        }

        tblMs.setModel(dtmMs);

        //----------Stock audit----------
        DefaultTableModel dtmSf = (DefaultTableModel) tblSf.getModel();
        dtmSf.setRowCount(0);
        int i2 = 0;

        for (Object x : htActualStockAudit.values()) {
            ActualStock a = (ActualStock) x;

            Object[] dtmObj = new Object[]{
                ++i2,
                a.getVacStock().VacType.GetCodeName(),
                a.getVacStock().getDose(),
                a.getQuantity(),
                a.getCreateDate().GetShortDateTime(),
                a.getCreatedBy() != null ? a.getCreatedBy().Username : "System",
                a.getRemarks()
            };

            dtmSf.addRow(dtmObj);

        }

        tblSf.setModel(dtmSf);

        //----------Stock audit----------
        DefaultTableModel dtmSf1 = (DefaultTableModel) tblSf1.getModel();
        dtmSf1.setRowCount(0);
        int i3 = 0;

        for (Object x : htPendingStockAudit.values()) {
            PendingStock a = (PendingStock) x;

            Object[] dtmObj = new Object[]{
                ++i2,
                a.getVacStock().VacType.GetCodeName(),
                a.getVacStock().getDose(),
                a.getQuantity(),
                a.getCreateDate().GetShortDateTime(),
                a.getCreatedBy() != null ? a.getCreatedBy().Username : "System",
                a.getRemarks()

            };

            dtmSf1.addRow(dtmObj);

        }

        tblSf1.setModel(dtmSf1);

    }

    public void setCurrentUser(Stockist user) {
        this.currentUser = user;
        InitGlobalData();
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
        jPanel20 = new javax.swing.JPanel();
        lblWbUsername = new javax.swing.JLabel();
        lblVSName6 = new javax.swing.JLabel();
        lblVSName7 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        txtAdPUser = new javax.swing.JTextField();
        txtAdPRole = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        jLabel89 = new javax.swing.JLabel();
        calAdPHiredDate = new com.toedter.calendar.JDateChooser();
        txtAdPVacCentre = new javax.swing.JTextField();
        txtAdPFullName = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jDob = new com.toedter.calendar.JDateChooser();
        cboAdPGender = new javax.swing.JComboBox<>();
        txtAdPNo = new javax.swing.JTextField();
        pnlCredential = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        txtPEmail = new javax.swing.JTextField();
        txtPNewPw = new javax.swing.JPasswordField();
        txtPCfmPw = new javax.swing.JPasswordField();
        jLabel99 = new javax.swing.JLabel();
        lblPwNoMatch = new javax.swing.JLabel();
        jLabel151 = new javax.swing.JLabel();
        btnAdPSave = new javax.swing.JButton();
        btnAdPInfoEdit = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMs = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        txtMsCurrentAs = new javax.swing.JTextField();
        btnTaMark1 = new javax.swing.JButton();
        jLabel68 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        txtMsNewAs = new javax.swing.JTextField();
        txtMsRemarks = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        txtMsCurrentPs = new javax.swing.JTextField();
        txtMsNewPs = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel71 = new javax.swing.JLabel();
        sldrMsPs = new javax.swing.JSlider();
        jLabel36 = new javax.swing.JLabel();
        cboMsPs = new javax.swing.JComboBox<>();
        sldrMsAs = new javax.swing.JSlider();
        cboMsAs = new javax.swing.JComboBox<>();
        cboMsVac = new javax.swing.JComboBox<>();
        cboMsCentre = new javax.swing.JComboBox<>();
        cboMsDose = new javax.swing.JComboBox<>();
        jLabel38 = new javax.swing.JLabel();
        cboMsSearchVac = new javax.swing.JComboBox<>();
        cboMsSearchDose = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        txtSfSearch = new javax.swing.JTextField();
        btnSfSearch = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblSf = new javax.swing.JTable();
        cboSfSearchDose = new javax.swing.JComboBox<>();
        cboSfSearchVac = new javax.swing.JComboBox<>();
        calSfSearch = new com.toedter.calendar.JDateChooser();
        jPanel5 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        txtSfSearch1 = new javax.swing.JTextField();
        btnSfSearch1 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblSf1 = new javax.swing.JTable();
        cboSfSearchDose1 = new javax.swing.JComboBox<>();
        cboSfSearchVac1 = new javax.swing.JComboBox<>();
        calSfSearch1 = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        jPanel20.setBackground(new java.awt.Color(102, 0, 0));

        lblWbUsername.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 20)); // NOI18N
        lblWbUsername.setForeground(new java.awt.Color(0, 0, 0));
        lblWbUsername.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblWbUsername.setText("Username");

        lblVSName6.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 20)); // NOI18N
        lblVSName6.setForeground(new java.awt.Color(0, 0, 0));
        lblVSName6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVSName6.setText("Stockist Panel");

        lblVSName7.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 20)); // NOI18N
        lblVSName7.setForeground(new java.awt.Color(0, 0, 0));
        lblVSName7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblVSName7.setText("Welcome Back");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblWbUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblVSName6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lblVSName7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(lblVSName7, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblWbUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 88, Short.MAX_VALUE)
                .addComponent(lblVSName6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(61, 61, 61)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.setBackground(new java.awt.Color(0, 0, 0));
        jTabbedPane1.setForeground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Berlin Sans FB Demi", 0, 12)); // NOI18N
        jTabbedPane1.setOpaque(true);

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));

        jLabel60.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 36)); // NOI18N
        jLabel60.setForeground(new java.awt.Color(255, 255, 255));
        jLabel60.setText("User Profile");

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

        jLabel89.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel89.setForeground(new java.awt.Color(255, 255, 255));
        jLabel89.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel89.setText("Vaccine Centre:");

        calAdPHiredDate.setBackground(new java.awt.Color(255, 255, 255));
        calAdPHiredDate.setForeground(new java.awt.Color(0, 0, 0));
        calAdPHiredDate.setEnabled(false);

        txtAdPVacCentre.setEditable(false);
        txtAdPVacCentre.setBackground(new java.awt.Color(204, 204, 204));
        txtAdPVacCentre.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdPVacCentre.setForeground(new java.awt.Color(0, 0, 0));
        txtAdPVacCentre.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdPVacCentre.setEnabled(false);

        txtAdPFullName.setEditable(false);
        txtAdPFullName.setBackground(new java.awt.Color(204, 204, 204));
        txtAdPFullName.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdPFullName.setForeground(new java.awt.Color(0, 0, 0));
        txtAdPFullName.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdPFullName.setEnabled(false);

        jLabel57.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(255, 255, 255));
        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel57.setText("Full Name:");

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

        jDob.setBackground(new java.awt.Color(255, 255, 255));
        jDob.setForeground(new java.awt.Color(0, 0, 0));
        jDob.setEnabled(false);

        cboAdPGender.setBackground(new java.awt.Color(204, 204, 204));
        cboAdPGender.setForeground(new java.awt.Color(0, 0, 0));
        cboAdPGender.setMaximumRowCount(2);
        cboAdPGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { " ", "Male", "Female" }));
        cboAdPGender.setEnabled(false);
        cboAdPGender.setOpaque(true);

        txtAdPNo.setEditable(false);
        txtAdPNo.setBackground(new java.awt.Color(204, 204, 204));
        txtAdPNo.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtAdPNo.setForeground(new java.awt.Color(0, 0, 0));
        txtAdPNo.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        txtAdPNo.setEnabled(false);

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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel60, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(658, 658, 658))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(txtAdPUser, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel78, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(txtAdPRole, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel88, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(122, 122, 122)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtAdPFullName)
                                            .addComponent(calAdPHiredDate, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtAdPVacCentre)))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(381, 381, 381)
                                .addComponent(btnAdPSave, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAdPInfoEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(pnlCredential, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                .addComponent(jLabel58, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel89, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel89)
                    .addComponent(txtAdPVacCentre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(txtAdPFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addContainerGap(653, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanel4);

        jTabbedPane1.addTab("Profile", jScrollPane2);

        jTabbedPane5.setBackground(new java.awt.Color(51, 51, 51));
        jTabbedPane5.setOpaque(true);

        jPanel18.setBackground(new java.awt.Color(51, 51, 51));

        tblMs.setBackground(new java.awt.Color(255, 255, 255));
        tblMs.setForeground(new java.awt.Color(0, 0, 0));
        tblMs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Vaccine", "Dose", "Quantity", "Pending Quantity", "Centre"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblMs.setSelectionBackground(new java.awt.Color(51, 51, 51));
        tblMs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblMs);

        jPanel8.setBackground(new java.awt.Color(51, 153, 255));
        jPanel8.setPreferredSize(new java.awt.Dimension(518, 500));

        jLabel25.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(0, 0, 0));
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("Vaccine Centre:");

        jLabel26.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Vaccine:");

        jLabel27.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(0, 0, 0));
        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("Current");

        txtMsCurrentAs.setEditable(false);
        txtMsCurrentAs.setBackground(new java.awt.Color(204, 204, 204));
        txtMsCurrentAs.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMsCurrentAs.setForeground(new java.awt.Color(0, 0, 0));
        txtMsCurrentAs.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMsCurrentAs.setText("0");
        txtMsCurrentAs.setEnabled(false);
        txtMsCurrentAs.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnTaMark1.setBackground(new java.awt.Color(102, 255, 102));
        btnTaMark1.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        btnTaMark1.setForeground(new java.awt.Color(0, 0, 0));
        btnTaMark1.setText("Done");
        btnTaMark1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnTaMark1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTaMark1.setOpaque(true);
        btnTaMark1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaMark1ActionPerformed(evt);
            }
        });

        jLabel68.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 30)); // NOI18N
        jLabel68.setForeground(new java.awt.Color(0, 0, 0));
        jLabel68.setText("Modify Stock");

        jLabel30.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(0, 0, 0));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("Dose #:");

        jLabel69.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 18)); // NOI18N
        jLabel69.setForeground(new java.awt.Color(0, 0, 0));
        jLabel69.setText("Stock Info.");

        jLabel31.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(0, 0, 0));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("New");

        txtMsNewAs.setBackground(new java.awt.Color(255, 255, 255));
        txtMsNewAs.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMsNewAs.setForeground(new java.awt.Color(0, 0, 0));
        txtMsNewAs.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMsNewAs.setText("0");
        txtMsNewAs.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtMsNewAs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMsNewAsKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtMsNewAsKeyTyped(evt);
            }
        });

        txtMsRemarks.setBackground(new java.awt.Color(255, 255, 255));
        txtMsRemarks.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMsRemarks.setForeground(new java.awt.Color(0, 0, 0));
        txtMsRemarks.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMsRemarks.setSelectionColor(new java.awt.Color(255, 255, 51));

        jLabel33.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(0, 0, 0));
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel33.setText("Remarks");

        txtMsCurrentPs.setEditable(false);
        txtMsCurrentPs.setBackground(new java.awt.Color(204, 204, 204));
        txtMsCurrentPs.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMsCurrentPs.setForeground(new java.awt.Color(0, 0, 0));
        txtMsCurrentPs.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMsCurrentPs.setText("0");
        txtMsCurrentPs.setEnabled(false);
        txtMsCurrentPs.setSelectionColor(new java.awt.Color(255, 255, 51));

        txtMsNewPs.setBackground(new java.awt.Color(255, 255, 255));
        txtMsNewPs.setFont(new java.awt.Font("Berlin Sans FB", 0, 12)); // NOI18N
        txtMsNewPs.setForeground(new java.awt.Color(0, 0, 0));
        txtMsNewPs.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtMsNewPs.setText("0");
        txtMsNewPs.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtMsNewPs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMsNewPsKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtMsNewPsKeyTyped(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(0, 0, 0));
        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel34.setText("Actual Stock");

        jLabel35.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(0, 0, 0));
        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("Pending Stock");

        jLabel71.setFont(new java.awt.Font("Berlin Sans FB Demi", 1, 18)); // NOI18N
        jLabel71.setForeground(new java.awt.Color(0, 0, 0));
        jLabel71.setText("Stock Adjustment");

        sldrMsPs.setMaximum(10000);
        sldrMsPs.setPaintLabels(true);
        sldrMsPs.setPaintTicks(true);
        sldrMsPs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldrMsPsStateChanged(evt);
            }
        });

        jLabel36.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(0, 0, 0));
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel36.setText("Pending Stock");

        cboMsPs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "+", "-" }));
        cboMsPs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMsPsActionPerformed(evt);
            }
        });

        sldrMsAs.setMaximum(10000);
        sldrMsAs.setPaintLabels(true);
        sldrMsAs.setPaintTicks(true);
        sldrMsAs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sldrMsAsStateChanged(evt);
            }
        });

        cboMsAs.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "+", "-" }));
        cboMsAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMsAsActionPerformed(evt);
            }
        });

        cboMsVac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));
        cboMsVac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMsVacActionPerformed(evt);
            }
        });

        cboMsCentre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));
        cboMsCentre.setEnabled(false);
        cboMsCentre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMsCentreActionPerformed(evt);
            }
        });

        cboMsDose.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select" }));
        cboMsDose.setEnabled(false);
        cboMsDose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMsDoseActionPerformed(evt);
            }
        });

        jLabel38.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(0, 0, 0));
        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel38.setText("Actual Stock");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel71, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 10, Short.MAX_VALUE))
                                    .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtMsCurrentAs, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtMsCurrentPs, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtMsNewAs, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtMsNewPs, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(80, 80, 80))
                                    .addComponent(txtMsRemarks)
                                    .addComponent(cboMsVac, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboMsCentre, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(cboMsDose, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel8Layout.createSequentialGroup()
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(sldrMsAs, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                                            .addComponent(sldrMsPs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cboMsAs, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(cboMsPs, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                            .addComponent(jLabel69, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnTaMark1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel8Layout.createSequentialGroup()
                                .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(18, 18, 18))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(jLabel68, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jLabel69)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(cboMsCentre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(cboMsVac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(cboMsDose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(jLabel31))
                .addGap(6, 6, 6)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMsNewAs)
                    .addComponent(txtMsCurrentAs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMsCurrentPs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMsNewPs)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jLabel71)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboMsAs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38)
                    .addComponent(sldrMsAs, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel36)
                    .addComponent(sldrMsPs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboMsPs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(txtMsRemarks, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(42, 42, 42)
                .addComponent(btnTaMark1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
        );

        cboMsSearchVac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Vaccines" }));
        cboMsSearchVac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMsSearchVacActionPerformed(evt);
            }
        });

        cboMsSearchDose.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Dose" }));
        cboMsSearchDose.setEnabled(false);
        cboMsSearchDose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMsSearchDoseActionPerformed(evt);
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
                        .addComponent(cboMsSearchVac, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboMsSearchDose, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(109, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cboMsSearchVac)
                    .addComponent(cboMsSearchDose, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 666, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap(565, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Vaccine Management", jPanel18);

        jPanel12.setBackground(new java.awt.Color(51, 51, 51));

        txtSfSearch.setBackground(new java.awt.Color(255, 255, 255));
        txtSfSearch.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        txtSfSearch.setForeground(new java.awt.Color(0, 0, 0));
        txtSfSearch.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSfSearch.setSelectionColor(new java.awt.Color(255, 255, 51));

        btnSfSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/search.png"))); // NOI18N
        btnSfSearch.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnSfSearch.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSfSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSfSearchActionPerformed(evt);
            }
        });

        tblSf.setBackground(new java.awt.Color(255, 255, 255));
        tblSf.setForeground(new java.awt.Color(0, 0, 0));
        tblSf.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Vaccine", "Dose", "Quantity", "Create Date", "Created By", "Remarks"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSf.setSelectionBackground(new java.awt.Color(51, 51, 51));
        jScrollPane4.setViewportView(tblSf);

        cboSfSearchDose.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Dose" }));
        cboSfSearchDose.setEnabled(false);
        cboSfSearchDose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSfSearchDoseActionPerformed(evt);
            }
        });

        cboSfSearchVac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Vaccines" }));
        cboSfSearchVac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSfSearchVacActionPerformed(evt);
            }
        });

        calSfSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                calSfSearchMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 988, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(txtSfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboSfSearchVac, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboSfSearchDose, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(calSfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSfSearch)
                    .addComponent(cboSfSearchVac)
                    .addComponent(cboSfSearchDose)
                    .addComponent(calSfSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 95, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 561, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Stock Audit", jPanel3);

        jPanel13.setBackground(new java.awt.Color(51, 51, 51));

        txtSfSearch1.setBackground(new java.awt.Color(255, 255, 255));
        txtSfSearch1.setFont(new java.awt.Font("Berlin Sans FB", 0, 18)); // NOI18N
        txtSfSearch1.setForeground(new java.awt.Color(0, 0, 0));
        txtSfSearch1.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtSfSearch1.setSelectionColor(new java.awt.Color(255, 255, 51));
        txtSfSearch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSfSearch1ActionPerformed(evt);
            }
        });

        btnSfSearch1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vaccinationsystem/search.png"))); // NOI18N
        btnSfSearch1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        btnSfSearch1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSfSearch1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSfSearch1ActionPerformed(evt);
            }
        });

        tblSf1.setBackground(new java.awt.Color(255, 255, 255));
        tblSf1.setForeground(new java.awt.Color(0, 0, 0));
        tblSf1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No.", "Vaccine", "Dose", "Quantity", "Create Date", "Created By", "Remarks"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSf1.setSelectionBackground(new java.awt.Color(51, 51, 51));
        jScrollPane5.setViewportView(tblSf1);

        cboSfSearchDose1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Dose" }));
        cboSfSearchDose1.setEnabled(false);
        cboSfSearchDose1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSfSearchDose1ActionPerformed(evt);
            }
        });

        cboSfSearchVac1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Vaccines" }));
        cboSfSearchVac1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSfSearchVac1ActionPerformed(evt);
            }
        });

        calSfSearch1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                calSfSearch1MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 984, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(txtSfSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSfSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cboSfSearchVac1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboSfSearchDose1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(calSfSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnSfSearch1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSfSearch1)
                    .addComponent(cboSfSearchVac1)
                    .addComponent(cboSfSearchDose1)
                    .addComponent(calSfSearch1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 96, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 558, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Pending Stock Audit", jPanel5);

        jScrollPane3.setViewportView(jTabbedPane5);

        jTabbedPane1.addTab("Stock Management", jScrollPane3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1095, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1296, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        this.dispose();
        Login login = new Login();
        login.setVisible(true);
    }//GEN-LAST:event_btnLogoutActionPerformed

    private void tblMsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMsMouseClicked


    }//GEN-LAST:event_tblMsMouseClicked

    private void MsSearch(Vaccine vac, int dose) {
        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblMs.getModel();
        dtm.setRowCount(0);

        for (Object x : htStock.values()) {
            Stock a = (Stock) x;

            if (!a.VacCentre.getVacCode().equals(currentUser.VacCentre.getVacCode())) {
                continue;
            }

            if (vac != null) {
                if (!a.VacType.getVacCode().equals(vac.getVacCode())) {
                    continue;
                }
            }

            if (dose != 0) {
                if (a.getDose() != dose) {
                    continue;
                }
            }

            Object[] dtmObj = new Object[]{
                ++i,
                a.VacType.GetCodeName(),
                a.getDose(),
                a.getQuantity(),
                a.getPendingQuantity(),
                a.VacCentre.GetCodeName()
            };

            dtm.addRow(dtmObj);

        }

        tblMs.setModel(dtm);
    }

    private void btnSfSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSfSearchActionPerformed
        String search = (!txtSfSearch.getText().isBlank() ? txtSfSearch.getText() : "").trim().toLowerCase();
        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = cboSfSearchVac.getSelectedIndex() > 0 ? (Vaccine) vacArray[cboSfSearchVac.getSelectedIndex() - 1] : null;

        int dose = cboSfSearchDose.getSelectedIndex() > 0 ? Integer.parseInt(String.valueOf(cboSfSearchDose.getSelectedItem())) : 0;

        MyDateTime mdt = calSfSearch.getCalendar() == null ? null : new MyDateTime(calSfSearch.getCalendar());

        SfSearch(search, vac, dose, mdt);
    }//GEN-LAST:event_btnSfSearchActionPerformed

    private void SfSearch(String search, Vaccine vac, int dose, MyDateTime mdt) {
        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblSf.getModel();
        dtm.setRowCount(0);

        for (Object x : htActualStockAudit.values()) {
            ActualStock a = (ActualStock) x;

            if (!a.getVacStock().VacCentre.getVacCode().equals(currentUser.VacCentre.getVacCode())) {
                continue;
            }

            if (vac != null) {
                if (!a.getVacStock().VacType.getVacCode().equals(vac.getVacCode())) {
                    continue;
                }
            }

            if (dose != 0) {
                if (a.getVacStock().getDose() != dose) {
                    continue;
                }
            }

            Object[] dtmObj = new Object[]{
                ++i,
                a.getVacStock().VacType.GetCodeName(),
                a.getVacStock().getDose(),
                a.getQuantity(),
                a.getCreateDate().GetShortDateTime(),
                a.getCreatedBy() != null ? a.getCreatedBy().Username : "System",
                a.getRemarks()
            };

            dtm.addRow(dtmObj);

        }

        tblSf.setModel(dtm);
    }

    private void SfSearch1(String search, Vaccine vac, int dose, MyDateTime mdt) {
        int i = 0;

        DefaultTableModel dtm = (DefaultTableModel) tblSf1.getModel();
        dtm.setRowCount(0);

        for (Object x : htPendingStockAudit.values()) {
            PendingStock a = (PendingStock) x;

            if (!a.getVacStock().VacCentre.getVacCode().equals(currentUser.VacCentre.getVacCode())) {
                continue;
            }

            if (vac != null) {
                if (!a.getVacStock().VacType.getVacCode().equals(vac.getVacCode())) {
                    continue;
                }
            }

            if (dose != 0) {
                if (a.getVacStock().getDose() != dose) {
                    continue;
                }
            }

            Object[] dtmObj = new Object[]{
                ++i,
                a.getVacStock().VacType.GetCodeName(),
                a.getVacStock().getDose(),
                a.getQuantity(),
                a.getCreateDate().GetShortDateTime(),
                a.getCreatedBy() != null ? a.getCreatedBy().Username : "System",
                a.getRemarks()
            };

            dtm.addRow(dtmObj);

        }

        tblSf1.setModel(dtm);
    }

    private void txtPNewPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNewPwKeyReleased
        txtPCfmPw.setEnabled(txtPNewPw.getPassword().length > 0);
        txtPCfmPw.setText("");
        lblPwNoMatch.setVisible(txtPCfmPw.isEnabled());
    }//GEN-LAST:event_txtPNewPwKeyReleased

    private void txtPNewPwKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPNewPwKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPNewPwKeyTyped

    private void txtPCfmPwKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPCfmPwKeyReleased
        lblPwNoMatch.setVisible(!String.valueOf(txtPNewPw.getPassword()).equals(String.valueOf(txtPCfmPw.getPassword())));
    }//GEN-LAST:event_txtPCfmPwKeyReleased

    private void btnAdPSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdPSaveActionPerformed
        if (General.AlertQuestionYesNo("Do you want to save your changes?", "Save Confirmation") == 1) {
            return;
        }

        //Check field filled
        if (txtAdPNo.getText().isBlank() || txtPEmail.getText().isBlank()) {
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
            InitComboData();
        } else {
            General.AlertMsgError("Profile changes were not saved, please try again later.", "Error!");
        }
    }//GEN-LAST:event_btnAdPSaveActionPerformed

    private void btnAdPInfoEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdPInfoEditActionPerformed
        editProfile(true);
    }//GEN-LAST:event_btnAdPInfoEditActionPerformed

    private void btnTaMark1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaMark1ActionPerformed

        if (cboMsCentre.getSelectedIndex() <= 0 || cboMsVac.getSelectedIndex() <= 0 || cboMsDose.getSelectedIndex() <= 0 || !(sldrMsAs.getValue() != 0 || sldrMsPs.getValue() != 0)) {
            General.AlertMsgError("All fields must be filled!", "Error");
            return;
        }

        Object[] vcArray = (Object[]) htVacCentre.values().toArray();
        VaccineCentre vc = (VaccineCentre) vcArray[cboMsCentre.getSelectedIndex() - 1];

        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = (Vaccine) vacArray[cboMsVac.getSelectedIndex() - 1];

        int dose = Integer.parseInt(String.valueOf(cboMsDose.getSelectedItem()));

        //Modify Stock
        Stock s = new Stock(vac, dose, vc);
        String remarks = txtMsRemarks.getText();
        int As = sldrMsAs.getValue();
        int Ps = sldrMsPs.getValue();

        boolean success = false;

        if (s.FindStock()) {
            if (As > 0) {
                if (cboMsAs.getSelectedIndex() == 0) {
                    if (!s.AddQty(As, currentUser, remarks)) {
                        General.AlertMsgError("Something went wrong, please try again later!", "Error");
                        return;
                    }
                } else {
                    if (!s.MinusQty(As, currentUser, remarks)) {
                        General.AlertMsgError("Something went wrong, please try again later!", "Error");
                        return;
                    }
                }
            }

            if (Ps > 0) {
                if (cboMsAs.getSelectedIndex() == 0) {
                    if (!s.AddPendingQty(Ps, currentUser, remarks)) {
                        General.AlertMsgError("Something went wrong, please try again later!", "Error");
                        return;
                    }
                } else {
                    if (!s.MinusPendingQty(Ps, currentUser, remarks)) {
                        General.AlertMsgError("Something went wrong, please try again later!", "Error");
                        return;
                    }
                }
            }

            //Save
            FileOperation fo = new FileOperation(s.getId(), General.stockFileName);
            success = fo.ModifyRecord(s);

        } else {
            s.GenerateId();

            if (As > 0) {
                if (cboMsAs.getSelectedIndex() == 0) {
                    if (!s.AddQty(As, currentUser, remarks)) {
                        General.AlertMsgError("Something went wrong, please try again later!", "Error");
                        return;
                    }
                } else {
                    if (!s.MinusQty(As, currentUser, remarks)) {
                        General.AlertMsgError("Something went wrong, please try again later!", "Error");
                        return;
                    }
                }
            }

            if (Ps > 0) {
                if (cboMsAs.getSelectedIndex() == 0) {
                    if (!s.AddPendingQty(Ps, currentUser, remarks)) {
                        General.AlertMsgError("Something went wrong, please try again later!", "Error");
                        return;
                    }
                } else {
                    if (!s.MinusPendingQty(Ps, currentUser, remarks)) {
                        General.AlertMsgError("Something went wrong, please try again later!", "Error");
                        return;
                    }
                }
            }

            success = FileOperation.SerializeObject(General.stockFileName, s);

        }

        if (success) {
            General.AlertMsgInfo(s.VacType.GetCodeName() + " in " + s.VacCentre.GetCodeName() + " is updated!", "Success");
            InitGlobalData();
            InitTableRecords();
            cboMsDose.setSelectedIndex(0);
            cboMsVac.setSelectedIndex(0);
            cboMsAs.setSelectedIndex(0);
            cboMsPs.setSelectedIndex(0);
            txtMsRemarks.setText("");
        } else {
            General.AlertMsgError("Failed to update" + s.VacType.GetCodeName() + " in " + s.VacCentre.GetCodeName(), "Error");
        }


    }//GEN-LAST:event_btnTaMark1ActionPerformed

    private void cboMsSearchDoseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMsSearchDoseActionPerformed
        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = cboMsSearchVac.getSelectedIndex() > 0 ? (Vaccine) vacArray[cboMsSearchVac.getSelectedIndex() - 1] : null;

        int dose = cboMsSearchDose.getSelectedIndex() > 0 ? Integer.parseInt(String.valueOf(cboMsSearchDose.getSelectedItem())) : 0;

        MsSearch(vac, dose);
    }//GEN-LAST:event_cboMsSearchDoseActionPerformed

    private void cboMsVacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMsVacActionPerformed
        cboMsDose.setEnabled(false);
        cboMsDose.removeAllItems();
        cboMsDose.addItem("Select");

        if (cboMsVac.getSelectedIndex() > 0 && cboMsCentre.getSelectedIndex() > 0) {

            Object[] vacArray = (Object[]) htVac.values().toArray();
            Vaccine vac = (Vaccine) vacArray[cboMsVac.getSelectedIndex() - 1];
            int i = 1;
            while (i <= vac.getDoseCount()) {
                cboMsDose.addItem(String.valueOf(i));
                i++;
            }
            cboMsDose.setEnabled(true);
        }
    }//GEN-LAST:event_cboMsVacActionPerformed

    private void cboMsCentreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMsCentreActionPerformed
        cboMsDose.setEnabled(false);
        cboMsDose.removeAllItems();
        cboMsDose.addItem("Select");

        if (cboMsVac.getSelectedIndex() > 0 && cboMsCentre.getSelectedIndex() > 0) {

            Object[] vacArray = (Object[]) htVac.values().toArray();
            Vaccine vac = (Vaccine) vacArray[cboMsVac.getSelectedIndex() - 1];
            int i = 1;
            while (i <= vac.getDoseCount()) {
                cboMsDose.addItem(String.valueOf(i));
                i++;
            }
            cboMsDose.setEnabled(true);
        }
    }//GEN-LAST:event_cboMsCentreActionPerformed

    private void cboMsDoseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMsDoseActionPerformed
        if (cboMsDose.getSelectedIndex() > 0 && cboMsVac.getSelectedIndex() > 0 && cboMsCentre.getSelectedIndex() > 0) {

            Object[] vcArray = (Object[]) htVacCentre.values().toArray();
            VaccineCentre vc = (VaccineCentre) vcArray[cboMsCentre.getSelectedIndex() - 1];
            Object[] vacArray = (Object[]) htVac.values().toArray();
            Vaccine vac = (Vaccine) vacArray[cboMsVac.getSelectedIndex() - 1];

            int dose = Integer.parseInt(String.valueOf(cboMsDose.getSelectedItem()));
            Stock s = new Stock(vac, dose, vc);

            if (s.FindStock()) {
                txtMsCurrentAs.setText(String.valueOf(s.getQuantity()));
                txtMsCurrentPs.setText(String.valueOf(s.getPendingQuantity()));

                txtMsNewAs.setText(String.valueOf(s.getQuantity()));
                txtMsNewPs.setText(String.valueOf(s.getPendingQuantity()));

                sldrMsAs.setValue(0);
                sldrMsPs.setValue(0);

                cboMsAs.setEnabled(true);
                cboMsPs.setEnabled(true);

            } else {
                txtMsCurrentAs.setText("0");
                txtMsCurrentPs.setText("0");

                txtMsNewAs.setText("0");
                txtMsNewPs.setText("0");

                cboMsAs.setEnabled(false);
                cboMsPs.setEnabled(false);

                cboMsAs.setSelectedIndex(0);
                cboMsPs.setSelectedIndex(0);

                sldrMsAs.setValue(0);
                sldrMsPs.setValue(0);
            }

        }
    }//GEN-LAST:event_cboMsDoseActionPerformed

    private void sldrMsAsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldrMsAsStateChanged
        int val = sldrMsAs.getValue();
        int old = Integer.parseInt(txtMsCurrentAs.getText());

        if (!txtMsCurrentAs.getText().isBlank()) {
            if (cboMsAs.getSelectedIndex() == 0) {
                txtMsNewAs.setText(String.valueOf(old + val));

            } else {

                if (old - val < 0) {
                    sldrMsAs.setValue(old);
                    txtMsNewAs.setText("0");
                } else {
                    txtMsNewAs.setText(String.valueOf(old - val));
                }

            }
        }
    }//GEN-LAST:event_sldrMsAsStateChanged

    private void sldrMsPsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sldrMsPsStateChanged
        // TODO add your handling code here:
        int val = sldrMsPs.getValue();
        int old = Integer.parseInt(txtMsCurrentPs.getText());

        if (!txtMsCurrentPs.getText().isBlank()) {
            if (cboMsPs.getSelectedIndex() == 0) {
                txtMsNewPs.setText(String.valueOf(old + val));

            } else {

                if (old - val < 0) {
                    sldrMsPs.setValue(old);
                    txtMsNewPs.setText("0");
                } else {
                    txtMsNewPs.setText(String.valueOf(old - val));
                }

            }
        }
    }//GEN-LAST:event_sldrMsPsStateChanged

    private void cboMsAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMsAsActionPerformed
        int val = sldrMsAs.getValue();
        int old = Integer.parseInt(txtMsCurrentAs.getText());

        if (!txtMsCurrentAs.getText().isBlank()) {
            if (cboMsAs.getSelectedIndex() == 0) {
                txtMsNewAs.setText(String.valueOf(old + val));

            } else {

                if (old - val < 0) {
                    sldrMsAs.setValue(old);
                    txtMsNewAs.setText("0");
                } else {
                    txtMsNewAs.setText(String.valueOf(old - val));
                }

            }
        }
    }//GEN-LAST:event_cboMsAsActionPerformed

    private void cboMsPsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMsPsActionPerformed
        int val = sldrMsPs.getValue();
        int old = Integer.parseInt(txtMsCurrentPs.getText());

        if (!txtMsCurrentPs.getText().isBlank()) {
            if (cboMsPs.getSelectedIndex() == 0) {
                txtMsNewPs.setText(String.valueOf(old + val));

            } else {

                if (old - val < 0) {
                    sldrMsPs.setValue(old);
                    txtMsNewPs.setText("0");
                } else {
                    txtMsNewPs.setText(String.valueOf(old - val));
                }

            }
        }
    }//GEN-LAST:event_cboMsPsActionPerformed

    private void cboMsSearchVacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMsSearchVacActionPerformed
        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = cboMsSearchVac.getSelectedIndex() > 0 ? (Vaccine) vacArray[cboMsSearchVac.getSelectedIndex() - 1] : null;

        int dose = cboMsSearchDose.getSelectedIndex() > 0 ? Integer.parseInt(String.valueOf(cboMsSearchDose.getSelectedItem())) : 0;

        //Retrive dose
        if (cboMsSearchVac.getSelectedIndex() > 0) {
            int i = 1;
            cboMsSearchDose.removeAllItems();
            cboMsSearchDose.addItem("All Dose");
            while (i <= vac.getDoseCount()) {
                cboMsSearchDose.addItem(String.valueOf(i));
                i++;
            }
            cboMsSearchDose.setEnabled(true);
        } else {
            cboMsSearchDose.setEnabled(false);
            cboMsSearchDose.removeAllItems();
            cboMsSearchDose.addItem("All Dose");
        }

        MsSearch(vac, dose);
    }//GEN-LAST:event_cboMsSearchVacActionPerformed

    private void cboSfSearchDoseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSfSearchDoseActionPerformed
        String search = (!txtSfSearch.getText().isBlank() ? txtSfSearch.getText() : "").trim().toLowerCase();
        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = cboSfSearchVac.getSelectedIndex() > 0 ? (Vaccine) vacArray[cboSfSearchVac.getSelectedIndex() - 1] : null;

        int dose = cboSfSearchDose.getSelectedIndex() > 0 ? Integer.parseInt(String.valueOf(cboSfSearchDose.getSelectedItem())) : 0;

        MyDateTime mdt = calSfSearch.getCalendar() == null ? null : new MyDateTime(calSfSearch.getCalendar());

        SfSearch(search, vac, dose, mdt);
    }//GEN-LAST:event_cboSfSearchDoseActionPerformed

    private void cboSfSearchVacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSfSearchVacActionPerformed
        String search = (!txtSfSearch.getText().isBlank() ? txtSfSearch.getText() : "").trim().toLowerCase();
        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = cboSfSearchVac.getSelectedIndex() > 0 ? (Vaccine) vacArray[cboSfSearchVac.getSelectedIndex() - 1] : null;

        int dose = cboSfSearchDose.getSelectedIndex() > 0 ? Integer.parseInt(String.valueOf(cboSfSearchDose.getSelectedItem())) : 0;

        MyDateTime mdt = calSfSearch.getCalendar() == null ? null : new MyDateTime(calSfSearch.getCalendar());

        //Retrive dose
        if (cboSfSearchVac.getSelectedIndex() > 0) {
            int i = 1;
            cboSfSearchDose.removeAllItems();
            cboSfSearchDose.addItem("All Dose");
            while (i <= vac.getDoseCount()) {
                cboSfSearchDose.addItem(String.valueOf(i));
                i++;
            }
            cboSfSearchDose.setEnabled(true);
        } else {
            cboSfSearchDose.setEnabled(false);
            cboSfSearchDose.removeAllItems();
            cboSfSearchDose.addItem("All Dose");
        }

        SfSearch(search, vac, dose, mdt);
    }//GEN-LAST:event_cboSfSearchVacActionPerformed

    private void calSfSearchMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calSfSearchMouseReleased
        String search = (!txtSfSearch.getText().isBlank() ? txtSfSearch.getText() : "").trim().toLowerCase();
        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = cboSfSearchVac.getSelectedIndex() > 0 ? (Vaccine) vacArray[cboSfSearchVac.getSelectedIndex() - 1] : null;

        int dose = cboSfSearchDose.getSelectedIndex() > 0 ? Integer.parseInt(String.valueOf(cboSfSearchDose.getSelectedItem())) : 0;

        MyDateTime mdt = calSfSearch.getCalendar() == null ? null : new MyDateTime(calSfSearch.getCalendar());

        //Retrive dose
        if (cboSfSearchVac.getSelectedIndex() > 0) {
            int i = 1;
            cboSfSearchDose.removeAllItems();
            cboSfSearchDose.addItem("All Dose");
            while (i <= vac.getDoseCount()) {
                cboSfSearchDose.addItem(String.valueOf(i));
                i++;
            }
            cboSfSearchDose.setEnabled(true);
        } else {
            cboSfSearchDose.setEnabled(false);
            cboSfSearchDose.removeAllItems();
            cboSfSearchDose.addItem("All Dose");
        }

        SfSearch(search, vac, dose, mdt);
    }//GEN-LAST:event_calSfSearchMouseReleased

    private void btnSfSearch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSfSearch1ActionPerformed
        String search = (!txtSfSearch1.getText().isBlank() ? txtSfSearch1.getText() : "").trim().toLowerCase();
        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = cboSfSearchVac1.getSelectedIndex() > 0 ? (Vaccine) vacArray[cboSfSearchVac1.getSelectedIndex() - 1] : null;

        int dose = cboSfSearchDose1.getSelectedIndex() > 0 ? Integer.parseInt(String.valueOf(cboSfSearchDose1.getSelectedItem())) : 0;

        MyDateTime mdt = calSfSearch1.getCalendar() == null ? null : new MyDateTime(calSfSearch1.getCalendar());

        //Retrive dose
        if (cboSfSearchVac1.getSelectedIndex() > 0) {
            int i = 1;
            cboSfSearchDose1.removeAllItems();
            cboSfSearchDose1.addItem("All Dose");
            while (i <= vac.getDoseCount()) {
                cboSfSearchDose1.addItem(String.valueOf(i));
                i++;
            }
            cboSfSearchDose1.setEnabled(true);
        } else {
            cboSfSearchDose1.setEnabled(false);
            cboSfSearchDose1.removeAllItems();
            cboSfSearchDose1.addItem("All Dose");
        }

        SfSearch1(search, vac, dose, mdt);
    }//GEN-LAST:event_btnSfSearch1ActionPerformed

    private void cboSfSearchDose1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSfSearchDose1ActionPerformed
        String search = (!txtSfSearch1.getText().isBlank() ? txtSfSearch1.getText() : "").trim().toLowerCase();
        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = cboSfSearchVac1.getSelectedIndex() > 0 ? (Vaccine) vacArray[cboSfSearchVac1.getSelectedIndex() - 1] : null;

        int dose = cboSfSearchDose1.getSelectedIndex() > 0 ? Integer.parseInt(String.valueOf(cboSfSearchDose1.getSelectedItem())) : 0;

        MyDateTime mdt = calSfSearch1.getCalendar() == null ? null : new MyDateTime(calSfSearch1.getCalendar());


        SfSearch1(search, vac, dose, mdt);
    }//GEN-LAST:event_cboSfSearchDose1ActionPerformed

    private void cboSfSearchVac1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSfSearchVac1ActionPerformed
        String search = (!txtSfSearch1.getText().isBlank() ? txtSfSearch1.getText() : "").trim().toLowerCase();
        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = cboSfSearchVac1.getSelectedIndex() > 0 ? (Vaccine) vacArray[cboSfSearchVac1.getSelectedIndex() - 1] : null;

        int dose = cboSfSearchDose1.getSelectedIndex() > 0 ? Integer.parseInt(String.valueOf(cboSfSearchDose1.getSelectedItem())) : 0;

        MyDateTime mdt = calSfSearch1.getCalendar() == null ? null : new MyDateTime(calSfSearch1.getCalendar());

        //Retrive dose
        if (cboSfSearchVac1.getSelectedIndex() > 0) {
            int i = 1;
            cboSfSearchDose1.removeAllItems();
            cboSfSearchDose1.addItem("All Dose");
            while (i <= vac.getDoseCount()) {
                cboSfSearchDose1.addItem(String.valueOf(i));
                i++;
            }
            cboSfSearchDose1.setEnabled(true);
        } else {
            cboSfSearchDose1.setEnabled(false);
            cboSfSearchDose1.removeAllItems();
            cboSfSearchDose1.addItem("All Dose");
        }

        SfSearch(search, vac, dose, mdt);
    }//GEN-LAST:event_cboSfSearchVac1ActionPerformed

    private void calSfSearch1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_calSfSearch1MouseReleased
        String search = (!txtSfSearch1.getText().isBlank() ? txtSfSearch1.getText() : "").trim().toLowerCase();
        Object[] vacArray = (Object[]) htVac.values().toArray();
        Vaccine vac = cboSfSearchVac1.getSelectedIndex() > 0 ? (Vaccine) vacArray[cboSfSearchVac1.getSelectedIndex() - 1] : null;

        int dose = cboSfSearchDose1.getSelectedIndex() > 0 ? Integer.parseInt(String.valueOf(cboSfSearchDose1.getSelectedItem())) : 0;

        MyDateTime mdt = calSfSearch1.getCalendar() == null ? null : new MyDateTime(calSfSearch1.getCalendar());

        //Retrive dose
        if (cboSfSearchDose1.getSelectedIndex() > 0) {
            int i = 1;
            cboSfSearchDose1.removeAllItems();
            cboSfSearchDose1.addItem("All Dose");
            while (i <= vac.getDoseCount()) {
                cboSfSearchDose1.addItem(String.valueOf(i));
                i++;
            }
            cboSfSearchDose1.setEnabled(true);
        } else {
            cboSfSearchDose1.setEnabled(false);
            cboSfSearchDose1.removeAllItems();
            cboSfSearchDose1.addItem("All Dose");
        }

        SfSearch1(search, vac, dose, mdt);
    }//GEN-LAST:event_calSfSearch1MouseReleased

    private void txtSfSearch1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSfSearch1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSfSearch1ActionPerformed

    private void txtMsNewAsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMsNewAsKeyTyped
        // TODO add your handling code here:


    }//GEN-LAST:event_txtMsNewAsKeyTyped

    private void txtMsNewAsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMsNewAsKeyReleased
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
            return;
        }

        int n = 0; //new
        int old = 0; //old

        try {
            n = Integer.parseInt(txtMsNewAs.getText());
            old = Integer.parseInt(txtMsCurrentAs.getText());
        } catch (Exception ex) {
            n = 0;
            old = 0;
        }

        int result = n - old; //new - old
        int max = sldrMsAs.getMaximum();

        if (result > 0) {

            if (result > max) {
                sldrMsAs.setMaximum(result);
            } else {
                sldrMsAs.setValue(result);
            }

            cboMsAs.setSelectedIndex(0);

        } else {

            if (max < old) {
                sldrMsAs.setMaximum(old);
                sldrMsAs.setValue(Math.abs(result));

            } else {
                sldrMsAs.setValue(Math.abs(result));
            }

            cboMsAs.setSelectedIndex(1);

        }

    }//GEN-LAST:event_txtMsNewAsKeyReleased

    private void txtMsNewPsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMsNewPsKeyReleased
        if (!Character.isDigit(evt.getKeyChar())) {
            evt.consume();
            return;
        }

        int n = 0; //new
        int old = 0; //old

        try {
            n = Integer.parseInt(txtMsNewPs.getText());
            old = Integer.parseInt(txtMsCurrentPs.getText());
        } catch (Exception ex) {
            n = 0;
            old = 0;
        }

        int result = n - old; //new - old
        int max = sldrMsPs.getMaximum();

        if (result > 0) {

            if (result > max) {
                sldrMsPs.setMaximum(result);
            } else {
                sldrMsPs.setValue(result);
            }

            cboMsPs.setSelectedIndex(0);

        } else {

            if (max < old) {
                sldrMsPs.setMaximum(old);
                sldrMsPs.setValue(Math.abs(result));

            } else {
                sldrMsPs.setValue(Math.abs(result));
            }

            cboMsPs.setSelectedIndex(1);

        }
    }//GEN-LAST:event_txtMsNewPsKeyReleased

    private void txtMsNewPsKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMsNewPsKeyTyped

    }//GEN-LAST:event_txtMsNewPsKeyTyped

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
            java.util.logging.Logger.getLogger(StockistLoadingPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StockistLoadingPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StockistLoadingPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StockistLoadingPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StockistLoadingPage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdPInfoEdit;
    private javax.swing.JButton btnAdPSave;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnSfSearch;
    private javax.swing.JButton btnSfSearch1;
    private javax.swing.JButton btnTaMark1;
    private com.toedter.calendar.JDateChooser calAdPHiredDate;
    private com.toedter.calendar.JDateChooser calSfSearch;
    private com.toedter.calendar.JDateChooser calSfSearch1;
    private javax.swing.JComboBox<String> cboAdPGender;
    private javax.swing.JComboBox<String> cboMsAs;
    private javax.swing.JComboBox<String> cboMsCentre;
    private javax.swing.JComboBox<String> cboMsDose;
    private javax.swing.JComboBox<String> cboMsPs;
    private javax.swing.JComboBox<String> cboMsSearchDose;
    private javax.swing.JComboBox<String> cboMsSearchVac;
    private javax.swing.JComboBox<String> cboMsVac;
    private javax.swing.JComboBox<String> cboSfSearchDose;
    private javax.swing.JComboBox<String> cboSfSearchDose1;
    private javax.swing.JComboBox<String> cboSfSearchVac;
    private javax.swing.JComboBox<String> cboSfSearchVac1;
    private com.toedter.calendar.JDateChooser jDob;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel151;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JLabel lblPwNoMatch;
    private javax.swing.JLabel lblVSName6;
    private javax.swing.JLabel lblVSName7;
    private javax.swing.JLabel lblWbUsername;
    private javax.swing.JPanel pnlCredential;
    private javax.swing.JSlider sldrMsAs;
    private javax.swing.JSlider sldrMsPs;
    private javax.swing.JTable tblMs;
    private javax.swing.JTable tblSf;
    private javax.swing.JTable tblSf1;
    private javax.swing.JTextField txtAdPFullName;
    private javax.swing.JTextField txtAdPNo;
    private javax.swing.JTextField txtAdPRole;
    private javax.swing.JTextField txtAdPUser;
    private javax.swing.JTextField txtAdPVacCentre;
    private javax.swing.JTextField txtMsCurrentAs;
    private javax.swing.JTextField txtMsCurrentPs;
    private javax.swing.JTextField txtMsNewAs;
    private javax.swing.JTextField txtMsNewPs;
    private javax.swing.JTextField txtMsRemarks;
    private javax.swing.JPasswordField txtPCfmPw;
    private javax.swing.JTextField txtPEmail;
    private javax.swing.JPasswordField txtPNewPw;
    private javax.swing.JTextField txtSfSearch;
    private javax.swing.JTextField txtSfSearch1;
    // End of variables declaration//GEN-END:variables
}
