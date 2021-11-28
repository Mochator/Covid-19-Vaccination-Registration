package Class;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mocha
 */
public class FileOperation {

    private String FileName;

    public FileOperation() {
    }

    ;
    
    public void SaveToFile(Object obj, String fileName) {

        try {
            FileWriter fw = new FileWriter(fileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println(obj.toString());

            pw.close();

            JOptionPane.showMessageDialog(null, "Data has been saved!", "Successful", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Data wasn't saved!", "Failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    //TODO::Serialization will be used
    public void ModifyRecord(Object newRecord, Object referenceData, String filename) {

    }

    public static String GenerateRecordId(String filename, String prefix) {
        int lastid = 0;

        try {
            File file = new File(filename);
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                String row = sc.nextLine();
                
                String tempId = row.split("\t")[0];
                
                if(tempId.startsWith(prefix)){
                    int tempLastId = Integer.parseInt(tempId.replace(prefix, ""));
                    if(tempLastId > lastid) lastid = tempLastId;
                }                
            }
            
            lastid ++;
            String stringLastId = prefix + String.format("%04d", lastid);
            
            while(FileOperation.AnyDuplicatedId(filename, stringLastId)){
                lastid ++;
                stringLastId = prefix + String.format("%04d", lastid);
            }                     
            
        } catch (Exception ex) {
            System.out.println(ex);
        }   
        
        return prefix + String.format("%04d", lastid);
    }
    
    //Static Methods
    public static boolean AnyDuplicatedId(String filename, String reference){
        //Check first data of each row
        
        boolean noDup = true;
        
        try{
            
            File file = new File(filename);
            Scanner sc = new Scanner(file);
            
            
            while(sc.hasNextLine() && noDup){
                
                String row = sc.nextLine();
                String id = row.split("\t")[0];
                
                if(id == reference){
                    noDup = false;
                }
            }
            
        } catch (Exception ex){
            
        }
        
        return !noDup;
    }

//    public ArrayList<User> ReadAllFromFile(String fileName, String className) {
//        
//    }
}
