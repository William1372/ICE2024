import java.util.HashMap;

public class Achievement {
   // private HashMap <String, Float> achievements;

     private int[] achievements = {5000, 10000, 15000, 21100, 42200};

    public Achievement(){
       // this.achievements = new HashMap<String, Float>();
    }


    //TODO tilføj metode der gemmer achievements i DB, tilknyttet brugeren
    //TODO tilføj case i switch til achievements, samt metode der henter dem fra DB
    //TODO tilføje metode der tjekker duration eller pace fx.

/*
    public void fillAchievements (){
        achievements.put("5KM", 5000F);
        achievements.put("10KM", 10000F);
        achievements.put("15km", 15000F);
        achievements.put("Half Marathon", 21100F);
        achievements.put("Marathon", 42200F);
    }
 */


    public String checkRunDistance (float distance) {
        String msg = "";
        for (int achievement : achievements) {
            if (distance >= achievement) {
                msg = "Achievement unlocked: " + achievement + " meters ran, congratulations!!!!";
            }
        }
        return msg;
    }
}
