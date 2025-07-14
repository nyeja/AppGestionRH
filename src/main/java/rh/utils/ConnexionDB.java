package rh.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnexionDB {
    private static Connection conn;

    public static Connection getConnection() {
        if (conn == null) {
            try {
                Class.forName("oracle.jdbc.OracleDriver");
                conn = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:XE", "walker", "walker");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}
