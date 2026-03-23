import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Dbconnection {

    public static Connection getConnection(){

        try{
            // Explicitly load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/crimson_oak";
            String user = "root";
            String password = "";

            Connection conn = DriverManager.getConnection(url,user,password);

            System.out.println("Database Connected");

            // Initialize tables table if empty
            initializeTables(conn);

            return conn;

        }catch(Exception e){

            System.out.println("Database Error");
            e.printStackTrace();
            return null;

        }

    }

    private static void initializeTables(Connection conn) {
        try {
            Statement stmt = conn.createStatement();
            
            // Check if tables table exists, if not create it
            try {
                stmt.execute("CREATE TABLE IF NOT EXISTS tables (table_no VARCHAR(5) PRIMARY KEY)");
            } catch(Exception e) {
                // Table might already exist
            }
            
            // Check if any records exist
            var result = stmt.executeQuery("SELECT COUNT(*) as cnt FROM tables");
            result.next();
            int count = result.getInt("cnt");
            
            // If empty, populate with T1-T14
            if(count == 0) {
                for(int i = 1; i <= 14; i++) {
                    stmt.execute("INSERT INTO tables (table_no) VALUES ('T" + i + "')");
                }
                System.out.println("Initialized tables T1-T14");
            }
        } catch(Exception e) {
            System.out.println("Error initializing tables: " + e.getMessage());
        }
    }
}