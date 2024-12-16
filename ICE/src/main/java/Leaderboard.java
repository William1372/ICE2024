import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Leaderboard {
    private TextUI ui;

    public Leaderboard(){

    }

    public void viewLeaderboard() {

        String bold = "\033[1m";
        String url = "jdbc:sqlite:identifier.sqlite";
        String user = "root";
        String password = "";

        String query = """
        SELECT 
            u.username, 
            (r.distance / 1000) AS distance_km, 
            ((r.hours * 60) + r.minutes + (r.seconds / 60)) AS samlet_tid_i_minutter, 
            (((r.hours * 60) + r.minutes + (r.seconds / 60)) / (r.distance / 1000)) AS pace_min_per_km 
        FROM running_log r
        JOIN users u ON r.user_id = u.id
        ORDER BY pace_min_per_km ASC;
    """;

        System.out.println("\n");
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("---------------------------------------------------------------");
            System.out.printf("%-5s  %-15s  %-18s  %-15s%n", bold + "Place", "Name", "Distance (km.)", "Pace (min. pr. km.)");
            System.out.println("---------------------------------------------------------------");

            int rank = 1;
            while (rs.next()) {
                String username = rs.getString("username").trim();
                float distanceKm = rs.getFloat("distance_km");
                double paceMinPerKm = rs.getDouble("pace_min_per_km");

                String place = String.valueOf(rank);
                if (rank == 1) {
                    place = "🥇";
                } else if (rank == 2) {
                    place = "🥈";
                } else if (rank == 3) {
                    place = "🥉";
                }

                System.out.printf("%-5s  %-15s  %-18.2f  %-15.2f%n", place, username, distanceKm, paceMinPerKm);
                rank++;
            }
            System.out.println("---------------------------------------------------------------");
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
