/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Class;

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

    public MyDateTime(int Year, int Month, int Day) {
        cal.set(Year, Month, Day);
    }

    public MyDateTime(int Year, int Month, int Day, int Hour, int Minute, int Second) {
        cal.set(Year, Month, Day, Hour, Minute, Second);
    }

    public void setDate(int Year, int Month, int Day) {
        cal.set(Calendar.YEAR, Year);
        cal.set(Calendar.MONTH, Month);
        cal.set(Calendar.DATE, Day);
    }

    public void setTime(int Hour, int Minute, int Second) {
        cal.set(Calendar.HOUR, Hour);
        cal.set(Calendar.MINUTE, Minute);
        cal.set(Calendar.SECOND, Second);
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
    
    

}
