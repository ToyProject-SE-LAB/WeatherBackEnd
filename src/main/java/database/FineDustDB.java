package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FineDustDB {
	private static final String URL = "jdbc:mysql://localhost:3306/toyproject";
    private static final String USER = "root";
    private static final String PASSWORD = "3907";

    public static void saveDataToDatabase(String dataTime, String pm10Grade, String pm25Grade) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO finedust VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, dataTime);
                statement.setString(2, pm10Grade);
                statement.setString(3, pm25Grade);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	
	
}
