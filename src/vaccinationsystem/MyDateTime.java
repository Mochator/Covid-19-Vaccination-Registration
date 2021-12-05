/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vaccinationsystem;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Mocha
 */
public class MyDateTime implements Serializable {

   private Calendar cal = Calendar.getInstance();

    public MyDateTime() {

    }
    
    public MyDateTime(Calendar cal){
        this.cal = cal;
    }

    public MyDateTime(int Year, int Month, int Day) {
        cal.set(Year, Month, Day);
    }

    public MyDateTime(int Year, int Month, int Day, int Hour, int Minute, int Second) {
        cal.set(Year, Month, Day, Hour, Minute, Second);
    }


    public String GetShortDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(cal.getTime());
    }
    
    public String GetShortDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return sdf.format(cal.getTime());
    }
    
    public String GetLongDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        return sdf.format(cal.getTime());
    }
    
    public String GetLongDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
        return sdf.format(cal.getTime());
    }

    @Override
    public String toString() {
        return cal.getTime().toString();
    }

    public Calendar getCal() {
        return cal;
    }
    
    public Date getDate(){
        Date date = new Date(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE));
        return date;
    }
    
    

}
