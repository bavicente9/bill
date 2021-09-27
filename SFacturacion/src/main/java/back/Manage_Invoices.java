package back;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bryan Vicente <>
 */
public class Manage_Invoices {

    //return a String][] with every Invoice
    public static String[][] getDataInvoices(String nInvoice) throws SQLException {
        String[][] data = null;
        try (Connection conn = sqlite.DB_Manager.connectDB()) {

            String query = "";
            int nColumns = 0;
            ArrayList<String[]> dataAL = new ArrayList<>(nColumns);

            //load  data
            query = "SELECT * FROM Invoices WHERE Ninvoice  LIKE \"" + nInvoice + "%\";";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            nColumns = rsmd.getColumnCount();
            dataAL = new ArrayList<>(nColumns);
            //to transfor the date to a string

            while (rs.next()) {
                String[] item = {String.valueOf(rs.getInt("Ninvoice")), rs.getString("Date"), rs.getString("CedulaClient"),
                    String.valueOf(rs.getFloat("Subtotal")), String.valueOf(rs.getFloat("Iva")), String.valueOf(rs.getFloat("Total"))};
                dataAL.add(item);
            }
            data = transformDataToArray(dataAL, nColumns);

        } catch (Exception e) {
            System.out.println(e);
        }

        return data;

    }

    //check if exist the invoice
    public static boolean existInvoice(int nInvoice) {
        boolean exist = false;
        try {
            String query = "SELECT * FROM Invoices where nInvoice = \"" + nInvoice + "\";";
            Connection conn = sqlite.DB_Manager.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            exist = rs.next();
            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(Manage_Invoices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exist;
    }

    //return a String with every Product with a especific Id from the Sales table and the final cost of invoice (subtotal, iva ,total)
    public static String getInvoiceDetails(int nInvoice) throws SQLException {

        String text = "";
        try (Connection conn = sqlite.DB_Manager.connectDB()) {
            String query = "";

            String header = String.format("%-20s %6s  %10s\n\n", "Producto", "cant", "total");
            text += header;
            //load  product's data
            query = "SELECT s.name,s.Amount , s.TotalPrice \n"
                    + "FROM Sales as s\n"
                    + "WHERE s.NInvoice =" + nInvoice + ";";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                //save product's data in the text
                text += String.format("%-20s %6s  %10s$\n", rs.getString("name"), rs.getString("amount"), rs.getString("totalPrice"));

            }
            text += "\n\n";
            //load general invoice's data
            query = "SELECT i.subtotal , i.iva, i.total  , i.CedulaClient, i.Date\n"
                    + "FROM  Invoices as i\n"
                    + "WHERE i.NInvoice = " + nInvoice + ";";
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            text += String.format("%-23s%16s$\n",
                    "Subtotal:", rs.getString("subtotal"));

            text += String.format("%-23s%16s$\n",
                    "Iva: ", rs.getString("iva"));

            text += String.format("%-23s%16s$\n\n\n",
                    "Total: ", rs.getString("total"));

            text += String.format("%-23s%16s\n",
                    "C.Cliente: ", rs.getString("cedulaClient"));

            text += String.format("%-23s%16s\n",
                    "Fecha: ", rs.getString("Date"));

            conn.close();
        } catch (Exception e) {
            System.out.println(e);
            text = "error";
        }
        return text;

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

}
