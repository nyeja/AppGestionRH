package rh.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionDB {
    private static Connection conn;

    public static Connection getConnection() {
        try {
            if (conn == null  || conn.isClosed()) {

                Class.forName("oracle.jdbc.OracleDriver");
                conn = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:XE", "walker", "walker");
                System.out.println("✅ Connexion à la base Oracle établie avec succès !");
            }
        }catch (ClassNotFoundException e) {
            System.err.println("❌ Driver Oracle introuvable !");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Échec de la connexion à Oracle !");
            e.printStackTrace();
        }
        return conn;
    }
}
