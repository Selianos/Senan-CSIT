
package dao;


import java.sql.Connection ;
import java.sql.DriverManager ;
import java.sql.SQLException ; 

public class DatabaseConnection {
    
        private static final String DB_URL = "jdbc:mysql://localhost:3306/inventory_management";
        private static final String USER  = "root" ;
        private static final String PASS = "System" ;
        private static Connection connection ;
        
        private DatabaseConnection(){
            
        }
        
        public static Connection getConnection(){
            if(connection == null){
                try{
                    Class.forName("com.mysql.cj.jdbc.Driver") ;
                    connection = DriverManager.getConnection(DB_URL,USER,PASS) ;
                }catch(ClassNotFoundException | SQLException e){
                    e.printStackTrace();
                    throw new RuntimeException("Failed to connect to database", e);
                }
            }
            return connection ;
        }
        
        
        public static void closeConnection(){
            if(connection != null){
                try{
                    connection.close();
                    connection = null ; 
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        } 
}
