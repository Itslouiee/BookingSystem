import java.sql.Connection;
import java.sql.DriverManager;

public class Dbconnection {

    public static Connection getConnection(){

        try{

            String url = "jdbc:mysql://localhost:3306/crimson_oak";
            String user = "root";
            String password = "";

            Connection conn = DriverManager.getConnection(url,user,password);

            System.out.println("Database Connected");

            return conn;

        }catch(Exception e){

            System.out.println("Database Error");
            e.printStackTrace();
            return null;

        }

    }
}