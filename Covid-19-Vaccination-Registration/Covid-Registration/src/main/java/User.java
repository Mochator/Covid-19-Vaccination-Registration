/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mocha
 */

public abstract class User{
    private int Id;
    private String First_Name;
    private String Last_Name;
    private char Gender;
    private String Email;
    private String Password;
    private String UserRole;

    public User(String First_Name, String Last_Name, char Gender, String Email, String Password) {
        this.First_Name = First_Name;
        this.Last_Name = Last_Name;
        this.Gender = Gender;
        this.Email = Email;
        this.Password = Password;
       
    } 
    
    public abstract void setUserRole(String userRole);
}

