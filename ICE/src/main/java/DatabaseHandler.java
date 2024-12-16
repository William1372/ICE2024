import java.sql.*;

public class DatabaseHandler {
    public static final String DB_URL = "jdbc:sqlite:identifier.sqlite";
    private static Connection connection;
    private static TextUI ui = new TextUI();

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);

            if (connection != null) {
                ui.displayMsg("Connected to the database.");
            }

            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            ui.displayMsg("Failed to connect to database: " + e.getMessage());
            return null;
        }
    }

    public static void createRunningLogTable(Connection conn) {
        String sql = """
    CREATE TABLE IF NOT EXISTS running_log (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        user_id INTEGER,
        hours INTEGER,
        minutes INTEGER,
        seconds INTEGER,
        distance REAL,
        date TEXT,
        FOREIGN KEY (user_id) REFERENCES users(id)
    );
    """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            ui.displayMsg("Running log table created or already exists.");
        } catch (SQLException e) {
            ui.displayMsg("Error creating running log table: " + e.getMessage());
        }
    }

    public static void createGoalTable(Connection conn) {
        String sql = """
            CREATE TABLE IF NOT EXISTS goals (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            distance FLOAT,
            minutes INTEGER,
            progress FLOAT,
            FOREIGN KEY (user_id) REFERENCES users(id)
            );
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            ui.displayMsg("Goals table created or already exists.");
        } catch (SQLException e) {
            ui.displayMsg("Error creating table: " + e.getMessage());
        }
    }

    public static void createChallengeTable (Connection conn){
        String sql = """
            CREATE TABLE IF NOT EXISTS challenges (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            distance INTEGER,
            hours INTEGER,
            minutes INTEGER,
            FOREIGN KEY (user_id) REFERENCES users(id)
            );
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            ui.displayMsg("Challenges table created or already exists.\n");
        } catch (SQLException e) {
            ui.displayMsg("Error creating table: " + e.getMessage());
        }
    }

    public static void createUserTable(Connection conn) {
        String sql = """
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            age INTEGER,
            gender TEXT,
            height INTEGER,
            weight REAL
        );
        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            ui.displayMsg("User table created or already exists.");
        } catch (SQLException e) {
            ui.displayMsg("Error creating table: " + e.getMessage());
        }
    }

    public static void saveUser(User user) {
        String checkSql = "SELECT id FROM users WHERE username = ?";
        String insertSql = """
    INSERT INTO users (username, password, age, gender, height, weight)
    VALUES (?, ?, ?, ?, ?, ?);
    """;

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {

            checkStmt.setString(1, user.getUsername());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("Username " + user.getUsername() + " already exists.");
                return;
            }

            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                insertStmt.setString(1, user.getUsername());
                insertStmt.setString(2, user.getPassword());
                insertStmt.setInt(3, user.getAge());
                insertStmt.setString(4, user.getGender());
                insertStmt.setInt(5, user.getHeight());
                insertStmt.setDouble(6, user.getWeight());

                insertStmt.executeUpdate();
                ui.displayMsg("User saved successfully.");
            }
        } catch (SQLException e) {
            ui.displayMsg("Error saving user: " + e.getMessage());
        }
    }

    public static void saveRun(User user, Run run) {
        String sql = """
    INSERT INTO running_log (user_id, hours, minutes, seconds, distance, date)
    VALUES (?, ?, ?, ?, ?, ?);
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getId());
            stmt.setInt(2, run.getHours());
            stmt.setInt(3, run.getMinutes());
            stmt.setInt(4, run.getSeconds());
            stmt.setDouble(5, run.getDistance());
            stmt.setString(6, run.getDate());

            stmt.executeUpdate();
            ui.displayMsg("Run saved successfully.");
        } catch (SQLException e) {
            ui.displayMsg("Error saving run: " + e.getMessage());
        }
    }

    public static void saveGoal (User user, Goal goal){
        String sql = """
                INSERT INTO goals (user_id, distance, minutes, progress)
                VALUES (?, ?, ?, ?);
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, user.getId());
            stmt.setFloat(2, goal.getDistance());
            stmt.setInt(3, goal.getMinutes());
            stmt.setFloat(4, goal.getProgress());
            stmt.executeUpdate();
            ui.displayMsg("Goal saves successfully.");
        } catch (SQLException e) {
            ui.displayMsg("Error saving goal: " + e.getMessage());
        }
    }

    /*
    public static void saveAchievement (User user, Achievement achievement){
        String sql = """
                INSERT INTO achievements (user_id, distance)
                VALUES (?, ?);
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, user.getId());
            stmt.setFloat(2, achievement.);
            stmt.executeUpdate();
        } catch (SQLException e) {
            ui.displayMsg("Error saving achievement: " + e.getMessage());
        }

    }
     */

    public static void updateDistanceGoals (User user, float progress){
        String sql = """
                UPDATE goals
                SET progress = progress + ?
                WHERE user_id = ? AND minutes = 0;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setFloat(1, progress);
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
            ui.displayMsg("Goal updated succesfully.");
        } catch (SQLException e) {
            ui.displayMsg("Error updating goal: " + e.getMessage());
        }
    }

    public static void updateTimeGoals (User user, int progress){
        String sql = """
                UPDATE goals
                SET progress = progress + ?
                WHERE user_id = ? AND distance = 0;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setFloat(1, progress);
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
            ui.displayMsg("Goal updated succesfully.");
        } catch (SQLException e) {
            ui.displayMsg("Error updating goal: " + e.getMessage());
        }
    }

    public static void updateDualGoals (User user, int time, float distance){
        String sql = """
                UPDATE goals
                SET progress = ?
                WHERE user_id = ? AND (progress > ? OR progress = 0) AND NOT (distance > ? OR distance = 0) AND NOT minutes = 0;
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setFloat(1, time);
            stmt.setInt(2, user.getId());
            stmt.setFloat(3, time);
            stmt.setFloat(4, distance);
            stmt.executeUpdate();
            ui.displayMsg("Goal updated succesfully.");
        } catch (SQLException e) {
            ui.displayMsg("Error updating goal: " + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}