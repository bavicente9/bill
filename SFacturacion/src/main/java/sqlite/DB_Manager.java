package sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author BryanV
 */
public class DB_Manager {
    private static Connection conn;
    //return the object connection and create a connection to the database
     public static Connection connectDB() {
         conn = null;
        try {           
            String url = "jdbc:sqlite:src/main/java/sqlite/Data";
            conn = DriverManager.getConnection(url);     
            
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } 
        return conn;
        
   }
     
}
