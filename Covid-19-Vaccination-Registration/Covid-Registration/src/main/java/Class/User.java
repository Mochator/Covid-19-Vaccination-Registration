/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Mocha
 */
public abstract class User {

    private String First_Name;
    private String Last_Name;
    private char Gender;
    protected MyDateTime Dob;
    private String Email;
    private String Password;

    protected String Username;

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

    public abstract void GenerateUsername();
}

class People extends User {

    protected Address Address;
    protected MyDateTime RegistrationDate;
    private VaccinationStatus VacStatus;
    private static String UserRole = "People";

    //Constructor for adding new user
    public People(Address Address, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(First_Name, Last_Name, Gender, Dob, Email, Password);
        this.Address = Address;
        this.VacStatus = VacStatus;
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

    public void GenerateUsername() {
        this.Username = FileOperation.GenerateRecordId(General.userFileName, General.PrefixPeople);
    }  
}

enum VaccinationStatus {
    Not,
    Partially,
    Fully
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

}

class NonCitizen extends People{
    private String Passport;
    protected MyDateTime PassportExpiry;
    private static boolean IsCitizen = false;

    public NonCitizen(String Passport, MyDateTime PassportExpiry, Address Address, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(Address, VacStatus, First_Name, Last_Name, Gender, Dob, Email, Password);
        this.Passport = Passport;
        this.PassportExpiry = PassportExpiry;
    }

    public NonCitizen(String Passport, MyDateTime PassportExpiry, Address Address, MyDateTime RegistrationDate, VaccinationStatus VacStatus, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
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
    
}

class Personnel extends User{
    private static String UserRole = "Personnel";
    protected MyDateTime HiredDate;
    private PersonnelStatus Status;

    //Create
    public Personnel(PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(First_Name, Last_Name, Gender, Dob, Email, Password);
        this.Status = Status;
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

    public void GenerateUsername() {
        this.Username = FileOperation.GenerateRecordId(General.userFileName, General.PrefixPersonnel);
    }   
       
}

enum PersonnelStatus{
    Active,
    Suspend
}


class Doctor extends Personnel{
    private static String PersonnelRole = "Doctor";
    protected VaccineCentre VacCentre;

    public Doctor(VaccineCentre VacCentre, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(Status, First_Name, Last_Name, Gender, Dob, Email, Password);
        this.VacCentre = VacCentre;
    }

    public Doctor(VaccineCentre VacCentre, MyDateTime HiredDate, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        super(HiredDate, Status, First_Name, Last_Name, Gender, Dob, Email, Password, Username);
        this.VacCentre = VacCentre;
    }

    public static String getPersonnelRole() {
        return PersonnelRole;
    }
    
}

class Admin extends Personnel{
    private static String PersonnelRole = "Admin";

    public Admin(VaccineCentre VacCentre, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(Status, First_Name, Last_Name, Gender, Dob, Email, Password);
    }

    public Admin(MyDateTime HiredDate, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        super(HiredDate, Status, First_Name, Last_Name, Gender, Dob, Email, Password, Username);
    }

    public static String getPersonnelRole() {
        return PersonnelRole;
    }
    
}

class Stockist extends Personnel{
    private static String PersonnelRole = "Stockist";
    protected VaccineCentre VacCentre;

    public Stockist(VaccineCentre VacCentre, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password) {
        super(Status, First_Name, Last_Name, Gender, Dob, Email, Password);
        this.VacCentre = VacCentre;
    }

    public Stockist(VaccineCentre VacCentre, MyDateTime HiredDate, PersonnelStatus Status, String First_Name, String Last_Name, char Gender, MyDateTime Dob, String Email, String Password, String Username) {
        super(HiredDate, Status, First_Name, Last_Name, Gender, Dob, Email, Password, Username);
        this.VacCentre = VacCentre;
    }

    public static String getPersonnelRole() {
        return PersonnelRole;
    }
    
}