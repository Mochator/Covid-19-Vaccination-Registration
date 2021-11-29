/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

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
    
    protected String Username;

    public User() {
    };

    public User(String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        this.First_Name = First_Name;
        this.Last_Name = Last_Name;
        this.Gender = Gender;
        this.Dob = Dob;
        this.Email = Email;
        this.Password = Password;
    }

    public User(String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        this.First_Name = First_Name;
        this.Last_Name = Last_Name;
        this.Gender = Gender;
        this.Dob = Dob;
        this.Email = Email;
        this.Password = Password;
        this.Username = Username;
    }

    public boolean LoginVerification(String password) {
        return password.equals(this.Password);
    }

    protected abstract String GenerateUsername();

    @Override
    public String toString() {
        return Username + "\t" + First_Name + "\t" + Last_Name + "\t" + Gender + "\t" + Dob + "\t" + Email + "\t" + Password;
    }
    
    
}

class People extends User {

    private static String UserRole = General.UserRolePeople;
    protected Address Address;
    protected MyDateTime RegistrationDate;
    private VaccinationStatus VacStatus;

    //Constructor for adding new user
    public People(Address Address, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(First_Name, Last_Name, Gender, Dob, Email, Password);
        this.Address = Address;
        this.VacStatus = VacStatus;
        super.Username = this.GenerateUsername();
    }

    //Constructor for all reading user
    public People(Address Address, MyDateTime RegistrationDate, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        super(First_Name, Last_Name, Gender, Dob, Email, Password, Username);
        this.Address = Address;
        this.RegistrationDate = RegistrationDate;
        this.VacStatus = VacStatus;
    }

    public String getVacStatus() {
        return VacStatus.name();
    }

    public void setVacStatus(VaccinationStatus VacStatus) {
        this.VacStatus = VacStatus;
    }

    public static String getUserRole() {
        return UserRole;
    }

    protected String GenerateUsername() {
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.userFileName);
        
        return FileOperation.GenerateNewId(allObj, General.PrefixPeople);

    }

    @Override
    public String toString() {
        return "\t" + super.toString() + "\t" + UserRole + "\t" + Address + "\t" + RegistrationDate  + "\t" + VacStatus;
    }

    enum VaccinationStatus {
        Not,
        Partially,
        Fully
    }

}

class Citizen extends People {

    private String IcNo;
    private static boolean IsCitizen = true;

    //Create new citizen
    public Citizen(String IcNo, Address Address, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(Address, VacStatus, First_Name, Last_Name, Gender, Dob, Email, Password);
        this.IcNo = IcNo;
    }

    public Citizen(String IcNo, Address Address, MyDateTime RegistrationDate, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        super(Address, RegistrationDate, VacStatus, First_Name, Last_Name, Gender, Dob, Email, Password, Username);
        this.IcNo = IcNo;
    }

    public String getIcNo() {
        return IcNo;
    }

    public void setIcNo(String IcNo) {
        this.IcNo = IcNo;
    }

    @Override
    public String toString() {
        return "\t" + super.toString() + "\t" + IsCitizen + "\t" + IcNo;
    }

    
}

class NonCitizen extends People {

    private String Passport;
    protected MyDateTime PassportExpiry;
    private static boolean IsCitizen = false;

    public NonCitizen(String Passport, MyDateTime PassportExpiry, Address Address, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(Address, VacStatus, First_Name, Last_Name, Gender, Dob, Email, Password);
        this.Passport = Passport;
        this.PassportExpiry = PassportExpiry;
    }

    public NonCitizen(String Passport, MyDateTime PassportExpiry, Address Address, MyDateTime RegistrationDate, VaccinationStatus VacStatus, boolean IsCitizen, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        super(Address, RegistrationDate, VacStatus, First_Name, Last_Name, Gender, Dob, Email, Password, Username);
        this.Passport = Passport;
        this.PassportExpiry = PassportExpiry;
    }

    public String getPassport() {
        return Passport;
    }

    public void setPassport(String Passport) {
        this.Passport = Passport;
    }

    public MyDateTime getPassportExpiry() {
        return PassportExpiry;
    }

    public void setPassportExpiry(MyDateTime PassportExpiry) {
        this.PassportExpiry = PassportExpiry;
    }

    @Override
    public String toString() {
        return "\t" + super.toString() + "\t" + IsCitizen + "\t" + Passport + "\t" + PassportExpiry;
    }
    
    

}

class Personnel extends User {

    private static String UserRole = General.UserRolePersonnel;
    protected MyDateTime HiredDate;
    private PersonnelStatus Status;

    //Create
    public Personnel(PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(First_Name, Last_Name, Gender, Dob, Email, Password);
        this.Status = Status;
        super.Username = this.GenerateUsername();
    }

    //Read
    public Personnel(MyDateTime HiredDate, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        super(First_Name, Last_Name, Gender, Dob, Email, Password, Username);
        this.HiredDate = HiredDate;
        this.Status = Status;
    }

    public PersonnelStatus getStatus() {
        return Status;
    }

    public void setStatus(PersonnelStatus Status) {
        this.Status = Status;
    }

    public static String getUserRole() {
        return UserRole;
    }

    public String GenerateUsername() {
        ArrayList<Object> allObj = FileOperation.DeserializeObject(General.userFileName);
        
        return FileOperation.GenerateNewId(allObj, General.PrefixPersonnel);
    }

    @Override
    public String toString() {
        return "\t" + super.toString() + "\t" + UserRole + "\t" + HiredDate + "\t" + Status;
    }

    enum PersonnelStatus {
        Active,
        Suspend
    }

}

class Doctor extends Personnel {

    private static String PersonnelRole = General.PersonnelRoleDoctor;
    protected VaccineCentre VacCentre;

    public Doctor(VaccineCentre VacCentre, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(Status, First_Name, Last_Name, Gender, Dob, Email, Password);
        this.VacCentre = VacCentre;
    }

    public Doctor(VaccineCentre VacCentre, MyDateTime HiredDate, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        super(HiredDate, Status, First_Name, Last_Name, Gender, Dob, Email, Password, Username);
        this.VacCentre = VacCentre;
    }

    @Override
    public String toString() {
        return "\t" + super.toString() + "\t" + PersonnelRole + "\t" + VacCentre.getVacCode();
    }
    
}

class Admin extends Personnel {

    private static String PersonnelRole = General.PersonnelRoleAdmin;

    public Admin(PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(Status, First_Name, Last_Name, Gender, Dob, Email, Password);

    }

    public Admin(MyDateTime HiredDate, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        super(HiredDate, Status, First_Name, Last_Name, Gender, Dob, Email, Password, Username);

    }

    @Override
    public String toString() {
        return "\t" + super.toString() + "\t" + this.PersonnelRole;
    }

    
}

class Stockist extends Personnel {

    private static String PersonnelRole = General.PersonnelRoleStockist;
    protected VaccineCentre VacCentre;

    public Stockist(VaccineCentre VacCentre, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(Status, First_Name, Last_Name, Gender, Dob, Email, Password);
        this.VacCentre = VacCentre;
    }

    public Stockist(VaccineCentre VacCentre, MyDateTime HiredDate, PersonnelStatus Status,  String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        super(HiredDate, Status, First_Name, Last_Name, Gender, Dob, Email, Password, Username);
        this.VacCentre = VacCentre;
    }

    @Override
    public String toString() {
        return "\t" + super.toString() + "\t" + PersonnelRole + "\t" + VacCentre.getVacCode();
    }

    
}
