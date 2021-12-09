import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBDao {
    private static String dbUrl = System.getenv("JDBC_DATABASE_URL");
    private static String DB_DRIVER = "org.postgresql.Driver";
    private static Connection conn = null;

    public static Connection getConnection(){

        try {
            Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(dbUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
    public static void closeConnection(Connection conn){

        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
