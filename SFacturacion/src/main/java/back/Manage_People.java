
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
public class Manage_People {

    //return a String][] with every people
    public static String[][] getDataPeople(String nCedula) throws SQLException {
        String[][] data = null;
        try (Connection conn = sqlite.DB_Manager.connectDB()) {
            data = null;
            String query = "";
            int nColumns = 0;
            ArrayList<String[]> dataAL = new ArrayList<>(nColumns);

            //load people's data
            query = "SELECT * from People  where  Cedula LIKE  \"" + nCedula + "%\";";
            PreparedStatement stmt = conn.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            nColumns = rsmd.getColumnCount();
            dataAL = new ArrayList<>(nColumns);
            while (rs.next()) {
                String[] person = {rs.getString("cedula"), rs.getString("name"), rs.getString("surname1"), rs.getString("surname2"),
                    rs.getString("dir"), rs.getString("tlf"), rs.getString("email")};
                dataAL.add(person);
            }

            data = transformDataToArray(dataAL, nColumns);
        } catch (Exception e) {
            System.out.println(e);
        }
        return data;

    }

    //check if exist a person
    public static boolean existPerson(String cedula) {
        boolean exist = false;
        try {
            String query = "SELECT * from people" + " where cedula = \"" + cedula + "\";";

            Connection conn = sqlite.DB_Manager.connectDB();
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            exist = rs.next();
            conn.close();

        } catch (SQLException ex) {
            Logger.getLogger(Manage_People.class.getName()).log(Level.SEVERE, null, ex);
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

    public static boolean addNewPerson(String cedula, String name, String surname1, String surname2, String dir, String tlf, String email, String turn) {
        boolean doneCorrectly = false;
        try {
            String query = "";
            //check if the person is in table people
            if (!existPerson(cedula)) {

                //save data into People table
                query = "INSERT INTO People VALUES (\"" + cedula + "\", \"" + name + "\","
                        + "\"" + surname1 + "\",\"" + surname2 + "\",\"" + dir + "\",\"" + tlf + "\",\"" + email + "\");";

                Connection conn = sqlite.DB_Manager.connectDB();
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.executeUpdate();
                conn.close();
            }

            System.out.println(cedula + " Person Added ");
            doneCorrectly = true;

        } catch (SQLException ex) {
            Logger.getLogger(Manage_People.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return doneCorrectly;
        }

    }

    //Delete a person
    public static void deletePerson(String cedula) {
        try {
            //check if the person  exist and delete  from People Table

            if (existPerson(cedula)) {

                String query = "DELETE FROM  people WHERE cedula =\"" + cedula + "\";";
                Connection conn = sqlite.DB_Manager.connectDB();
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.executeUpdate();
                conn.close();

            }
        } catch (SQLException ex) {
            Logger.getLogger(Manage_People.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //update a person, and return if that happend correctly
    public static boolean updatePerson(String cedulaToEdit, String cedula, String name, String surname1, String surname2, String dir, String tlf, String email, String turn) {
        boolean doneCorrectly = false;
        try {
            //if cedula is changed, check if the new cedula not exist with another person
            if (cedulaToEdit.equals(cedula) || (existPerson(cedulaToEdit) && !existPerson(cedula))) {

                String query = "";

                //update data in People table  
                query = "UPDATE  people SET cedula = \"" + cedula + "\", name = \"" + name + "\", surname1 = \"" + surname1 + "\", surname2 = \"" + surname2 + "\", "
                        + "dir = \"" + dir + "\", tlf = \"" + tlf + "\", email = \"" + email + "\" "
                        + "WHERE cedula = \"" + cedulaToEdit + "\";";

                Connection conn = sqlite.DB_Manager.connectDB();
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.executeUpdate();

                conn.close();
                System.out.println(cedula + " Person Updated ");
                doneCorrectly = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Manage_People.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return doneCorrectly;
        }

    }

}
