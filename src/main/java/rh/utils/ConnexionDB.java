package rh.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionDB {
//     Class.forName("oracle.jdbc.driver.OracleDriver");
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USER = "rh";
    private static final String PASSWORD = "rh123";
    private static Connection conn = null;

    // Ouvre la connexion s'il n'y en a pas encore ou si elle est fermée
    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'obtention de la connexion : " + e.getMessage());
        }
        return conn;
    }

    // Ferme la connexion si elle est ouverte
    public static void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la fermeture de la connexion : " + e.getMessage());
        }
    }
}
