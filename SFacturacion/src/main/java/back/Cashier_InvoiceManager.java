package back;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import sqlite.DB_Manager;

/**
 *
 * @author Bryan Vicente <>
 */
public class Cashier_InvoiceManager {

    private static final float actualIva = (float) 0.12;

    public String getDataClient(String cedula) {

        return "";
    }

    //--------------------manage the stock and calculate the invoice-----------------//
    //check if there are enought stock to make the sale
    public static boolean checkStock(int id, int amount) {

        try {
            int stock = Manage_Products.getStock(id);
            return amount <= stock;

        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

    }

//calculate the invoice and update the stock
    public static void calculateTotalInTable(DefaultTableModel modelTable) {
        //Calculate total, subtotal and iva and put the information into ultimates tables's rows
        float total = 0, subtotal = 0, iva = 0;
        int rowCount = modelTable.getRowCount();

        //check if there are products added
        if (!modelTable.getValueAt(0, 0).toString().equals("")) {
            for (int i = 0; i < rowCount; i++) {
                //get all TotalPrice from table's model
                String column3Data = modelTable.getValueAt(i, 3).toString().toLowerCase();
                //check if the row is the calculated subtotal, iva or total 
                if (column3Data.equals("subtotal") || column3Data.equals("iva") || column3Data.equals("total")) {
                    break;
                } else {
                    float suma = Float.parseFloat(modelTable.getValueAt(i, 4).toString());
                    subtotal += suma;
                }

            }
        }

        subtotal = (float) (Math.round(subtotal * 100d) / 100d);
        iva = subtotal * actualIva;
        iva = (float) (Math.round(iva * 100d) / 100d);
        total = subtotal + iva;
        total = (float) (Math.round(total * 100d) / 100d);

        //colocate in their respective row
        modelTable.setValueAt("Total", rowCount - 1, 3);
        modelTable.setValueAt(String.valueOf(total), rowCount - 1, 4);

        modelTable.setValueAt("IVA", rowCount - 2, 3);
        modelTable.setValueAt(String.valueOf(iva), rowCount - 2, 4);

        modelTable.setValueAt("Subtotal", rowCount - 3, 3);
        modelTable.setValueAt(String.valueOf(subtotal), rowCount - 3, 4);
    }

    //calculate the money to return
    public static float calculateChange(float taken, float total, JTextField txt_MoneyToReturn) {
        float moneyToReturn = -1;
        try {
            moneyToReturn = taken - total;
            moneyToReturn = (float) (Math.round(moneyToReturn * 100d) / 100d);
            txt_MoneyToReturn.setText(String.valueOf(moneyToReturn));
            if (moneyToReturn < 0) {
                txt_MoneyToReturn.setForeground(Color.red);
            } else {
                txt_MoneyToReturn.setForeground(Color.BLACK);
            }

        } catch (Exception e) {
        }
        return moneyToReturn;
    }

    //save the invoice data
    public static void saveInvocie(String cedulaClient, DefaultTableModel modelTable) throws SQLException {

        int newNInvoice = getLastNumInvoice() +1;
        System.out.println("last Num invoice" + getLastNumInvoice());
        System.out.println("new Num invoice" + newNInvoice);
        //get the current date yy-mm-dd
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);

        /*order in the table
            subtotal [-3,4]
            iva [-2,4]
            total [-1,4]
         */
        int nRows = modelTable.getRowCount();

        float subtotal = Float.parseFloat(modelTable.getValueAt(nRows-3, 4).toString());
        float iva = Float.parseFloat(modelTable.getValueAt(nRows-2, 4).toString());
        float total = Float.parseFloat(modelTable.getValueAt(nRows-1, 4).toString());

        try (Connection conn = sqlite.DB_Manager.connectDB()) {
            //columns = NInvoice, date , cedulaClient , subtotal , iva , total 
            String query = "INSERT INTO invoices (nInvoice, date, cedulaClient,Subtotal, iva , Total) "
                    + "VALUES (\"" +newNInvoice + "\",\"" + date + "\",\"" + cedulaClient + "\", \"" + subtotal + "\","
                    + "\"" + iva + "\",\"" + total + "\");";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.executeUpdate();

            //save each product selled to the table "sales"
            //columns = NInvoice , IdProduct, Amount, TotalPrice
          
            for (int i = 0; i < modelTable.getRowCount() - 3; i++) {
                String id = modelTable.getValueAt(i, 0).toString();
                String name = modelTable.getValueAt(i, 1).toString();
                String amount = modelTable.getValueAt(i, 3).toString();
                String totalPrice = modelTable.getValueAt(i, 4).toString();
                
                query = "INSERT INTO sales VALUES (" + newNInvoice + "," + id + ", \""+name +"\"," +amount + ","
                        + "" + totalPrice + ");";
                stmt = conn.prepareStatement(query);
                stmt.executeUpdate();
            }

            conn.close();

        }

    }

    public static int getLastNumInvoice() {
        try (final Connection conn = DB_Manager.connectDB()) {
            int nInvoice = 0;
            //load product's stock
            String query = "SELECT max(nInvoice) FROM Invoices;";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            nInvoice = rs.getInt("max(nInvoice)");
           
            conn.close();
            return nInvoice;
    
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
    }

    public static void printInvoice(JTextArea txt_Area, DefaultTableModel modelTable) {
        //nombre p/u cantidad  total
       String text = "";
        String header = String.format("%-32s  %-5s  %-4s  %6s\n\n\n" ,"Producto", "p/u","cant" ,"total");
        
        text += header;
   
       
        for (int i = 0; i < modelTable.getRowCount()-3; i++) {
            String product = modelTable.getValueAt(i,1).toString();
            String pU = modelTable.getValueAt(i,2).toString();
            String amount = modelTable.getValueAt(i,3).toString();
            String total = modelTable.getValueAt(i,4).toString();
            
            String item = String.format("%-32s  %-5s  %-4s  %6s\n", product , pU,amount,total);
            text += item;
        }
        
        text += "\n\n";
        //is to print subtotal , iva and total
        for (int i = modelTable.getRowCount()-1; i >= modelTable.getRowCount()-3; i--) {
            String name = modelTable.getValueAt(i, 3).toString();
            String value = modelTable.getValueAt(i, 4).toString();
            String item = String.format("%-30s  %21s\n", name,value);
            
            text += item;
        }
        txt_Area.setText(text);
    }
}
