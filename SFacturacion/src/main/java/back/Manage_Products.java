package back;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import sqlite.DB_Manager;

/**
 *
 * @author Bryan Vicente <>
 */
public class Manage_Products {

    //return a String][] with every Product
    public static String[][] getDataProducts(String name) throws SQLException {
        String[][] data = null;
        try (Connection conn = sqlite.DB_Manager.connectDB()) {

            String query = "";
            int nColumns = 0;
            ArrayList<String[]> dataAL = new ArrayList<>(nColumns);

            //load  data
            query = "SELECT idProduct ,name ,priceUnity ,stock FROM Products WHERE Name  LIKE \"%" + name + "%\";";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            nColumns = rsmd.getColumnCount();
            dataAL = new ArrayList<>(nColumns);
            while (rs.next()) {
                String[] person = {rs.getString("idProduct"), rs.getString("name"), String.valueOf(rs.getFloat("PriceUnity")) , rs.getString("stock")};
                dataAL.add(person);
            }
            data = transformDataToArray(dataAL, nColumns);

        } catch (Exception e) {
            System.out.println(e);
              JOptionPane.showMessageDialog(null,"Error","Alert",JOptionPane.WARNING_MESSAGE);     
        }

        return data;

    }

    //check if exist the product
    public static boolean existProduct(String name) {
        boolean exist = false;
        try {
            String query = "SELECT * FROM Products where name = \"" + name + "\";";
            Connection conn = sqlite.DB_Manager.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            exist = rs.next();
            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(Manage_Products.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    //Transform data from ArrayList to a String[][]
    private static String[][] transformDataToArray(ArrayList<String[]> dataAL, int nColumns) {
        String[][] data = new String[dataAL.size()][nColumns];
        //save data into a Array[][]
        for (int i = 0; i < dataAL.size(); i++) {
            for (int j = 0; j < data[0].length; j++) {
                data[i][j] = dataAL.get(i)[j];

            }

        }
        return data;
    }

    //add a new product
    public static boolean addNewProduct(String name, float priceU, int stock, int id) {
        boolean doneCorrectly = false;
        try {
            String query = "";
            //check if the product is in the table products
            if (!existProduct(name)) {

                //save data into Products table
                query = "INSERT INTO Products (idProduct, name, PriceUnity,stock) VALUES (" + id + ",\"" + name + "\"," + priceU  + "," + stock + ");";

                Connection conn = sqlite.DB_Manager.connectDB();
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.executeUpdate();
                conn.close();
            }
            doneCorrectly = true;
            System.out.println(name + " product Added ");

        } catch (SQLException ex) {
            Logger.getLogger(Manage_Products.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return doneCorrectly;
        }

    }

    //update a product, and return if that happend correctly
    public static boolean updateProduct(String idToEdit, String name, float priceU, int stock) {
        boolean doneCorrectly = false;
        try {
            String query = "";

            //update data into products table  
            query = "UPDATE  Products SET name= \"" + name + "\", priceUnity = \"" + priceU + "\",stock = \""+stock + " WHERE IdProduct = \"" + idToEdit + "\";";

            Connection conn = sqlite.DB_Manager.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.executeUpdate();

            conn.close();
            System.out.println(idToEdit + " product Updated");
            doneCorrectly = true;

        } catch (SQLException ex) {
            Logger.getLogger(Manage_Products.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return doneCorrectly;
        }

    }
    //update just the stock

    public static void updateStock(int id, int newStock) {
        try {
            String query = "";

            //update data into products table  
            query = "UPDATE  Products SET stock= \"" + newStock + "\" WHERE IdProduct = \"" + id + "\";";

            Connection conn = sqlite.DB_Manager.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.executeUpdate();

            conn.close();
            System.out.println(id + " product Updated");

        } catch (SQLException ex) {
            Logger.getLogger(Manage_Products.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String getUltimateId() {
        String id = "Error";
        try {
            Connection conn = sqlite.DB_Manager.connectDB();

            String query = "select MAX(IdProduct) FROM Products ;";
            PreparedStatement stm = conn.prepareStatement(query);
            ResultSet rs = stm.executeQuery();
            id = String.valueOf(rs.getInt("MAX(IdProduct)"));
            conn.close();
            return id;
        } catch (SQLException ex) {
            Logger.getLogger(Manage_Products.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return id;
        }
    }

    // get a product's stock
    public static int getStock(int id) {
        try (final Connection conn = DB_Manager.connectDB()) {
            int stock = 0;
            //load product's stock
            String query = "SELECT stock from Products WHERE idProduct = " + id + ";";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            stock = rs.getInt("stock");
            return stock;
        } catch (Exception e) {
            System.out.println(e);
            return -1;
        }
    }

}
