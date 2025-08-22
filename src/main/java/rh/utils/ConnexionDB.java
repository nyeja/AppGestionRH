package rh.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionDB {
    private static Connection conn;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed() || !conn.isValid(2)) {
                Class.forName("oracle.jdbc.OracleDriver");
                conn = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:XE", "Yola", "Yolabd");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }
}
