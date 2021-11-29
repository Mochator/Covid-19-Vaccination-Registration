/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ListIterator;

/**
 *
 * @author Mocha
 */
public class Main {

    public static void main(String[] args) {

        Address add = new Address("123", "Street", "City", "11600", "penang");
        MyDateTime mdt = new MyDateTime(2000, 10, 26);
        MyDateTime passportExp = new MyDateTime(2022, 12, 12);
        User citizen = new NonCitizen("123123", passportExp, add, People.VaccinationStatus.Not, "James", "Bond", 'M', mdt, "123@gmail.com", "123123");
        citizen.GenerateUsername();
        System.out.println(citizen);
        System.out.println("----------");

        User admin = new Admin(Personnel.PersonnelStatus.Active, "Carmen", "Lim", 'F', mdt, "clyy26@gmail.com", "123123");
        admin.GenerateUsername();
        System.out.println(admin);

        FileOperation.SerializeObject(General.userFileName, citizen);
        FileOperation.SerializeObject(General.userFileName, admin);

        ArrayList<Object> users = FileOperation.DeserializeObject(General.userFileName);
        ListIterator li = users.listIterator();

        while (li.hasNext()) {
            Object ob = li.next();
            User user = (User) ob;
            System.out.println(ob.getClass());
            System.out.println(user.Username);

            if (user.getClass() == Admin.class) {
                Personnel obj = (Admin) ob;
                System.out.println(obj.getStatus());
            } else if (user.getClass() == Doctor.class){
                Doctor obj = (Doctor) ob;
                System.out.println(obj.VacCentre);
            } else if(user.getClass() == NonCitizen.class){
                NonCitizen obj = (NonCitizen) ob;
                System.out.println(obj.Address.getFullAddress());
            }
        }

    }
}
