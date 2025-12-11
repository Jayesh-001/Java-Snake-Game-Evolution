package com.snakegame;
import java.sql.*;
import java.util.ArrayList;

public class GameDatabase {

    private static final String URL = "jdbc:mysql://localhost:3306/snake_game"; 
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Initialize: Connects and creates the table if it's missing
    public static void initialize() {
        String sql = "CREATE TABLE IF NOT EXISTS player_scores (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," + 
                "name VARCHAR(50) NOT NULL," +         
                "score INT NOT NULL)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("SUCCESS: Connected to MySQL database.");
            
        } catch (SQLException e) {
            System.out.println("ERROR: Could not connect to MySQL.");
            e.printStackTrace();
        }
    }

    // Save a player's score
    public static void saveScore(String name, int score) {
        String sql = "INSERT INTO player_scores(name, score) VALUES(?, ?)";
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, score);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get the Top 5 High Scores
    public static ArrayList<String> getTopScores() {
        ArrayList<String> scores = new ArrayList<>();
        String sql = "SELECT name, score FROM player_scores ORDER BY score DESC LIMIT 5";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int rank = 1;
            while (rs.next()) {
                scores.add(rank + ". " + rs.getString("name") + " - " + rs.getInt("score"));
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }
}