public class Achievement {
     private int[] achievements = {5000, 10000, 15000, 21100, 42200};

    public Achievement(){

    }

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
