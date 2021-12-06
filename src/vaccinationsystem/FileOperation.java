package vaccinationsystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
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

    private String Id;
    private String FileName; //for plain text file
    private ArrayList<Object> arrayList = new ArrayList<Object>(); //For serialized file
    private Hashtable<String, Object> ht = new Hashtable<String, Object>();
    private Object result;

    public FileOperation() {
    }

    ;
    
    public FileOperation(String fileName) {
        this.FileName = fileName;
    }

    //Read based on ID (int)
    public FileOperation(int id, String fileName) {
        this.Id = String.valueOf(id);
        this.FileName = fileName;
    }

    //Read based on Serialized ID (String)
    public FileOperation(String id, String fileName) {
        this.Id = id;
        this.FileName = fileName;
    }

    public void ReadFile() {
        Object result = null;

        this.arrayList = FileOperation.DeserializeObject(FileName);
        this.ht = FileOperation.ConvertToHashTable(arrayList);

        if (ht.containsKey(Id)) {
            this.result = ht.get(Id);
        }
    }

    public Object getReadResult() {
        return result;
    }

    //Save as plaintext
    public boolean SaveToFile(Object obj) {
        boolean success = false;
        this.result = obj;

        try {
            FileWriter fw = new FileWriter(this.FileName, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println(obj.toString());

            pw.close();
            bw.close();
            fw.close();
            success = true;

        } catch (IOException ex) {
            success = false;
        }

        return success;
    }

    //Modify existing data
    public boolean ModifyRecord(Object newRecord) {

        boolean success = false;
        this.result = newRecord;

        arrayList = DeserializeObject(this.FileName);
        ht = ConvertToHashTable(arrayList);

        if (ht.containsKey(this.Id)) {
            ht.replace(String.valueOf(this.Id), newRecord);
        } else {
            return success;
        }

        try {
            //existing file
            File file = new File(this.FileName);
            String tempFilename = "temp.ser";
            File tempFile = new File(tempFilename);

            if (tempFile.exists()) {
                tempFile.delete();
            }

            for (Object x : ht.values()) {

                if (!SerializeObject(tempFilename, x)) {
                    System.out.println("Update data serialization error!");
                    break;
                }

            }

            if (file.delete()) {
                tempFile.renameTo(file);
                success = true;
            } else {
                System.out.println("Fail to delete");
            }
        } catch (Exception ex) {
            System.out.println(ex);
            return success;
        }

        return success;

    }

    //Mass modify record
    public boolean ModifyRecords() {

        boolean success = false;

        try {
            //existing file
            File file = new File(this.FileName);
            String tempFilename = "temp.ser";
            File tempFile = new File(tempFilename);

            if (tempFile.exists()) {
                tempFile.delete();
            }

            for (Object x : ht.values()) {
                if (!SerializeObject(tempFilename, x)) {
                    System.out.println("Update data serialization error!");
                    break;
                }
            }

            if (file.delete()) {
                tempFile.renameTo(file);
                success = true;
            } else {
                System.out.println("Fail to delete");
            }
        } catch (Exception ex) {
            System.out.println(ex);
            return success;
        }

        return success;
    }

    //Static methods
    public static ArrayList<Object> DeserializeObject(String filename) {
        ArrayList<Object> arrayList = new ArrayList<Object>();

        try {

            //boolean s = true;
            int s = 0;
            File fileName = new File(filename);
            FileInputStream file = new FileInputStream(fileName);

            ObjectInputStream in = new ObjectInputStream(file);

            while (s <= file.available()) {
                Object temp = new Object();
                temp = in.readObject();

                if (temp != null) {
                    arrayList.add(temp);
                }
                s++;
            }

            in.close();
            file.close();

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return arrayList;
    }

    public static boolean SerializeObject(String filename, Object obj) {

        boolean success = false;
        ArrayList<Object> al = new ArrayList<Object>();
        al = FileOperation.DeserializeObject(filename);

        try {

            File fileName = new File(filename);
            FileOutputStream file = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(file);

            ListIterator li = al.listIterator();

            while (li.hasNext()) {
                Object element = li.next();
                if (element != null) {
                    out.writeObject(element);
                }
            }

            out.writeObject(obj);
            out.close();
            file.close();
            System.out.println(obj);

            success = true;
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return success;
    }

    public static Hashtable<String, Object> ConvertToHashTable(ArrayList<Object> al) {

        //Get first element as key for hash table
        ListIterator li = al.listIterator();
        Hashtable<String, Object> hashtable = new Hashtable<String, Object>();

        try {
            while (li.hasNext()) {
                Object obj = li.next();

                String firstIndexAsKey = String.valueOf(obj).trim().split("\t")[0];

                hashtable.put(firstIndexAsKey, obj);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return hashtable;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String FileName) {
        this.FileName = FileName;
    }

    public ArrayList<Object> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<Object> arrayList) {
        this.arrayList = arrayList;
    }

    public Hashtable<String, Object> getHt() {
        return ht;
    }

    public void setHt(Hashtable<String, Object> ht) {
        this.ht = ht;
    }

}

class GenerateId {

    private ArrayList<Object> arrayList;
    private String filename;
    private String newId;
    private String prefix;

    //Plain text generate id
    public GenerateId(String filename, String prefix) {
        this.filename = filename;
        this.prefix = prefix;

        int count = 1;

        this.newId = prefix + String.format("%04d", count);

        while (AnyDuplicatedId()) {
            count++;
            this.newId = prefix + String.format("%04d", count);
        }
    }

    //Plain text generate id
    public GenerateId(String filename) {
        this.filename = filename;

        int count = 1;

        this.newId = String.valueOf(count);

        while (AnyDuplicatedId()) {
            count++;
            this.newId = String.valueOf(count);
        }
    }

    //Serialized generate id
    public GenerateId(ArrayList<Object> arrayList, String prefix) {
        this.arrayList = arrayList;
        this.prefix = prefix;

        int count = 1;

        this.newId = prefix + String.format("%04d", count);

        while (AnyDuplicatedSerializableId()) {
            count++;
            this.newId = prefix + String.format("%04d", count);
        }
    }

    public GenerateId(ArrayList<Object> arrayList) {
        this.arrayList = arrayList;

        int count = 1;

        this.newId = String.valueOf(count);

        while (AnyDuplicatedSerializableId()) {
            count++;
            this.newId = String.valueOf(count);
        }
    }

    //Static Methods
    private boolean AnyDuplicatedId() {
        //Check first data of each row

        boolean anyDup = false;

        try {
            File file = new File(filename);
            Scanner sc = new Scanner(file);

            while (sc.hasNextLine() && !anyDup) {
                String record = sc.nextLine();

                if (!(record.isBlank() || record.isEmpty())) {
                    String code = record.trim().split("\t")[0];

                    anyDup = code.equals(this.newId);
                }
            }

            sc.close();

        } catch (Exception ex) {

        }

        return anyDup;
    }

    private boolean AnyDuplicatedSerializableId() {
        //Check first data of each row

        boolean anyDup = false;

        try {

            ListIterator li = arrayList.listIterator();

            while (li.hasNext() && !anyDup) {
                Object element = li.next();

                if (element != null) {

                    String code = String.valueOf(element).trim().split("\t")[0];
                    anyDup = code.equals(this.newId);
                }
            }

        } catch (Exception ex) {

        }

        return anyDup;
    }

    public String returnId() {
        return this.newId;
    }
}
