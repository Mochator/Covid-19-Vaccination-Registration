/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

/**
 *
 * @author Mocha
 */
public abstract class User implements Serializable {
    private String First_Name;
    private String Last_Name;
    private char Gender;
    protected MyDateTime Dob;
    private String Email;
    private String Password;
    private String Contact;
    protected String Username;
    private String UserRole;
    public User() {
    };

    public User(String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Contact) {
        this.First_Name = First_Name;
        this.Last_Name = Last_Name;
        this.Gender = Gender;
        this.Dob = Dob;
        this.Email = Email;
        this.Password = Password;
        this.Contact = Contact;
    }

    public boolean LoginVerification(char[] password) {
        return this.Password.equals(String.valueOf(password));
    }
    
    protected void setUserRole(String Role){
        this.UserRole = Role;
    }
    
    protected String getUserRole(){
        return UserRole;
    }

    public String getFullName() {
        return this.First_Name + " " + this.Last_Name;
    }

    public String getFirst_Name() {
        return First_Name;
    }

    public String getLast_Name() {
        return Last_Name;
    }

    public char getGender() {
        return Gender;
    }

    public String getEmail() {
        return Email;
    }

    public String getPassword() {
        return Password;
    }

    public void setFirst_Name(String First_Name) {
        this.First_Name = First_Name;
    }

    public void setLast_Name(String Last_Name) {
        this.Last_Name = Last_Name;
    }

    public void setGender(char Gender) {
        this.Gender = Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender.charAt(0);
    }

    public void setDob(MyDateTime Dob) {
        this.Dob = Dob;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String Contact) {
        this.Contact = Contact;
    }

    protected abstract String GenerateUsername();

    @Override
    public String toString() {
        return Username + "\t" + First_Name + "\t" + Last_Name + "\t" + Gender + "\t" + Dob + "\t" + Email + "\t" + Password + "\t" + Contact + "\t" + UserRole;
    }

}

class People extends User {

    private final String UserRole = General.UserRolePeople;
    protected Address Address;
    protected static MyDateTime RegistrationDate = new MyDateTime();
    private VaccinationStatus VacStatus;
    private boolean isCitizen;

    //Constructor for adding new user
    public People(Address Address, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Contact) {
        super(First_Name, Last_Name, Gender, Dob, Email, Password, Contact);
        this.Address = Address;
        this.VacStatus = VacStatus;
        super.Username = this.GenerateUsername();
        super.setUserRole(UserRole);
    }

    public VaccinationStatus getVacStatus() {
        return VacStatus;
    }

    public void setVacStatus(VaccinationStatus VacStatus) {
        this.VacStatus = VacStatus;
    }
    
    protected void setIsCitizen(boolean isCitizen){
        this.isCitizen = isCitizen;
    }
    
    protected boolean getIsCitizen(){
        return this.isCitizen;
    }

    protected String GenerateUsername() {
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.userFileName);

        GenerateId genId = new GenerateId(allObj, General.PrefixPeople);

        return genId.returnId();

    }

    public void setAddress(String no, String street, String city, String postcode, String state) {
        this.Address = new Address(no, street, city, postcode, state);
    }
    
    public Address getAddress(){
        return this.Address;
    }

    @Override
    public String toString() {
        return super.toString() + "\t" + Address + "\t" + RegistrationDate + "\t" + VacStatus;
    }

}

enum VaccinationStatus {
    Not,
    Partially,
    Fully
}

class Citizen extends People {

    private String IcNo;
    private final boolean IsCitizen = true;

    //Create new citizen
    public Citizen(String IcNo, Address Address, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Contact) {
        super(Address, VacStatus, First_Name, Last_Name, Gender, Dob, Email, Password, Contact);
        this.IcNo = IcNo;
        this.setIsCitizen(IsCitizen);
    }


    public String getIcNo() {
        return IcNo;
    }

    public void setIcNo(String IcNo) {
        this.IcNo = IcNo;
    }

    @Override
    public String toString() {
        return super.toString() + "\t" + this.IcNo;
    }

}

class NonCitizen extends People {

    private String Passport;
    private final boolean IsCitizen = false;

    public NonCitizen(String Passport, Address Address, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Contact) {
        super(Address, VacStatus, First_Name, Last_Name, Gender, Dob, Email, Password, Contact);
        this.Passport = Passport;
        super.setIsCitizen(IsCitizen);
    }

    public String getPassport() {
        return Passport;
    }

    public void setPassport(String Passport) {
        this.Passport = Passport;
    }

    @Override
    public String toString() {
        return super.toString() + "\t" + Passport;
    }

}

class Personnel extends User {

    private final String UserRole = General.UserRolePersonnel;
    protected static MyDateTime HiredDate = new MyDateTime();
    private PersonnelStatus Status;
    private String PersonnelRole;

    //Create
    public Personnel(PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Contact) {
        super(First_Name, Last_Name, Gender, Dob, Email, Password, Contact);
        this.Status = Status;
        super.Username = this.GenerateUsername();
        super.setUserRole(this.UserRole);
    }
    public PersonnelStatus getStatus() {
        return Status;
    }

    public void setStatus(PersonnelStatus Status) {
        this.Status = Status;
    }

    public String getPersonnelRole() {
        return PersonnelRole;
    }

    protected void setPersonnelRole(String PersonnelRole) {
        this.PersonnelRole = PersonnelRole;
    }
    
    

    public String GenerateUsername() {
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.userFileName);

        GenerateId genId = new GenerateId(allObj, General.PrefixPersonnel);

        return genId.returnId();
    }

    public MyDateTime getHiredDate() {
        return HiredDate;
    }

    @Override
    public String toString() {
        return super.toString() + "\t" + HiredDate + "\t" + Status + "\t" + PersonnelRole;
    }

}

enum PersonnelStatus {
    Active,
    Suspend
}

class Doctor extends Personnel {

    private final String PersonnelRole = General.PersonnelRoleDoctor;
    protected VaccineCentre VacCentre;

    public Doctor(VaccineCentre VacCentre, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Contact) {
        super(Status, First_Name, Last_Name, Gender, Dob, Email, Password, Contact);
        this.VacCentre = VacCentre;
        super.setPersonnelRole(PersonnelRole);
    }


    public VaccineCentre getVacCentre() {
        return VacCentre;
    }

    public void setVacCentre(VaccineCentre VacCentre) {
        this.VacCentre = VacCentre;
    }

    @Override
    public String toString() {
        return super.toString() + "\t" + VacCentre;
    }

}

class Admin extends Personnel {

    private final String PersonnelRole = General.PersonnelRoleAdmin;

    public Admin(PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Contact) {
        super(Status, First_Name, Last_Name, Gender, Dob, Email, Password, Contact);
        super.setPersonnelRole(PersonnelRole);
    }

    @Override
    public String toString() {
        return super.toString();
    }

}

class Stockist extends Personnel {

    private final String PersonnelRole = General.PersonnelRoleStockist;
    protected VaccineCentre VacCentre;

    public Stockist(VaccineCentre VacCentre, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Contact) {
        super(Status, First_Name, Last_Name, Gender, Dob, Email, Password, Contact);
        this.VacCentre = VacCentre;
        super.setPersonnelRole(PersonnelRole);
    }

    public VaccineCentre getVacCentre() {
        return VacCentre;
    }

    public void setVacCentre(VaccineCentre VacCentre) {
        this.VacCentre = VacCentre;
    }

    @Override
    public String toString() {
        return super.toString() +  "\t" + VacCentre;
    }

}
