public class Goal {
    float distance;
    float progress;
    int minutes;

    public Goal(float distance, float progress) {
        this.distance = distance;
        this.progress = progress;
    }

    public Goal(float distance, int minutes, float progress) {
        this.distance = distance;
        this.progress = progress;
        this.minutes = minutes;
    }

    public Goal(int minutes, int progress) {
        this.minutes = minutes;
        this.progress = progress;
    }

    @Override
    public String toString() {
        if (minutes == 0) {
            return progress + " km out of " + distance + " km";
        } else if (distance == 0)
            return (int) progress + " min out " + minutes + " min";
            return (int) progress + " min out of " + minutes + " min for " + distance + " m";
        }


    public float getDistance() {
        return  distance;
    }

    public int getMinutes() {
        return minutes;
    }

    public float getProgress() {
        return progress;
    }
}