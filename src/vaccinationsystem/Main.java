/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 *
 * @author Mocha
 */
public class Main {

    public static void main(String[] args) throws IOException {
        

//        Vaccine data = new Vaccine("test", 2, 15);
//        MyDateTime newVacDate = new MyDateTime();
//            newVacDate.getCal().add(Calendar.DATE, data.getInterval());
//            System.out.println(newVacDate);
//        Vaccine vaccine = new Vaccine("Pfizer", 2, 21);
//        FileOperation fo = new FileOperation();
//        fo.SaveToFile(vaccine, General.vaccineFileName);
//
//        Address add = new Address("123", "Street abc", "City Potato", "11600", "Penang");
//        VaccineCentre vc = new VaccineCentre("Bukit Jalil Covid Centre", add);
//        fo.SaveToFile(vc, General.vaccineCentreFileName);
//
//        for (int i = 1; i <= vaccine.getDoseCount(); i++) {
//            Stock stock = new Stock(vaccine, i, vc);
//            fo.SaveToFile(stock, General.stockFileName);
//        }
//        try{
//            File file = new File(General.stockFileName);
//            Scanner sc = new Scanner(file);
//            
//            while(sc.hasNextLine()){
//                String row = sc.nextLine();
//                String[] data = row.trim().split("\t");
//                
//                
//                
//            }
//            
//        } catch (Exception ex){
//            
//        }
//          ArrayList<Object> arrayList = new ArrayList<Object>();
        MyDateTime mdt = new MyDateTime(2000, 10, 26);
        MyDateTime passportExp = new MyDateTime(2022, 12, 12);
//        User citizen = new NonCitizen("123123", add, VaccinationStatus.Not, "James", "Bond", 'M', mdt, "123@gmail.com", "123123", "012312");
//        citizen.GenerateUsername();
//        System.out.println(citizen);
//        System.out.println("----------");
//
        User admin = new Admin(PersonnelStatus.Active, "Carmen", "Lim", 'F', mdt, "clyy26@gmail.com", "123123", "012312");
        admin.GenerateUsername();
//        System.out.println(admin);
//
//        FileOperation.SerializeObject(General.userFileName, citizen);
        FileOperation.SerializeObject(General.userFileName, admin);
//        
//
//        ArrayList<Object> users = FileOperation.DeserializeObject(General.userFileName);
//        ListIterator li = users.listIterator();
//
//        while (li.hasNext()) {
//            Object ob = li.next();
//            User user = (User) ob;
//            System.out.println(ob.getClass());
//            System.out.println(user.Username);
//
//            if (user.getClass() == Admin.class) {
//                Personnel obj = (Admin) ob;
//                System.out.println(obj.getStatus());
//            } else if (user.getClass() == Doctor.class){
//                Doctor obj = (Doctor) ob;
//                System.out.println(obj.VacCentre);
//            } else if(user.getClass() == NonCitizen.class){
//                NonCitizen obj = (NonCitizen) ob;
//                System.out.println(obj.Address.getFullAddress());
//            }
//        }
        ArrayList<Object> users = FileOperation.DeserializeObject(General.userFileName);
        Hashtable<String, Object> ht = FileOperation.ConvertToHashTable(users);

        ListIterator li = users.listIterator();

        while (li.hasNext()) {
            Object ob = li.next();
            User user = (User) ob;

            if (user.getClass() == Admin.class) {
                Personnel obj = (Admin) ob;
            } else if (user.getClass() == Doctor.class) {
                Doctor obj = (Doctor) ob;
            } else if (user.getClass() == NonCitizen.class) {
                NonCitizen obj = (NonCitizen) ob;
            }
        }

    }
}
