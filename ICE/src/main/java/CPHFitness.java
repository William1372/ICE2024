import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class CPHFitness {
    private static Connection connection;
    private User currentUser;
    private Leaderboard leaderboard;
    private TextUI ui;
    private ChallengesList challengesList;
    private Achievement achievement;

    public CPHFitness(){
        connection = DatabaseHandler.connect();
        this.leaderboard = new Leaderboard();
        this.ui = new TextUI();
        this.challengesList = new ChallengesList();
        this.achievement = new Achievement();
    }

    public void startMenu(){
        String bold = "\u001B[1m";
        int choice = ui.promptNumeric("\uD83C\uDFC3\u200D♀\uFE0F\u200D➡\uFE0F\uD83C\uDFC3\u200D♂\uFE0F\u200D➡\uFE0F \033[4mWelcome to CPH Fitness\033[0m \uD83D\uDCAA \n" +
                "1) Log in\n"+
                "2) Sign up\n"+
                "3) Exit app");
        switch(choice){
            case 1:
                userLogin();
                mainMenu();
                break;
            case 2:
                currentUser = registerUser();
                mainMenu();
                break;
            case 3:
                exitProgram();
                break;
            default:
                startMenu();
        }
    }

    public void mainMenu() {
        int choice = ui.promptNumeric("You have the following options: \n " +
                "1) Add a run ➕\uD83C\uDFC3\u200D♀\uFE0F\u200D➡\uFE0F\uD83C\uDFC3\u200D➡\uFE0F \n " +
                "2) Add a goal ➕\uD83E\uDD45\uD83D\uDCCC \n " +
                "3) View previous runs \uD83D\uDDFA\uFE0F \n " +
                "4) View your current training plan or choose a new one \uD83D\uDCAA\uD83D\uDC40 \n " +
                "5) View your current challenge or choose a new one \uD83D\uDCC6\uD83D\uDC40 \n " +
                "6) View your goals \uD83E\uDD45\uD83D\uDC40 \n " +
                "7) Edit your goals \uD83E\uDD45 \n " +
                "8) View leaderboard \uD83D\uDCCA⚖\uFE0F\uD83D\uDC40 \n " +
                "9) View your achievements \uD83C\uDFC6\uD83C\uDF96\uFE0F \n " +
                "10) Edit profile \uD83E\uDDEC \n " +
                "11) Log out ❌\n " +
                "12) Exit program ❌");

        switch (choice) {
            case 1:
                addRun();
                mainMenu();
                break;
            case 2:
                createGoal();
                mainMenu();
                break;
            case 3:
                ui.displayMsg("\uD83D\uDCCBList of previous runs: ");
                currentUser.viewRunningLog();
                mainMenu();
                break;
            case 4:
                ui.displayMsg("❌The 'trainingplan' feature is currently unavailable!");
                mainMenu();
            case 5:
                if(!currentUser.getCurrentChallenges().isEmpty()) {
                    ui.displayMsg("\uD83C\uDFAFCurrent challenges: ");
                    ChallengesList.viewAllChallenges();
                    mainMenu();
                } else {
                    ui.displayMsg("❌You currently have no active challenges.");
                    mainMenu();
                    break;
                }
            case 6:
                if (currentUser.getGoalsFromDatabase().isEmpty()){
                    ui.displayMsg("❌You have no current goals");
                    mainMenu();
                } else {
                    ui.displayMsg("\uD83E\uDD45Current goals: ");
                    for (Goal goal : currentUser.getGoalsFromDatabase())
                        System.out.println(goal);
                }
                mainMenu();
            case 7:
                ui.displayMsg("❌The 'edit goals' feature is currently unavailable!");
                mainMenu();
                break;
            case 8:
                leaderboard.viewLeaderboard();
                mainMenu();
                break;
            case 9:
                ui.displayMsg("❌The 'achievements' feature is currently unavailable!");
                mainMenu();
                break;
            case 10:
                currentUser.updateProfile();
                mainMenu();
                break;
            case 11:
                logOut();
            case 12:
                ui.displayMsg("Exiting the program. Goodbye!\uD83D\uDC4B");
                exitProgram();
            default:
                ui.displayMsg("❌Invalid choice. Please try again.\uD83E\uDD14");
                mainMenu();
        }
    }

    public void createGoal() {
        int choice = ui.promptNumeric("You have the following options: \n " +
                "1) Add untimed distance-based goal (e.g 5.000 meters)\uD83D\uDDFA\uFE0F\n " +
                "2) Add timed distance-based goal (e.g 10.000 meters under 60 minutes)⏳\uD83D\uDDFA\uFE0F\n " +
                "3) Add timed non-distance based goal (e.g 30 minutes)⏳\n " +
                "4) Return to Main Menu⬅\uFE0F");

        switch(choice){
            case 1:
                float goal1 = ui.promptNumeric("Enter distance in meters:");
                currentUser.addGoal(new Goal(goal1, 0));
                ui.displayMsg("You just added : " + goal1 + " meters to your goals. Good luck!\uD83D\uDE04");
                Goal goalObj1 = new Goal(goal1, 0);
                DatabaseHandler.saveGoal(currentUser, goalObj1);
                mainMenu();
                break;
            case 2:
                float goal2Meters = ui.promptNumeric("Enter distance in meters:");
                int goal2Time = ui.promptNumeric("Enter time in minutes: ");
                currentUser.addGoal(new Goal(goal2Meters, goal2Time, 0));
                ui.displayMsg("You just added: " + goal2Meters + " meters in " + goal2Time + " minutes to your goals. Good luck!");
                Goal goalObj2 = new Goal(goal2Meters, goal2Time, 0);
                DatabaseHandler.saveGoal(currentUser, goalObj2);
                break;
            case 3:
                int goal3 = ui.promptNumeric("Enter time in minutes: ");
                currentUser.addGoal(new Goal(goal3, 0));
                ui.displayMsg("You just added : " + goal3 + " min to your goals. Good luck!\uD83D\uDE04");
                Goal goalObj3 = new Goal(goal3, 0);
                DatabaseHandler.saveGoal(currentUser, goalObj3);
                break;
            case 4:
                mainMenu();
            default:
                ui.displayMsg("❌Invalid number. Please try again.");
                createGoal();
        }
    }

    public void userLogin() {
        String bold = "\u001B[1m";
        String inputUsername = ui.promptText(bold + "\uD83D\uDC68\u200D\uD83D\uDCBB Enter your username: ");
        String inputPassword = ui.promptText(bold + "\uD83E\uDEE3 Enter your password: ");

        String sql = "SELECT * FROM users WHERE username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, inputUsername);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (inputPassword.equals(storedPassword)) {
                    ui.displayMsg(bold + "Login succeeded! ✅");
                    ui.displayMsg(bold + "Welcome, " + inputUsername + "! \uD83D\uDC4B");

                    String gender = rs.getString("gender");
                    int age = rs.getInt("age");
                    int height = rs.getInt("height");
                    double weight = rs.getDouble("weight");
                    int userId = rs.getInt("id");

                    currentUser = new User(inputUsername, storedPassword, age, gender, height, weight);
                    currentUser.setId(userId);

                    return;
                } else {
                    ui.displayMsg(bold + "❌Incorrect password. Please try again. ☠\uFE0F");
                }
            } else {
                ui.displayMsg(bold + "❌Username not found. Please try again or sign up! \uD83E\uDD37\u200D♂\uFE0F");
            }
            startMenu();
        } catch (SQLException e) {
            ui.displayMsg(bold + "\uD83E\uDD26\u200D♂\uFE0F An error occurred during login: " + e.getMessage());
        }
    }

    public User registerUser() {
        String username = ui.promptText("\uD83D\uDC68\u200D\uD83D\uDCBB Enter your username:");
        boolean usernameExists = false;

        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    usernameExists = rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (usernameExists) {
            System.out.println("❌Username already exists. Please choose another.");
            registerUser();
        } else {
            System.out.println("✅Username is available!");
        }
        String password = ui.promptText("\uD83E\uDEE3 Enter your password:");
        int age = ui.promptNumeric("\uD83D\uDC74 \uD83D\uDC75 Enter your age:");
        String gender = ui.promptText("\uD83D\uDC71\u200D♂\uFE0F\uD83D\uDC71\u200D♀\uFE0F Enter your gender (Male/Female): ");
        int height = ui.promptNumeric("\uD83D\uDCCF Enter your height in cm:");
        double weight = ui.promptNumeric("⚖\uFE0F Enter your weight in kg:");

        User user = new User(username, password, age, gender, height, weight);

        DatabaseHandler.saveUser(user);

        String sql1 = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql1)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
        } catch (SQLException e) {
            ui.displayMsg("❌Error retrieving user ID after registration: " + e.getMessage());
        }
        ui.displayMsg("✅User registered successfully!");
        mainMenu();
        return user;
    }

    public void viewCurrentChallenges() {

        ArrayList<Challenge> challenges = currentUser.getCurrentChallenges();

        if (challenges.isEmpty()) {
            ui.displayMsg("❌You have no active challenges.");
        } else {
            ui.displayMsg("Your active challenges:");
            for (Challenge challenge : challenges) {
                ui.displayMsg(challenge.toString());
            }
        }
    }

    public void addRun(){

        int hours = ui.promptNumeric("\uD83D\uDD50Enter hours: ");
        int minutes = ui.promptNumeric("\uD83D\uDD51Enter minutes: ");
        int seconds = ui.promptNumeric("\uD83D\uDD52Enter seconds: ");
        String date = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        while (true) {
            date = ui.promptText("\uD83D\uDDD3\uFE0FPress Enter for today's date or enter the date of the run (dd/mm/yy): ");
            if (date == null || date.isEmpty()) {
                date = LocalDate.now().format(formatter);
                ui.displayMsg("Using today's date: " + date);
                break;
            } else {
                try {
                    LocalDate.parse(date, formatter);
                    break;
                } catch (DateTimeParseException e) {
                    ui.displayMsg("❌Invalid date format. Please use dd/mm/yy.");
                }
            }
        }
        float distance = ui.promptNumeric("\uD83D\uDCCFEnter the distance in meters:");
        int totalMin = hours*60 + minutes;
        DatabaseHandler.updateDistanceGoals(currentUser,distance);
        DatabaseHandler.updateTimeGoals(currentUser, totalMin);
        DatabaseHandler.updateDualGoals(currentUser, totalMin, distance);
        Run run = new Run(hours, minutes, seconds, distance, date);
        DatabaseHandler.saveRun(currentUser, run);
        ui.displayMsg("You just added the run " + run + " to your running log. Good work!\uD83E\uDD29\uD83D\uDC4C");
        ui.displayMsg(achievement.checkRunDistance(distance));
    }

    public void logOut(){
        userLogin();
        mainMenu();
    }

    public void exitProgram(){
        System.exit(0);
    }
}