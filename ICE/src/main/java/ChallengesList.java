import java.util.ArrayList;
import java.util.List;

public class ChallengesList {
    private static ArrayList<Challenge> AllChallengesList;
    private TextUI ui;

    public ChallengesList() {
        this.AllChallengesList = new ArrayList<>();
    }


    public static void viewAllChallenges() {

    }

    public List<Challenge> getAllChallengesList(){
        return AllChallengesList;
    }
}

