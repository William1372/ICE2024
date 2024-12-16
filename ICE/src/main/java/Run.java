public class Run {
    private int hours;
    private int minutes;
    private int seconds;
    private float distance;
    private String date;

    public Run(int hours, int minutes, int seconds, float distance, String date){
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.distance = distance;
        this.date = date;
    }

    public Run(int hours, int minutes, int seconds, float distance){
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.distance = distance;
    }

    public String toString(){
        if (!date.equals(""))
            return hours + ":" + minutes + ":" + seconds + ", " + distance + " meters" + ", " + date;
        return hours + ":" + minutes + ":" + seconds + ", " + distance + " meters";
    }

    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public double getDistance() {
        return distance;
    }

    public String getDate() {
        return date;
    }

}
