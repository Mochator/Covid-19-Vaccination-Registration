
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
    
    
    public FileOperation() {};
    
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
    
    public void ReadFromFileForId(String filename){
        this.FileName = filename;
        
        
    }
}
