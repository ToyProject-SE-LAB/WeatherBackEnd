package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WeatherDB {
	private static final String URL = "jdbc:mysql://localhost:3306/toyproject";
    private static final String USER = "root";
    private static final String PASSWORD = "3907";

    public static void saveDataToDatabase(String dataTime, String sky, String tmp, String pop, String wsd, String reh) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String sql = "INSERT INTO weather VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, dataTime);
                statement.setString(2, sky);
                statement.setString(3, tmp);
                statement.setString(4, pop);
                statement.setString(5, wsd);
                statement.setString(6, reh);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	
	
}
