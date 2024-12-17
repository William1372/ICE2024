import java.sql.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class User {
    private int id;
    private String username;
    private String password;
    private int age;
    private String gender;
    private int height;
    private double weight;
    private TextUI ui = new TextUI();
    private ArrayList<Challenge> currentChallenges;
    private CPHFitness cphfitness = new CPHFitness();
    private Achievement achievement;

    public User(String username, String password, int age, String gender, int height, double weight) {
        this.username = username;
        this.password = password;
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.currentChallenges = new ArrayList<>();
    }

    public void updateProfile() {
        int choice = ui.promptNumeric("1) Edit weight⚖\uFE0F \n" +
                "2) Edit height\uD83D\uDCCF \n" +
                "3) Return to Main Menu\uD83D\uDD19");

        Connection connection = DatabaseHandler.getConnection();

        try {
            connection.setAutoCommit(false);

            switch (choice) {
                case 1:
                    updateWeight(connection);
                    break;
                case 2:
                    updateHeight(connection);
                    break;
                case 3:
                    cphfitness.mainMenu();
                    return;
                default:
                    ui.displayMsg("❌Invalid number. Please try again.");
                    updateProfile();
            }

            connection.commit();
            ui.displayMsg("\u001B[1mChanges have been successfully saved.");

        } catch (SQLException e) {
            try {
                connection.rollback();
                ui.displayMsg("\u001B[1mTransaction failed, changes rolled back.");
            } catch (SQLException ex) {
                ui.displayMsg("\u001B[1mError rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                ui.displayMsg("\u001B[1mError resetting autocommit: " + e.getMessage());
            }
        }
    }

    public void updateWeight(Connection conn) {
        String bold = "\u001B[1m";
        try {
            double newWeight = ui.promptNumeric(bold + "Type your updated weight in kg's: ");
            if (newWeight > 0) {
                ui.displayMsg(bold + "Weight has been updated from: " + weight + "kg., to: " + newWeight + "kg.");
                weight = newWeight;

                String updateSql = "UPDATE users SET weight = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setDouble(1, newWeight);
                    stmt.setInt(2, id);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        conn.commit();
                        ui.displayMsg(bold + "Your weight has been updated in the database.");
                    } else {
                        conn.rollback();
                        ui.displayMsg(bold + "Failed to update your weight in the database.");
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    ui.displayMsg(bold + "Database error: " + e.getMessage());
                }
            } else {
                ui.displayMsg(bold + "Invalid weight. Please type a positive number!");
                updateWeight(conn);
            }
        } catch (Exception e) {
            ui.displayMsg(bold + e);
        }
    }

    public void updateHeight(Connection conn) {
        String bold = "\u001B[1m";
        try {
            int newHeight = ui.promptNumeric(bold + "Type your updated height in cm's: ");
            if (newHeight > 0) {
                ui.displayMsg(bold + "Height has been updated from: " + height + "cm., to: " + newHeight + "cm.");
                height = newHeight;

                String updateSql = "UPDATE users SET height = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setInt(1, newHeight);
                    stmt.setInt(2, id);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        conn.commit();
                        ui.displayMsg(bold + "Your height has been updated in the database.");
                    } else {
                        conn.rollback();
                        ui.displayMsg(bold + "Failed to update your height in the database.");
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    ui.displayMsg(bold + "Database error: " + e.getMessage());
                }
            } else {
                ui.displayMsg(bold + "Invalid height. Please type a positive number!");
                updateHeight(conn);
            }
        } catch (Exception e) {
            ui.displayMsg(bold + e);
        }
    }

    public void viewRunningLog() {
        if (id <= 0) {
            ui.displayMsg("Error: User ID not set.");
            return;
        }

        String sql = "SELECT hours, minutes, seconds, distance, date FROM running_log WHERE user_id = ?";

        try (PreparedStatement stmt = DatabaseHandler.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);  // Brug brugerens ID for at hente løb kun for den bruger
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                ui.displayMsg("No runs found for this user.");
                return;
            }

            do {
                int hours = rs.getInt("hours");
                int minutes = rs.getInt("minutes");
                int seconds = rs.getInt("seconds");
                double distance = rs.getDouble("distance");
                String date = rs.getString("date");

                ui.displayMsg("Run: " + hours + "h " + minutes + "m " + seconds + "s, Distance: " + distance + " meters, Date: " + date);
            } while (rs.next());
        } catch (SQLException e) {
            ui.displayMsg("Error retrieving running log: " + e.getMessage());
        }
    }

    public List<Goal> getGoalsFromDatabase() {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT distance, minutes, progress FROM goals WHERE user_id = ?";

        try (PreparedStatement stmt = DatabaseHandler.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    float distance = rs.getFloat("distance");
                    int minutes = rs.getInt("minutes");
                    float progress = rs.getFloat("progress");
                    Goal goal;
                    if (minutes != 0) {
                        goal = new Goal(distance, minutes, progress);
                    }
                    else {
                        goal = new Goal(distance, progress);
                    }
                    goals.add(goal);
                }
            }
        } catch (SQLException e) {
            ui.displayMsg("Error retrieving goals: " + e.getMessage());
        }
        return goals;
    }

    public ArrayList<Challenge> getCurrentChallenges(){
        return currentChallenges;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}