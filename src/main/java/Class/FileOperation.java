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
import java.util.ListIterator;
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
    };
    
    //Read based on ID (String)
    public FileOperation(String id, String fileName){
        
    }
    
    //Read based on ID (int)
    public FileOperation(int id, String fileName){
        
    }
    
    //Save as plaintext
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

    //Static Methods
    public static boolean AnyDuplicatedId(String filename, String reference) {
        //Check first data of each row

        boolean noDup = true;

        try {

            File file = new File(filename);
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine() && noDup) {

                String row = sc.nextLine();
                String id = row.split("\t")[0];

                if (id == reference) {
                    noDup = false;
                }
            }

        } catch (Exception ex) {

        }

        return !noDup;
    }

    public static boolean AnyDuplicatedSerializableId(ArrayList<Object> arrayList, String reference) {
        //Check first data of each row

        boolean anyDup = false;

        try {

            ListIterator li = arrayList.listIterator();

            while (li.hasNext() && !anyDup) {
                Object element = li.next();

                if (element != null) {

                    String code = String.valueOf(element).trim().split("\t")[0];
                    System.out.println("Check Code: " + code);
                    anyDup = code.equals(reference);
                    System.out.println(anyDup);
                }
            }

        } catch (Exception ex) {

        }

        return anyDup;
    }
    
    public static String GenerateNewId(ArrayList<Object> arrayList, String prefix){
        int count = 1;

        String newUsername = prefix + String.format("%04d", count);

        while (FileOperation.AnyDuplicatedSerializableId(arrayList, newUsername)) {
            count++;
            newUsername = prefix + String.format("%04d", count);
        }
        
        return newUsername;
    }

    public static ArrayList<Object> DeserializeObject(String filename) {
        ArrayList<Object> arrayList = new ArrayList<Object>();

        try {

            boolean s = true;

            File fileName = new File(filename);
            FileInputStream file = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(file);

            while (s) {

                Object temp = new Object();
                temp = in.readObject();

                if (temp != null) {
                    arrayList.add(temp);
                } else {
                    s = false;
                }

            }

            in.close();
            file.close();

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return arrayList;
    }

    public static void SerializeObject(String filename, Object obj) {

        ArrayList<Object> existingData = new ArrayList<Object>();
        existingData = DeserializeObject(filename);

        try {

            File fileName = new File(filename);
            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);

            ListIterator li = existingData.listIterator();

            while (li.hasNext()) {
                Object element = li.next();

                if (element != null) {
                    out.writeObject(element);
                }
            }

            //Duplicated ID Check
            if(!FileOperation.AnyDuplicatedSerializableId(existingData, filename)){
                out.writeObject(obj);
            } else {
                JOptionPane.showMessageDialog(null, "Please try again later", "Error!", JOptionPane.ERROR_MESSAGE);
            }
            

            out.close();
            file.close();

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
   
}

